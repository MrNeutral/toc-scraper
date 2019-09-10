package com.neutral.tocscraper.sql;

import com.neutral.tocscraper.Scraper;
import com.neutral.tocscraper.models.Chapter;
import com.neutral.tocscraper.models.Novel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 *
 * @author Mr.Neutral
 */
public class Database {

    private Connection conn;
    private String novelIdTable = " novel_id ";
    private String novelChaptersTable = " novel_chapters ";
    private String novelId = " novel_id.'_id' ";
    private String novelName = " novel_id.name ";
    private String novelNovel = " novel_chapters.novel ";
    private String novelChapter = " novel_chapters.chapter ";
    private String novelLink = " novel_chapters.link ";

    public Database() {
        try {
            String[] db = System.getenv("DATABASE_URL").split("//")[1].split("@");
            String dbUser = db[0].split(":")[0];
            String dbPass = db[0].split(":")[1];
            String dbLink = db[1];
            conn = DriverManager.getConnection("jdbc:postgresql://" + dbLink + "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory", dbUser, dbPass);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateDB(List<Novel> novels) {
        try (Statement statement = conn.createStatement(); ResultSet results = statement.executeQuery("SELECT novel_id, chapter_id FROM novels");) {

            List<String> novelIds = new ArrayList<>();
            List<String> chapterIds = new ArrayList<>();
            while (results.next()) {
                novelIds.add(results.getString(1));
                chapterIds.add(results.getString(2));
            }

            conn.setAutoCommit(false);
            for (Novel novel : novels) {
                if (!novelIds.contains(novel.getId())) {
                    statement.addBatch("INSERT INTO"
                            + novelIdTable
                            + "VALUES ('"
                            + novel.getId() + "', E'"
                            + novel.getTitle() + "')");
                    Scraper.LOGGER.log(Level.FINEST, "{0} added to queue.", novel.getTitle());
                } else {
                    Scraper.LOGGER.log(Level.FINEST, "{0} is already in the DB.", novel.getTitle());
                }
                for (Chapter chapter : novel.getChapters()) {
                    if (!chapterIds.contains(chapter.getId())) {
                        statement.addBatch("INSERT INTO"
                                + novelChaptersTable
                                + "VALUES ('"
                                + chapter.getId() + "','"
                                + novel.getId() + "','"
                                + chapter.getChapters() + "','"
                                + chapter.getLink() + "')");

                        Scraper.LOGGER.log(Level.FINEST, "{0}: {1} added to queue.", new Object[]{novel.getTitle(), chapter.getChapters()});
                    } else {
                        Scraper.LOGGER.log(Level.FINEST, "{0}: {1} is already in the DB.", new Object[]{novel.getTitle(), chapter.getChapters()});
                    }
                }
            }
            statement.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

//    public void addChapters(List<Novel> novels, Chapter chapter) {
//        try (Statement statement = conn.createStatement()) {
//            statement.execute("INSERT INTO"
//                    + novelChaptersTable
//                    + "VALUES ('"
//                    + chapter.getId() + "','"
//                    + novel.getId() + "','"
//                    + chapter.getChapters() + "','"
//                    + chapter.getLink() + "')"
//            );
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//            e.printStackTrace();
//        }
//    }
}
