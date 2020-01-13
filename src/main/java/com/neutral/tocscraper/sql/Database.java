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
import org.apache.commons.text.StringEscapeUtils;

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
//            preparedStatements.addAll(List.of(updateStatusStatement, updateChapterGroupLinkStatement, updateChapterGroupRangeStatement, insertNovelStatement, insertNovelStatusStatement, insertChapterGroupStatement));
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
                    insertNovelStatement.setString(2, StringEscapeUtils.escapeEcmaScript(novel.getTitle()));
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

//            List<DBRow> rows = new ArrayList<>();
//
//            //Create a DBRow entity for every DB row
//            while (results.next()) {
//                rows.add(new DBRow(results.getString(1),
//                        results.getString(2),
//                        results.getString(3), results.getString(4),
//                        Novel.parseStatus(results.getString(5)))
//                );
//            }
//
//            if (!results.isClosed()) {
//                results.close();
//            }
//            conn.setAutoCommit(false);
//
//            NovelContainer novelsToBeUpdated = new NovelContainer();
//            ChapterGroupContainer chaptersToBeUpdated = new ChapterGroupContainer();
//
//            //Iterate through every row in the DB
//            for (var row : rows) {
//                //If the scraped novel is an exact match to the db novel
//                if (novelsInMemory.contains(row.getNovelTitle(), row.getNovelStatus())) {
//                    Scraper.LOGGER.log(Level.FINEST, "{0} is already in the DB.", row.getNovelTitle());
//                } //If the scraped novel is a completed version of the db novel
//                else if (novelsInMemory.contains(row.getNovelTitle(), Novel.NovelStatus.COMPLETED)
//                        && !novelsToBeUpdated.contains(row.getNovelTitle())) {
//                    statement.addBatch("UPDATE novel_status SET status = '"
//                            + Novel.NovelStatus.COMPLETED
//                            + "' WHERE _id ='"
//                            + row.getNovelId() + "'");
//                    novelsToBeUpdated.add(novelsInMemory.getNovelByTitle(row.getNovelTitle()));
//                    Scraper.LOGGER.log(Level.FINEST, "{0}: Novel completed, updated status.", row.getNovelTitle());
//                } //If the scraped novel is a suspended version of the db novel
//                else if (novelsInMemory.contains(row.getNovelTitle(), Novel.NovelStatus.SUSPENDED)
//                        && !novelsToBeUpdated.contains(row.getNovelTitle())) {
//                    statement.addBatch("UPDATE novel_status SET status = '"
//                            + Novel.NovelStatus.SUSPENDED
//                            + "' WHERE _id ='"
//                            + row.getNovelId() + "'");
//                    novelsToBeUpdated.add(novelsInMemory.getNovelByTitle(row.getNovelTitle()));
//                    Scraper.LOGGER.log(Level.FINEST, "{0}: Novel suspended, updated status.", row.getNovelTitle());
//                } //If the scraped novel is an unsuspended/available version of the db novel
//                else if (novelsInMemory.contains(row.getNovelTitle(), Novel.NovelStatus.ONGOING)
//                        && !novelsToBeUpdated.contains(row.getNovelTitle())) {
//                    statement.addBatch("UPDATE novel_status SET status = '"
//                            + Novel.NovelStatus.ONGOING
//                            + "' WHERE _id ='"
//                            + row.getNovelId() + "'");
//                    novelsToBeUpdated.add(novelsInMemory.getNovelByTitle(row.getNovelTitle()));
//                    Scraper.LOGGER.log(Level.FINEST, "{0}: Novel unsuspended/available, updated status.", row.getNovelTitle());
//                } //If there is no equivalent scraped novel for a db novel and the db novel isn't finished
//                else if (!novelsInMemory.contains(row.getNovelTitle()) && !novelsToBeUpdated.contains(row.getNovelTitle()) && !row.getNovelStatus().equals(Novel.NovelStatus.COMPLETED)) {
//                    statement.addBatch("UPDATE novel_status SET status = '"
//                            + Novel.NovelStatus.UNAVAILABLE
//                            + "' WHERE _id ='"
//                            + row.getNovelId() + "'");
//                    novelsToBeUpdated.add(new Novel(row.getNovelTitle(), Novel.NovelStatus.UNAVAILABLE));
//                    Scraper.LOGGER.log(Level.FINEST, "{0}: Novel not available, updated status.", row.getNovelTitle());
//                    continue;
//                } //If there is no equivalent db novel for a scraped novel
//                else {
//                    continue;
//                }
//
//                //Get all scraped chapters of the novel with the current title, ignoring completion status
//                ChapterGroupContainer chaptersInMemory = novelsInMemory.getNovelByTitle(row.getNovelTitle()).getChapters();
//                //If scraped chapters of this novel contain this specific chapter
//                if (chaptersInMemory.contains(row.getChapterTitle())) {
//                    //If the scraped chapter link is the same as the DB link
//                    if (chaptersInMemory.getChapterGroupByRange(row.getChapterTitle()).getLink().equals(row.getChapterLink())) {
//                        Scraper.LOGGER.log(Level.FINEST, "{0}: {1} is already in the DB.", new Object[]{row.getNovelTitle(), row.getChapterTitle()});
//                    } //If the title is the same but the link is different
//                    else if (!chaptersToBeUpdated.containsLink(chaptersInMemory.getChapterGroupByRange(row.getChapterTitle()).getLink())) {
//                        statement.addBatch("UPDATE novel_chapter_groups SET link = '"
//                                + chaptersInMemory.getChapterGroupByRange(row.getChapterTitle()).getLink() + "'"
//                                + "WHERE link = '"
//                                + row.getChapterLink() + "'");
//                        chaptersToBeUpdated.add(chaptersInMemory.getChapterGroupByRange(row.getChapterTitle()));
//                        Scraper.LOGGER.log(Level.FINEST, "{0}: {1} The link was queued to be updated.", new Object[]{row.getNovelTitle(), row.getChapterTitle()});
//                    }
//                } //If the digits are the same as the scraped chapter but the link is different
//                else if (chaptersInMemory.contains(getDigits(row.getChapterTitle()))
//                        && !chaptersToBeUpdated.containsLink(chaptersInMemory.getChapterByDigits(getDigits(row.getChapterTitle())).getLink())) {
//                    Chapter chapter = chaptersInMemory.
//                            getChapterByDigits(getDigits(row.getChapterTitle()));
//                    statement.addBatch("UPDATE novel_chapter_groups SET link = '"
//                            + chapter.getLink() + "', "
//                            + "title = '"
//                            + chapter.getTitle()
//                            + "' WHERE novel_id = '"
//                            + row.getNovelId() + "' AND "
//                            + "title = '"
//                            + row.getChapterTitle() + "'");
//                    chaptersInMemory.getChapterGroupByRange(getDigitsAsString(row.getChapterTitle()));
//                    Scraper.LOGGER.log(Level.FINEST, "{0}: {1} ({2}) The link was queued to be updated.", new Object[]{row.getNovelTitle(), chapter.getTitle(), chapter.getLink()});
//
//                }
//            }
//
//            statement.executeBatch();
//            conn.commit();
//
//            results = statement.executeQuery(query);
//
//            rows.clear();
//
//            while (results.next()) {
//                rows.add(new DBRow(results.getString(1),
//                        results.getString(2),
//                        results.getString(3), results.getString(4),
//                        Novel.parseStatus(results.getString(5)))
//                );
//            }
//
//            if (!results.isClosed()) {
//                results.close();
//            }
//
//            NovelContainer dbNovels = new NovelContainer();
//            rows.stream()
//                    .distinct()
//                    .forEach(row -> dbNovels.add(new Novel(row.getNovelId(), row.getNovelStatus(), row.getNovelTitle(), new ChapterGroupContainer())));
//            rows.stream()
//                    .forEach(row -> dbNovels.getNovelByTitle(row.getNovelTitle()).getChapters().add(new Chapter(dbNovels.getNovelByTitle(row.getNovelTitle()), row.getChapterTitle(), row.getChapterLink())));
//
//            for (Novel novel : novelsInMemory) {
//                //If database doesn't contain a novel with this title, insert it
//                if (!dbNovels.contains(novel.getTitle())) {
//                    statement.addBatch("INSERT INTO"
//                            + novelIdTable
//                            + "VALUES ('"
//                            + novel.getId() + "', '"
//                            + StringEscapeUtils.escapeEcmaScript(novel.getTitle()) + "')");
//                    statement.addBatch("INSERT INTO"
//                            + novelStatusTable
//                            + "VALUES('"
//                            + novel.getId()
//                            + "', '"
//                            + novel.getStatus() + "')");
//                    Scraper.LOGGER.log(Level.FINEST, "{0} added to the queue.", novel.getTitle());
//                }
//
//                for (Chapter chapter : novel.getChapters()) {
//                    if (dbNovels.getNovelByTitle(novel.getTitle()) == null) {
//                        statement.addBatch("INSERT INTO"
//                                + novelChaptersTable
//                                + "VALUES ('"
//                                + chapter.getId() + "','"
//                                + novel.getId() + "','"
//                                + chapter.getTitle() + "','"
//                                + chapter.getLink() + "')"
//                        );
//                        Scraper.LOGGER.log(Level.FINEST, "{0}: {1} ({2}) The link was added to the queue.", new Object[]{novel.getTitle(), chapter.getTitle(), chapter.getLink()});
//                    } else if (!dbNovels.getNovelByTitle(novel.getTitle()).getChapters().containsLink(chapter.getLink())) {
//                        statement.addBatch("INSERT INTO"
//                                + novelChaptersTable
//                                + "VALUES ('"
//                                + chapter.getId() + "','"
//                                + dbNovels.getNovelByTitle(novel.getTitle()).getId() + "','"
//                                + chapter.getTitle() + "','"
//                                + chapter.getLink() + "')"
//                        );
//                        Scraper.LOGGER.log(Level.FINEST, "{0}: {1} ({2}) The link was added to the queue.", new Object[]{novel.getTitle(), chapter.getTitle(), chapter.getLink()});
//
//                    }
//                }
//            }
//            statement.executeBatch();
//            conn.commit();
//            conn.setAutoCommit(true);
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
            if(results.getInt(chapterGroupEndIndex) == 634){
                System.out.println("");
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
            if (chapterGroup.getLink().contains("https://priv.atebin.com/?03405ccf2b07b7d3#oMj/LXrPqoRMijxTQi5d34")) {
                System.out.println("");
            }
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
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) throws SQLException {
        new Database().checkChapterContinuity(new NovelContainer());
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
                    + "WHERE novel_id = 'cab4b9e5-b922-4200-8bb5-9d5286f62d9c' "
                    + "ORDER BY\n"
                    + titleAtNovelId);
            populateContainer(dbNovels, results);
