package com.neutral.tocscrapergui.sql;

import static com.neutral.tocscrapergui.App.LOGGER;
import com.neutral.tocscrapergui.models.Chapter;
import com.neutral.tocscrapergui.models.Novel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Mr.Neutral
 */
public class Database {

    private Connection conn;
    private int novelIdIndex = 1;
    private int novelNameIndex = 2;
    private int chapterIdIndex = 3;
    private int chapterNameIndex = 4;
    private int chapterLinkIndex = 5;

    public Database() {
        try {
            String dbURL = "postgres://vzlhdaaoeimggi:217bae05dc3739dd87b12b9562db2208e707dee3b77b21d24c6cf5b9c77acc19@ec2-46-137-187-23.eu-west-1.compute.amazonaws.com:5432/df35qul49481sv";
            String[] db = dbURL.split("//")[1].split("@");
            String dbUser = db[0].split(":")[0];
            String dbPass = db[0].split(":")[1];
            String dbLink = db[1];
            conn = DriverManager.getConnection("jdbc:postgresql://" + dbLink + "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory", dbUser, dbPass);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }
    }

    public ObservableList<Novel> getNovels() {
        Map<String, Novel> novels = new HashMap<>();
        try (Statement statement = conn.createStatement();
                ResultSet results = statement.executeQuery("SELECT * FROM novels")) {

            while (results.next()) {
                
                if (!novels.containsKey(results.getString(novelIdIndex))) {
                    Novel novel = new Novel(results.getString(novelIdIndex), results.getString(novelNameIndex).contains("(Completed)"), results.getString(novelNameIndex), new ArrayList<Chapter>());
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
