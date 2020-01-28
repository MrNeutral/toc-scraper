package com.neutral.tocscraper.sql;

import com.neutral.tocscraper.Scraper;
import com.neutral.tocscrapermodels.ChapterGroup;
import com.neutral.tocscrapermodels.ChapterGroupBuilder;
import com.neutral.tocscrapermodels.ChapterGroupContainer;
import com.neutral.tocscrapermodels.Novel;
import com.neutral.tocscrapermodels.Novel.NovelStatus;
import com.neutral.tocscrapermodels.NovelBuilder;
import com.neutral.tocscrapermodels.NovelContainer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
    private PreparedStatement updateStatusStatement;
    private PreparedStatement updateChapterGroupLinkStatement;
    private PreparedStatement updateChapterGroupRangeStatement;
    private PreparedStatement insertNovelStatement;
    private PreparedStatement insertNovelStatusStatement;
    private PreparedStatement insertChapterGroupStatement;
    private PreparedStatement deleteChapterGroupStatement;

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

    public Database() {
        try {
//            String[] db = System.getenv("DATABASE_URL").split("//")[1].split("@");
//            String dbUser = db[0].split(":")[0];
//            String dbPass = db[0].split(":")[1];
//            String dbLink = db[1];
            String dbUser = "WAOOiOHHaC";
            String dbPass = "GWz5r06J31";
            String dbLink = "remotemysql.com:3306/WAOOiOHHaC";
            conn = DriverManager.getConnection("jdbc:mysql://" + dbLink + "?rewriteBatchedStatements=true", dbUser, dbPass);
            conn.setAutoCommit(false);

            updateStatusStatement = conn.prepareStatement(
                    "UPDATE"
                    + novelStatusTable
                    + "SET"
                    + statusAtNovelStatusTable
                    + "= ? WHERE" + idAtNovelStatusTable + "= ?");
            updateChapterGroupLinkStatement = conn.prepareStatement(
                    "UPDATE"
                    + novelChapterGroupsTable
                    + "SET"
                    + linkAtNovelChapterGroups
                    + "= ? WHERE link = ?");
            updateChapterGroupRangeStatement = conn.prepareStatement(
                    "UPDATE"
                    + novelChapterGroupsTable
                    + "SET"
                    + startAtNovelChapterGroups
                    + "= ?,"
                    + endAtNovelChapterGroups
                    + "= ? WHERE link = ?");
            insertNovelStatement = conn.prepareStatement(
                    "INSERT INTO"
                    + novelIdTable
                    + "VALUES (?, ?)");
            insertNovelStatusStatement = conn.prepareStatement(
                    "INSERT INTO"
                    + novelStatusTable
                    + "VALUES (?, ?)");
            insertChapterGroupStatement = conn.prepareStatement(
                    "INSERT INTO"
                    + novelChapterGroupsTable
                    + "VALUES (?,?,?,?,?)");
            deleteChapterGroupStatement = conn.prepareStatement(
                    "DELETE FROM"
                    + novelChapterGroupsTable
                    + "WHERE"
                    + linkAtNovelChapterGroups
                    + "= ?");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void updateDB(NovelContainer novelsInMemory) {
        try (Statement statement = conn.createStatement()) {
            //Get novel_id, novel_name, chapter_link, chapter_title
            ResultSet results = statement.executeQuery(query);
            NovelContainer dbNovels = new NovelContainer();

            populateContainer(dbNovels, results);
            for (Novel novel : novelsInMemory) {
                if (dbNovels.contains(novel.getTitle())) {
                    if (dbNovels.getNovelByTitle(novel.getTitle()).getStatus() != novel.getStatus()) {
                        updateStatusStatement.setString(1, novel.getStatus().toString());
                        updateStatusStatement.setString(2, novel.getId());
                        updateStatusStatement.addBatch();
                        Scraper.LOGGER.log(Level.FINEST, "{0}: Novel status changed.", novel.getTitle());
                    } else {
                        Scraper.LOGGER.log(Level.FINEST, "{0} is already in the DB.", novel.getTitle());
                    }
                } else {
                    insertNovelStatement.setString(1, novel.getId());
                    insertNovelStatement.setString(2, novel.getTitle());
                    insertNovelStatement.addBatch();

                    insertNovelStatusStatement.setString(1, novel.getId());
                    insertNovelStatusStatement.setString(2, novel.getStatus().toString());
                    insertNovelStatusStatement.addBatch();
                }
                parseNovelChapters(dbNovels.getNovelByTitle(novel.getTitle()), novel);
            }
            checkRemovedNovels(dbNovels, novelsInMemory);
            executeStatements();
            dbNovels.clear();
            novelsInMemory.clear();
            checkChapterContinuity(dbNovels);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
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

    private void parseNovelChapters(Novel dbNovel, Novel novel) throws SQLException {
        if (dbNovel == null) {
            for (ChapterGroup chapterGroup : novel.getChapters()) {
                insertChapterGroupStatement.setString(1, chapterGroup.getId());
                insertChapterGroupStatement.setString(2, novel.getId());
                insertChapterGroupStatement.setInt(3, chapterGroup.getStart());
                insertChapterGroupStatement.setInt(4, chapterGroup.getEnd());
                insertChapterGroupStatement.setString(5, chapterGroup.getLink());
                insertChapterGroupStatement.addBatch();
            }
            return;
        }
        var dbChapters = dbNovel.getChapters();
        for (ChapterGroup chapterGroup : novel.getChapters()) {
            //if chapter group with same range exists
            if (dbChapters.contains(chapterGroup.getStart(), chapterGroup.getEnd())) {
                //if the link is the same
                if (dbChapters.getChapterGroupByRange(chapterGroup.getStart(), chapterGroup.getEnd()).getLink().equals(chapterGroup.getLink())) {
                    continue;
                }
                updateChapterGroupLinkStatement.setString(1, chapterGroup.getLink());
                updateChapterGroupLinkStatement.setString(2, dbChapters.getChapterGroupByRange(chapterGroup.getStart(), chapterGroup.getEnd()).getLink());
                updateChapterGroupLinkStatement.addBatch();

            } //if link is the same but the range is different
            else if (dbChapters.containsLink(chapterGroup.getLink())) {
                updateChapterGroupRangeStatement.setInt(1, chapterGroup.getStart());
                updateChapterGroupRangeStatement.setInt(2, chapterGroup.getEnd());
                updateChapterGroupRangeStatement.setString(3, chapterGroup.getLink());
                updateChapterGroupRangeStatement.addBatch();
            } //no chapter match 
            else {
                insertChapterGroupStatement.setString(1, chapterGroup.getId());
                insertChapterGroupStatement.setString(2, dbNovel.getId());
                insertChapterGroupStatement.setInt(3, chapterGroup.getStart());
                insertChapterGroupStatement.setInt(4, chapterGroup.getEnd());
                insertChapterGroupStatement.setString(5, chapterGroup.getLink());
                insertChapterGroupStatement.addBatch();
            }
        }
    }

    private void checkRemovedNovels(NovelContainer dbNovels, NovelContainer novelsInMemory) throws SQLException {
        for (Novel novel : dbNovels) {
            if (!novelsInMemory.contains(novel.getTitle())) {
                updateStatusStatement.setString(1, novel.getId());
                updateStatusStatement.setString(2, NovelStatus.UNAVAILABLE.toString());
                updateStatusStatement.addBatch();
            }
        }
    }

    private void executeStatements() throws SQLException {
        try {
            insertNovelStatement.executeBatch();
            insertNovelStatusStatement.executeBatch();
            insertChapterGroupStatement.executeBatch();
            updateStatusStatement.executeBatch();
            updateChapterGroupLinkStatement.executeBatch();
            updateChapterGroupRangeStatement.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void checkChapterContinuity(NovelContainer dbNovels) throws SQLException {
        try (Statement statement = conn.createStatement()) {
            ResultSet results = statement.executeQuery("SELECT\n"
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
                    + titleAtNovelId);
            populateContainer(dbNovels, results);

            for (Novel novel : dbNovels) {
                var chapterGroups = novel.getChapters();
                var current = chapterGroups.last();
                chapterGroups.removeAll(getLongestPath(chapterGroups, current));
                Scraper.LOGGER.log(Level.FINEST, "Cleaned chapters for {0}.", novel.getTitle());
                for (ChapterGroup chapterGroup : chapterGroups) {
                    deleteChapterGroupStatement.setString(1, chapterGroup.getLink());
                    deleteChapterGroupStatement.addBatch();
                }
            }
            deleteChapterGroupStatement.executeBatch();
            conn.commit();
        }
    }

    private ChapterGroupContainer getLongestPath(ChapterGroupContainer chapterGroups, ChapterGroup current) {
        if (chapterGroups.size() == 1) {
            return new ChapterGroupContainer(current);
        }
        int paths = chapterGroups.getChapterGroupsByEnd(current.getStart() - 1).size();
        while (paths == 0) {
            if (current == chapterGroups.last()) {
                current = chapterGroups.lower(current);
                paths = chapterGroups.getChapterGroupsByEnd(current.getStart() - 1).size();
            } else if (current == chapterGroups.lower(chapterGroups.last()) && chapterGroups.size() == 2) {
                return new ChapterGroupContainer(current);
            } else {
                break;
            }
        }
        //for each of those chapters with the same end
        List<ChapterGroupContainer> allPaths = new ArrayList<>();
        var root = current;

        for (int i = 0; i < paths; ++i) {
            current = root;
            ChapterGroupContainer toKeep = new ChapterGroupContainer();
            if (current == chapterGroups.last()
                    || (current == chapterGroups.lower(chapterGroups.last())
                    && chapterGroups.getChapterGroupsByEnd(chapterGroups.last().getEnd()).size() > 1)) {
                toKeep.add(current);
            }
            if (current.getStart() == current.getEnd()) {
                toKeep.add(current);
            }
            //iterate backwards
            current = chapterGroups.getChapterGroupsByEnd(current.getStart() - 1).get(i);
            while (true) {
                toKeep.add(current);
                if (chapterGroups.getChapterGroupsByEnd(current.getStart() - 1).size() > 1) {
                    toKeep.addAll(getLongestPath(chapterGroups, current));
                    current = toKeep.first();
                } else if (chapterGroups.getSingularChapterGroupByEnd(current.getStart() - 1) != null) {
                    current = chapterGroups.getSingularChapterGroupByEnd(current.getStart() - 1);
                } else {
                    //add chain to allPaths
                    allPaths.add(toKeep);
                    break;
                }
            }
        }
        return allPaths.stream().max((o1, o2) -> {
            return Integer.compare(o1.size(), o2.size());
        }).get();
    }

}