//            FileWriter writer = new FileWriter("depth.txt");

            for (Novel novel : dbNovels) {
                var chapterGroups = novel.getChapters();
//                List<ArrayList<ChapterGroup>> listPaths = new ArrayList<>();
//                //get chapter with biggest end
                var current = chapterGroups.last();
//                //check if there is there is more than one chapter with that end
//                int paths = chapterGroups.getChapterGroupsByEnd(current.getEnd()).size();
//                //for each of those chapters with the same end
//                for (int i = 0; i < paths; ++i) {
//                    ArrayList<ChapterGroup> toKeep = new ArrayList<>();
//                    //iterate backwards
//                    current = chapterGroups.getChapterGroupsByEnd(current.getEnd()).get(i);
//                    while (true) {
//                        toKeep.add(current);
//                        if (chapterGroups.getSingularChapterGroupByEnd(current.getStart() - 1) != null) {
//                            current = chapterGroups.getSingularChapterGroupByEnd(current.getStart() - 1);
//                        } else {
//                            //add chain to listPaths
//                            listPaths.add(toKeep);
//                            break;
//                        }
//                    }
//                }
//                var biggestPath = listPaths.stream().max((o1, o2) -> {
//                    return Integer.compare(o1.size(), o2.size());
//                }).get();

//                chapterGroups.removeAll(biggestPath);
                chapterGroups.removeAll(getLongestPath(chapterGroups, current));
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
        int paths = chapterGroups.getChapterGroupsByEnd(current.getStart() - 1).size();
        //for each of those chapters with the same end
        List<ChapterGroupContainer> allPaths = new ArrayList<>();
        for (int i = 0; i < paths; ++i) {
            ChapterGroupContainer toKeep = new ChapterGroupContainer();
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
