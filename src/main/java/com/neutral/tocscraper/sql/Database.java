package com.neutral.tocscraper.sql;

import com.neutral.tocscraper.Scraper;
import com.neutral.tocscrapermodels.Chapter;
import static com.neutral.tocscrapermodels.Chapter.getDigits;
import static com.neutral.tocscrapermodels.Chapter.getDigitsAsString;
import com.neutral.tocscrapermodels.ChapterContainer;
import com.neutral.tocscrapermodels.DBRow;
import com.neutral.tocscrapermodels.Novel;
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
    private final String novelIdTable = " novel_id ";
    private final String novelChaptersTable = " novel_chapters ";
    private final String novelStatusTable = " novel_status ";
    private final String idAtNovelId = " novel_id._id ";
    private final String nameAtNovelId = " novel_id.name ";
    private final String novelIdAtNovelChapters = " novel_chapters.novel_id ";
    private final String titleAtNovelChapters = " novel_chapters.title ";
    private final String linkAtNovelChapters = " novel_chapters.link ";
    private final String idAtNovelStatusTable = " novel_status._id ";
    private final String statusAtNovelStatusTable = " novel_status.status ";

    private final String query
            = "SELECT\n"
            + "    " + idAtNovelId + "AS novel_id,\n"
            + "    " + nameAtNovelId + "AS novel_name,\n"
            + "    " + linkAtNovelChapters + "AS chapter_link,\n"
            + "    " + titleAtNovelChapters + "AS chapter_title,\n"
            + "    " + statusAtNovelStatusTable + "AS novel_status\n"
            + "FROM\n"
            + "    " + novelIdTable + "\n"
            + "INNER JOIN" + novelChaptersTable + "ON" + idAtNovelId + "=" + novelIdAtNovelChapters + "\n"
            + "INNER JOIN" + novelStatusTable + "ON" + idAtNovelId + "=" + idAtNovelStatusTable + "\n"
            + "ORDER BY\n"
            + nameAtNovelId;

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

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void updateDB(NovelContainer novelsInMemory) {
        try (Statement statement = conn.createStatement();) {
            //Get novel_id, novel_name, chapter_link, chapter_title
            ResultSet results = statement.executeQuery(query);
            List<DBRow> rows = new ArrayList<>();

            //Create a DBRow entity for every DB row
            while (results.next()) {
                rows.add(new DBRow(results.getString(1),
                        results.getString(2),
                        results.getString(3), results.getString(4),
                        Novel.parseStatus(results.getString(5)))
                );
            }

            if (!results.isClosed()) {
                results.close();
            }
            conn.setAutoCommit(false);

            NovelContainer novelsToBeUpdated = new NovelContainer();
            ChapterContainer chaptersToBeUpdated = new ChapterContainer();

            //Iterate through every row in the DB
            for (var row : rows) {
                //If the scraped novel is an exact match to the db novel
                if (novelsInMemory.contains(row.getNovelTitle(), row.getNovelStatus())) {
                    Scraper.LOGGER.log(Level.FINEST, "{0} is already in the DB.", row.getNovelTitle());
                } //If the scraped novel is a completed version of the db novel
                else if (novelsInMemory.contains(row.getNovelTitle(), Novel.NovelStatus.COMPLETED)
                        && !novelsToBeUpdated.contains(row.getNovelTitle())) {
                    statement.addBatch("UPDATE novel_status SET status = '"
                            + Novel.NovelStatus.COMPLETED
                            + "' WHERE _id ='"
                            + row.getNovelId() + "'");
                    novelsToBeUpdated.add(novelsInMemory.getNovelByTitle(row.getNovelTitle()));
                    Scraper.LOGGER.log(Level.FINEST, "{0}: Novel completed, updated status.", row.getNovelTitle());
                } //If the scraped novel is a suspended version of the db novel
                else if (novelsInMemory.contains(row.getNovelTitle(), Novel.NovelStatus.SUSPENDED)
                        && !novelsToBeUpdated.contains(row.getNovelTitle())) {
                    statement.addBatch("UPDATE novel_status SET status = '"
                            + Novel.NovelStatus.SUSPENDED
                            + "' WHERE _id ='"
                            + row.getNovelId() + "'");
                    novelsToBeUpdated.add(novelsInMemory.getNovelByTitle(row.getNovelTitle()));
                    Scraper.LOGGER.log(Level.FINEST, "{0}: Novel suspended, updated status.", row.getNovelTitle());
                } //If there is no equivalent scraped novel for a db novel
                else if (!novelsInMemory.contains(row.getNovelTitle()) && !novelsToBeUpdated.contains(row.getNovelTitle())) {
                    statement.addBatch("UPDATE novel_status SET status = '"
                            + Novel.NovelStatus.UNAVAILABLE
                            + "' WHERE _id ='"
                            + row.getNovelId() + "'");
                    novelsToBeUpdated.add(new Novel(row.getNovelTitle(), Novel.NovelStatus.UNAVAILABLE));
                    Scraper.LOGGER.log(Level.FINEST, "{0}: Novel not available, updated status.", row.getNovelTitle());
                    continue;
                } //If there is no equivalent db novel for a scraped novel
                else {
                    continue;
                }

                //Get all scraped chapters of the novel with the current title, ignoring completion status
                ChapterContainer chaptersInMemory = novelsInMemory.getNovelByTitle(row.getNovelTitle()).getChapters();
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
                else if (chaptersInMemory.contains(getDigits(row.getChapterTitle()))
                        && !chaptersToBeUpdated.containsLink(chaptersInMemory.getChapterByDigits(getDigits(row.getChapterTitle())).getLink())) {
                    Chapter chapter = chaptersInMemory.
                            getChapterByDigits(getDigits(row.getChapterTitle()));
                    statement.addBatch("UPDATE novel_chapters SET link = '"
                            + chapter.getLink() + "', "
                            + "title = '"
                            + chapter.getTitle()
                            + "' WHERE novel_id = '"
                            + row.getNovelId() + "' AND "
                            + "title = '"
                            + row.getChapterTitle() + "'");
                    chaptersInMemory.getChapterByTitle(getDigitsAsString(row.getChapterTitle()));
                    Scraper.LOGGER.log(Level.FINEST, "{0}: {1} ({2}) The link was queued to be updated.", new Object[]{row.getNovelTitle(), chapter.getTitle(), chapter.getLink()});

                }
            }

            statement.executeBatch();
            conn.commit();

            results = statement.executeQuery(query);

            rows.clear();

            while (results.next()) {
                rows.add(new DBRow(results.getString(1),
                        results.getString(2),
                        results.getString(3), results.getString(4),
                        Novel.parseStatus(results.getString(5)))
                );
            }

            if (!results.isClosed()) {
                results.close();
            }

            NovelContainer dbNovels = new NovelContainer();
            rows.stream()
                    .distinct()
                    .forEach(row -> dbNovels.add(new Novel(row.getNovelId(), row.getNovelStatus(), row.getNovelTitle(), new ChapterContainer())));
            rows.stream()
                    .forEach(row -> dbNovels.getNovelByTitle(row.getNovelTitle()).getChapters().add(new Chapter(dbNovels.getNovelByTitle(row.getNovelTitle()), row.getChapterTitle(), row.getChapterLink())));

            for (Novel novel : novelsInMemory) {
                //If database doesn't contain a novel with this title, insert it
                if (!dbNovels.contains(novel.getTitle())) {
                    statement.addBatch("INSERT INTO"
                            + novelIdTable
                            + "VALUES ('"
                            + novel.getId() + "', '"
                            + StringEscapeUtils.escapeEcmaScript(novel.getTitle()) + "')");
                    statement.addBatch("INSERT INTO"
                            + novelStatusTable
                            + "VALUES('"
                            + novel.getId()
                            + "', '"
                            + novel.getStatus() + "')");
                    Scraper.LOGGER.log(Level.FINEST, "{0} added to the queue.", novel.getTitle());
                }

                for (Chapter chapter : novel.getChapters()) {
                    if (dbNovels.getNovelByTitle(novel.getTitle()) == null) {
                        statement.addBatch("INSERT INTO"
                                + novelChaptersTable
                                + "VALUES ('"
                                + chapter.getId() + "','"
                                + novel.getId() + "','"
                                + chapter.getTitle() + "','"
                                + chapter.getLink() + "')"
                        );
                        Scraper.LOGGER.log(Level.FINEST, "{0}: {1} ({2}) The link was added to the queue.", new Object[]{novel.getTitle(), chapter.getTitle(), chapter.getLink()});
                    } else if (!dbNovels.getNovelByTitle(novel.getTitle()).getChapters().containsLink(chapter.getLink())) {
                        statement.addBatch("INSERT INTO"
                                + novelChaptersTable
                                + "VALUES ('"
                                + chapter.getId() + "','"
                                + dbNovels.getNovelByTitle(novel.getTitle()).getId() + "','"
                                + chapter.getTitle() + "','"
                                + chapter.getLink() + "')"
                        );
                        Scraper.LOGGER.log(Level.FINEST, "{0}: {1} ({2}) The link was added to the queue.", new Object[]{novel.getTitle(), chapter.getTitle(), chapter.getLink()});

                    }
                }
            }

            statement.executeBatch();

            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

}
