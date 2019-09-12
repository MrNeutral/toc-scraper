package com.neutral.tocscrapergui.sql;

import static com.neutral.tocscrapergui.App.LOGGER;
import com.neutral.tocscrapermodels.Chapter;
import com.neutral.tocscrapermodels.ChapterContainer;
import com.neutral.tocscrapermodels.Novel;
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

/**
 *
 * @author Mr.Neutral
 */
public class DatabaseRetriever {

    private Connection conn;
    private int novelIdIndex = 1;
    private int novelNameIndex = 2;
    private int chapterIdIndex = 3;
    private int chapterNameIndex = 4;
    private int chapterLinkIndex = 5;

    public DatabaseRetriever() {
        try {
//            String dbURL = "postgres://vzlhdaaoeimggi:217bae05dc3739dd87b12b9562db2208e707dee3b77b21d24c6cf5b9c77acc19@ec2-46-137-187-23.eu-west-1.compute.amazonaws.com:5432/df35qul49481sv";
//            String[] db = dbURL.split("//")[1].split("@");
//            String dbUser = db[0].split(":")[0];
//            String dbPass = db[0].split(":")[1];
//            String dbLink = db[1];
            String dbUser = "Iwkn0Ill0U";
            String dbPass = "2FVdgXV3H9";
            String dbLink = "remotemysql.com:3306/Iwkn0Ill0U";
            conn = DriverManager.getConnection("jdbc:mysql://" + dbLink, dbUser, dbPass);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
            System.exit(1);
        }
    }

    public ObservableList<Novel> getNovels() {
        Map<String, Novel> novels = new HashMap<>();
        try (Statement statement = conn.createStatement();
                ResultSet results = statement.executeQuery(
                        "SELECT\n"
                        + "    novel_id._id AS novel_id,\n"
                        + "    novel_id.name AS novel_name,\n"
                        + "    novel_chapters._id AS chapter_id,\n"
                        + "    novel_chapters.title AS chapter_title,\n"
                        + "    novel_chapters.link AS chapter_link\n"
                        + "FROM\n"
                        + "    novel_id\n"
                        + "INNER JOIN novel_chapters ON novel_id._id = novel_chapters.novel_id\n"
                        + "ORDER BY\n"
                        + "    novel_id.name")) {

            while (results.next()) {

                if (!novels.containsKey(results.getString(novelIdIndex))) {
                    Novel novel = new Novel(results.getString(novelIdIndex), results.getString(novelNameIndex).contains("(Completed)"), results.getString(novelNameIndex), new ChapterContainer());
                    novels.put(results.getString(novelIdIndex), novel);
                    LOGGER.log(Level.FINEST, "{0} added", results.getString(novelNameIndex));
                }

                Novel novel = novels.get(results.getString(novelIdIndex));
                novel.getChapters().add(new Chapter(results.getString(chapterIdIndex), novel, results.getString(chapterNameIndex), results.getString(chapterLinkIndex)));
                LOGGER.log(Level.FINEST, "Chapter {0}  of {1} added", new Object[]{results.getString(chapterNameIndex), novel.getTitle()});
            }

            LOGGER.log(Level.FINE, "DB downloaded. Novels parsed.");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }

        return FXCollections.observableArrayList(novels.values());
    }

}
