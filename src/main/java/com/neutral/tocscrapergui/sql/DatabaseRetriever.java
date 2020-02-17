package com.neutral.tocscrapergui.sql;

import com.neutral.tocscrapermodels.ChapterGroupBuilder;
import com.neutral.tocscrapermodels.Novel;
import com.neutral.tocscrapermodels.NovelBuilder;
import com.neutral.tocscrapermodels.NovelContainer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import static com.neutral.tocscrapergui.App.LOGGER;

/**
 *
 * @author Mr.Neutral
 */
public class DatabaseRetriever {

    private static Connection conn;
    private final String novelIdTable = " novel_id ";
    private final String novelChapterGroupsTable = " novel_chapter_groups ";
    private final String novelStatusTable = " novel_status ";
    private final String idAtNovelId = " novel_id._id ";
    private final String titleAtNovelId = " novel_id.title ";
    private final String idAtNovelChapterGroups = " novel_chapter_groups._id ";
    private final String novelIdAtNovelChapterGroups = " novel_chapter_groups.novel_id ";
    private final String startAtNovelChapterGroups = " novel_chapter_groups.start ";
    private final String endAtNovelChapterGroups = " novel_chapter_groups.end ";
    private final String linkAtNovelChapterGroups = " novel_chapter_groups.link ";
    private final String idAtNovelStatusTable = " novel_status._id ";
    private final String statusAtNovelStatusTable = " novel_status.status ";
    private final int novelIdIndex = 1;
    private final int novelTitleIndex = 2;
    private final int chapterGroupLinkIndex = 3;
    private final int chapterGroupStartIndex = 4;
    private final int chapterGroupEndIndex = 5;
    private final int novelStatusIndex = 6;
    private final NovelBuilder novelBuilder = new NovelBuilder();
    private final ChapterGroupBuilder chapterGroupBuilder = new ChapterGroupBuilder();
    private final String query
            = "SELECT\n"
            + "    " + idAtNovelId + "AS novel_id,\n"
            + "    " + titleAtNovelId + "AS novel_title,\n"
            + "    " + linkAtNovelChapterGroups + "AS chapter_group_link,\n"
            + "    " + startAtNovelChapterGroups + "AS chapter_group_start,\n"
            + "    " + endAtNovelChapterGroups + "AS chapter_group_end,\n"
            + "    " + statusAtNovelStatusTable + "AS novel_status\n"
            + "FROM\n"
            + "    " + novelIdTable + "\n"
            + "INNER JOIN" + novelChapterGroupsTable + "ON" + idAtNovelId + "=" + novelIdAtNovelChapterGroups + "\n"
            + "INNER JOIN" + novelStatusTable + "ON" + idAtNovelId + "=" + idAtNovelStatusTable + "\n"
            + "ORDER BY\n"
            + titleAtNovelId;

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
            conn = DriverManager.getConnection("jdbc:mysql://" + dbLink + "?rewriteBatchedStatements=true", dbUser, dbPass);
            LOGGER.log(Level.FINER, "Connection to DB established");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
            System.exit(1);
        }
    }

    public ObservableList<Novel> getNovels() {
        NovelContainer dbNovels = new NovelContainer();
        try (Statement statement = conn.createStatement()) {
            ResultSet results = statement.executeQuery(query);
            populateContainer(dbNovels, results);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            System.exit(1);
        }

        return FXCollections.observableArrayList(dbNovels.asList());
    }

    private void populateContainer(NovelContainer dbNovels, ResultSet results) throws SQLException {
        while (results.next()) {
            if (!dbNovels.contains(results.getString(novelTitleIndex))) {
                dbNovels.add(
                        novelBuilder
                                .setId(results.getString(novelIdIndex))
                                .setTitle(results.getString(novelTitleIndex))
                                .setStatus(Novel.NovelStatus.from(results.getString(novelStatusIndex)))
                                .createNovel()
                );
                LOGGER.log(Level.FINEST, "{0}: Retrieved", results.getString(novelTitleIndex));
            }
            dbNovels.getNovelByTitle(results.getString(novelTitleIndex)).getChapters()
                    .add(chapterGroupBuilder
                            .setStart(results.getInt(chapterGroupStartIndex))
                            .setEnd(results.getInt(chapterGroupEndIndex))
                            .setLink(results.getString(chapterGroupLinkIndex))
                            .setNovel(dbNovels.getNovelByTitle(results.getString(novelTitleIndex)))
                            .createChapter()
                    );
        }
    }
}
