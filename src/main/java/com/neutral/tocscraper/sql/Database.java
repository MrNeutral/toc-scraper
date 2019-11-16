package com.neutral.tocscraper.sql;

import com.neutral.tocscraper.Scraper;
import com.neutral.tocscrapermodels.Chapter;
import com.neutral.tocscrapermodels.ChapterContainer;
import com.neutral.tocscrapermodels.DBRow;
import com.neutral.tocscrapermodels.Novel;
import static com.neutral.tocscrapermodels.Novel.removeExtra;
import com.neutral.tocscrapermodels.NovelContainer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.apache.commons.text.StringEscapeUtils;

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
    private String novelChapter = " novel_chapters.title ";
    private String novelLink = " novel_chapters.link ";

    public Database() {
        try {
//            String[] db = System.getenv("DATABASE_URL").split("//")[1].split("@");
//            String dbUser = db[0].split(":")[0];
//            String dbPass = db[0].split(":")[1];
//            String dbLink = db[1];
            String dbUser = "WAOOiOHHaC";
            String dbPass = "GWz5r06J31";
            String dbLink = "remotemysql.com:3306/WAOOiOHHaC";
            conn = DriverManager.getConnection("jdbc:mysql://" + dbLink, dbUser, dbPass);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void updateDB(NovelContainer novelsInMemory) {
        try (Statement statement = conn.createStatement();) {
            //Get novel_id, novel_name, chapter_link, chapter_title
            ResultSet results = statement.executeQuery(
                    "SELECT\n"
                    + "    novel_id._id AS novel_id,\n"
                    + "    novel_id.name AS novel_name,\n"
                    + "    novel_chapters.link AS chapter_link,\n"
                    + "    novel_chapters.title AS chapter_title\n"
                    + "FROM\n"
                    + "    novel_id\n"
                    + "INNER JOIN novel_chapters ON novel_id._id = novel_chapters.novel_id\n"
                    + "ORDER BY\n"
                    + "    novel_id.name");
            List<DBRow> rows = new ArrayList<>();

            //Create a DBRow entity for every DB row
            while (results.next()) {
                rows.add(new DBRow(results.getString(1), StringEscapeUtils.escapeEcmaScript(results.getString(2)), results.getString(3), results.getString(4)));
            }

            if (!results.isClosed()) {
                results.close();
            }
            conn.setAutoCommit(false);

            NovelContainer novelsToBeUpdated = new NovelContainer();
            ChapterContainer chaptersToBeUpdated = new ChapterContainer();

            //Iterate through every row in the DB
            for (var row : rows) {
                //If the database contains a scraped novel
                if (novelsInMemory.contains(row.getNovelTitle())) {
                    Scraper.LOGGER.log(Level.FINEST, "{0} is already in the DB.", row.getNovelTitle());
                } //If the database contains an incomplete scraped novel
                else if (novelsInMemory.contains(row.getNovelTitle().trim() + " (Completed)")
                        && !novelsToBeUpdated.contains(novelsInMemory.getNovelByName(row.getNovelTitle().trim() + " (Completed)").getTitle())) {
                    statement.addBatch("UPDATE novel_id SET name = '"
                            + novelsInMemory.getNovelByName(row.getNovelTitle().trim() + " (Completed)")
                            + "' WHERE _id ='"
                            + row.getNovelId() + "'");
                    novelsToBeUpdated.add(novelsInMemory.getNovelByName(row.getNovelTitle().trim() + " (Completed)"));
                    Scraper.LOGGER.log(Level.FINEST, "{0} Novel completed, updated title.", row.getNovelTitle());
                } //If the database contains an unsuspended scraped novel
                else if (novelsInMemory.contains(row.getNovelTitle().trim() + " (Suspend)")
                        && !novelsToBeUpdated.contains(novelsInMemory.getNovelByName(row.getNovelTitle().trim() + " (Suspend)").getTitle())) {
                   statement.addBatch("UPDATE novel_id SET name = '"
                            + novelsInMemory.getNovelByName(row.getNovelTitle().trim() + " (Suspend)")
                            + "' WHERE _id ='"
                            + row.getNovelId() + "'");
                    novelsToBeUpdated.add(novelsInMemory.getNovelByName(row.getNovelTitle().trim() + " (Suspend)"));
                    Scraper.LOGGER.log(Level.FINEST, "{0} Novel suspended, updated title.", row.getNovelTitle());
                } //If the database doesn't contains a novel
                else {
                    continue;
                }

                //Get all scraped chapters of the novel with the current title, ignoring completion status
                ChapterContainer chaptersInMemory = novelsInMemory.getNovelByNameIgnoreStatus(row.getNovelTitle()).getChapters();
                //If scraped chapters of this novel contain this specific chapter
                if (chaptersInMemory.contains(row.getChapterTitle())) {
                    //If the scraped chapter link is the same as the DB link
                    if (chaptersInMemory.getChapterByTitle(row.getChapterTitle()).getLink().equals(row.getChapterLink())) {
                        Scraper.LOGGER.log(Level.FINEST, "{0}: {1} is already in the DB.", new Object[]{row.getNovelTitle(), row.getChapterTitle()});
                    } //If the title is the same but the link is different
                    else if (!chaptersToBeUpdated.containsLink(chaptersInMemory.getChapterByTitle(row.getChapterTitle()).getLink())) {
                        statement.addBatch("UPDATE novel_chapters SET link = '"
                                + chaptersInMemory.getChapterByTitle(row.getChapterTitle()).getLink() + "'"
                                + "WHERE link = '"
                                + row.getChapterLink() + "'");
                        chaptersToBeUpdated.add(chaptersInMemory.getChapterByTitle(row.getChapterTitle()));
                        Scraper.LOGGER.log(Level.FINEST, "{0}: {1} The link was queued to be updated.", new Object[]{row.getNovelTitle(), row.getChapterTitle()});
                    }
                } //If the digits are the same as the scraped chapter but the link is different
                else if (chaptersInMemory.contains(Integer.valueOf(row.getChapterTitle().split("-")[0].trim()))
                        && !chaptersToBeUpdated.containsLink(chaptersInMemory.getChapterByDigits(Integer.valueOf(row.getChapterTitle().split("-")[0].trim())).getLink())) {
                    Chapter chapter = chaptersInMemory.
                            getChapterByDigits(Integer.valueOf(row.getChapterTitle().split("-")[0].trim()));
                    statement.addBatch("UPDATE novel_chapters SET link = '"
                            + chapter.getLink() + "', "
                            + "title = '"
                            + chapter.getTitle()
                            + "' WHERE novel_id = '"
                            + row.getNovelId() + "' AND "
                            + "title = '"
                            + row.getChapterTitle() + "'");
                    chaptersInMemory.getChapterByTitle(row.getChapterTitle().split("-")[0].trim());
                    Scraper.LOGGER.log(Level.FINEST, "{0}: {1} ({2}) The link was queued to be updated.", new Object[]{row.getNovelTitle(), chapter.getTitle(), chapter.getLink()});

                }
            }

            statement.executeBatch();
            conn.commit();

            results = statement.executeQuery(
                    "SELECT\n"
                    + "    novel_id._id AS novel_id,\n"
                    + "    novel_id.name AS novel_name,\n"
                    + "    novel_chapters.link AS chapter_link,\n"
                    + "    novel_chapters.title AS chapter_title\n"
                    + "FROM\n"
                    + "    novel_id\n"
                    + "INNER JOIN novel_chapters ON novel_id._id = novel_chapters.novel_id\n"
                    + "ORDER BY\n"
                    + "    novel_id.name");

            rows.clear();
            while (results.next()) {
                rows.add(new DBRow(results.getString(1), StringEscapeUtils.escapeEcmaScript(results.getString(2)), results.getString(3), results.getString(4)));
            }

            if (!results.isClosed()) {
                results.close();
            }

            List<String> dbNovelTitles = new ArrayList<>();
            ChapterContainer novelChapters = new ChapterContainer();
            rows.forEach(row -> dbNovelTitles.add(removeExtra(row.getNovelTitle())));

            for (Novel novel : novelsInMemory) {
                novelChapters.clear();
                //If database doesn't contain a novel with this base title, insert it
                if (!dbNovelTitles.contains(novel.getBaseTitle())) {
                    statement.addBatch("INSERT INTO"
                            + novelIdTable
                            + "VALUES ('"
                            + novel.getId() + "', '"
                            + novel.getTitle() + "')");
                    Scraper.LOGGER.log(Level.FINEST, "{0} added to the queue.", novel.getTitle());
                }

                for (var row : rows) {
                    if (removeExtra(row.getNovelTitle()).equals(novel.getBaseTitle())) {
                        if (row.getNovelTitle().contains("Trafford")) {
                            System.out.println("");
                        }
                        novelChapters.add(new Chapter(row.getChapterTitle(), row.getChapterLink()));
                    }
                }

                for (Chapter chapter : novel.getChapters()) {
                    if (!novelChapters.containsLink(chapter.getLink())) {
                        statement.addBatch("INSERT INTO"
                                + novelChaptersTable
                                + "VALUES ('"
                                + chapter.getId() + "','"
                                + novel.getId() + "','"
                                + chapter.getTitle() + "','"
                                + chapter.getLink() + "')"
                        );
                        Scraper.LOGGER.log(Level.FINEST, "{0}: {1} ({2})The link was added to the queue.", new Object[]{novel.getTitle(), chapter.getTitle(), chapter.getLink()});

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

}
