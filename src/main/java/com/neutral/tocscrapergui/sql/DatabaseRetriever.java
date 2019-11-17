package com.neutral.tocscrapergui.sql;

import com.neutral.tocscrapermodels.Chapter;
import com.neutral.tocscrapermodels.ChapterContainer;
import com.neutral.tocscrapermodels.Novel;
import static com.neutral.tocscrapermodels.Novel.parseStatus;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import static com.neutral.tocscrapergui.App.logger;

/**
 *
 * @author Mr.Neutral
 */
public class DatabaseRetriever {

    private static Connection conn;
    private static final int NOVEL_ID_INDEX = 1;
    private static final int NOVEL_TITLE_INDEX = 2;
    private static final int CHAPTER_ID_INDEX = 3;
    private static final int CHAPTER_LINK_INDEX = 4;
    private static final int CHAPTER_TITLE_INDEX = 5;
    private static final int NOVEL_STATUS_INDEX = 6;
    private static final String NOVEL_ID_TABLE = " novel_id ";
    private static final String NOVEL_CHAPTERS_TABLE = " novel_chapters ";
    private static final String NOVEL_STATUS_TABLE = " novel_status ";
    private static final String ID_AT_NOVEL_ID = " novel_id._id ";
    private static final String NAME_AT_NOVEL_ID = " novel_id.name ";
    private static final String ID_AT_NOVEL_CHAPTERS = " novel_chapters._id ";
    private static final String NOVEL_ID_AT_NOVEL_CHAPTERS = " novel_chapters.novel_id ";
    private static final String TITLE_AT_NOVEL_CHAPTERS = " novel_chapters.title ";
    private static final String LINK_AT_NOVEL_CHAPTERS = " novel_chapters.link ";
    private static final String ID_AT_NOVEL_STATUS_TABLE = " novel_status._id ";
    private static final String STATUS_AT_NOVEL_STATUS_TABLE = " novel_status.status ";
    private static final String BASE_QUERY
            = "SELECT\n"
            + "    " + ID_AT_NOVEL_ID + "AS novel_id,\n"
            + "    " + NAME_AT_NOVEL_ID + "AS novel_name,\n"
            + "    " + ID_AT_NOVEL_CHAPTERS + "AS chapter_id,\n"
            + "    " + LINK_AT_NOVEL_CHAPTERS + "AS chapter_link,\n"
            + "    " + TITLE_AT_NOVEL_CHAPTERS + "AS chapter_title,\n"
            + "    " + STATUS_AT_NOVEL_STATUS_TABLE + "AS novel_status\n"
            + "FROM\n"
            + "    " + NOVEL_ID_TABLE + "\n"
            + "INNER JOIN" + NOVEL_CHAPTERS_TABLE + "ON" + ID_AT_NOVEL_ID + "=" + NOVEL_ID_AT_NOVEL_CHAPTERS + "\n"
            + "INNER JOIN" + NOVEL_STATUS_TABLE + "ON" + ID_AT_NOVEL_ID + "=" + ID_AT_NOVEL_STATUS_TABLE + "\n"
            + "ORDER BY\n"
            + NAME_AT_NOVEL_ID;

    public DatabaseRetriever() {
        try {
//            String dbURL = "postgres://vzlhdaaoeimggi:217bae05dc3739dd87b12b9562db2208e707dee3b77b21d24c6cf5b9c77acc19@ec2-46-137-187-23.eu-west-1.compute.amazonaws.com:5432/df35qul49481sv";
//            String[] db = dbURL.split("//")[1].split("@");
//            String dbUser = db[0].split(":")[0];
//            String dbPass = db[0].split(":")[1];
//            String dbLink = db[1];
            String dbUser = "WAOOiOHHaC";
            String dbPass = "GWz5r06J31";
            String dbLink = "remotemysql.com:3306/WAOOiOHHaC";
            conn = DriverManager.getConnection("jdbc:mysql://" + dbLink, dbUser, dbPass);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.toString(), e);
            System.exit(1);
        }
    }

    public ObservableList<Novel> getNovels() {
        Map<String, Novel> novels = new HashMap<>();
        try (Statement statement = conn.createStatement();
                ResultSet results = statement.executeQuery(BASE_QUERY)) {

            while (results.next()) {

                if (!novels.containsKey(results.getString(NOVEL_ID_INDEX))) {
                    Novel novel = new Novel(results.getString(NOVEL_ID_INDEX), parseStatus(results.getString(NOVEL_STATUS_INDEX).toUpperCase()), results.getString(NOVEL_TITLE_INDEX), new ChapterContainer());
                    novels.put(results.getString(NOVEL_ID_INDEX), novel);
                    logger.log(Level.FINEST, "{0} added", results.getString(NOVEL_TITLE_INDEX));
                }

                Novel novel = novels.get(results.getString(NOVEL_ID_INDEX));
                novel.getChapters().add(new Chapter(results.getString(CHAPTER_ID_INDEX), novel, results.getString(CHAPTER_TITLE_INDEX), results.getString(CHAPTER_LINK_INDEX)));
                logger.log(Level.FINEST, "Chapter {0}  of {1} added", new Object[]{results.getString(CHAPTER_TITLE_INDEX), novel.getTitle()});
            }

            logger.log(Level.FINE, "DB downloaded. Novels parsed.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.toString(), e);
        }

        return FXCollections.observableArrayList(novels.values());
    }

}
