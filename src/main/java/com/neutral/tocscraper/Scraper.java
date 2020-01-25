package com.neutral.tocscraper;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.neutral.tocscraper.sql.Database;
import com.neutral.tocscrapermodels.ChapterGroup;
import com.neutral.tocscrapermodels.ChapterGroupBuilder;
import com.neutral.tocscrapermodels.ChapterGroupContainer;
import com.neutral.tocscrapermodels.Novel;
import static com.neutral.tocscrapermodels.Novel.parseStatusFromTitle;
import static com.neutral.tocscrapermodels.Novel.removeExtra;
import com.neutral.tocscrapermodels.NovelBuilder;
import com.neutral.tocscrapermodels.NovelContainer;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 *
 * @author Mr.Neutral
 */
public class Scraper {

    private final String site = "https://toc.qidianunderground.org/";
    private final Database dB = new Database();
    private final WebClient client;
    public static final Logger LOGGER = Logger.getLogger(Scraper.class.getName());
    private final List<String> titles = new ArrayList<>();
    private final NovelContainer novels = new NovelContainer();
    private final NovelBuilder novelBuilder = new NovelBuilder();
    private final ChapterGroupBuilder chapterGroupBuilder = new ChapterGroupBuilder();

    public Scraper() throws Exception {
        FileHandler handler = new FileHandler("log.txt", false);
        handler.setLevel(Level.ALL);
        LOGGER.setLevel(Level.ALL);
        handler.setFormatter(new SimpleFormatter());
        LOGGER.addHandler(handler);
        client = new WebClient(BrowserVersion.FIREFOX_60);
        client.getOptions().setPrintContentOnFailingStatusCode(false);
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(true);
        client.getOptions().setThrowExceptionOnFailingStatusCode(false);
        client.getOptions().setRedirectEnabled(true);
        client.setJavaScriptTimeout(10000);
    }

    public void scrape() {
        try (client) {
            client.getPage(site);
            client.waitForBackgroundJavaScriptStartingBefore(1000);
            client.waitForBackgroundJavaScript(15000);
            Document doc = Jsoup.parse(client.getPage(site).getWebResponse().getContentAsString());
            parseTitles(doc);
            parseChapterLinks(doc);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
            System.exit(1);
        }

        writeToCSV();
        updateDB();
    }

    public NovelContainer getNovels() {
        return novels;
    }

    public void writeToCSV() {
        File file = new File("scrape.csv");
        try (FileWriter writer = new FileWriter(file, false)) {
            writer.write("Title, Chapter(s), Link\n");
            for (Novel novel : novels) {
                for (ChapterGroup chapter : novel.getChapters()) {
                    String text = novel.getTitle() + ", " + chapter.getStart() + ", " + chapter.getEnd() + ", " + chapter.getLink() + "\n";
                    writer.write(text);
                }
            }
        } catch (Exception e) {
            file.delete();
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }
    }

    public void updateDB() {
        dB.updateDB(novels);
    }

    private void parseTitles(Document doc) throws Exception {
        List<Element> possibleTitles = doc.select(".content p");
        List<Element> dates = doc.select(".content p small");
        dates.stream().forEach(Element::remove);
        List<String> titles = new ArrayList<>();

        try {
            ListIterator<Element> iterator = possibleTitles.listIterator();
            while (iterator.hasNext()) {
                String title = possibleTitles.get(iterator.nextIndex()).text();
                try {
                    String regex = "(about)? a?(\\d\\d?)? (month(s)?)?(day(s)?)?(hour(s)?)? ago";
                    titles.add(title.split(regex)[0].trim());
                    LOGGER.log(Level.FINEST, "//Title {0} added.\n", title);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, e.toString(), e);
                } finally {
                    iterator.next();
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }

        this.titles.addAll(titles);
    }

    private void parseChapterLinks(Document doc) throws Exception {
        List<Element> novelLinksInLists = doc.select(".content ul li");
        ListIterator<Element> iterator = novelLinksInLists.listIterator();
        int missing = 0;

        while (iterator.hasNext()) {
            int index = iterator.nextIndex();
            List<Element> unParsedNovelLinks = iterator.next().children();
            ChapterGroupContainer parsedNovelLinks = new ChapterGroupContainer();
            Novel novel = novelBuilder
                    .setTitle(removeExtra(titles.get(index)))
                    .setChapters(parsedNovelLinks)
                    .setStatus(parseStatusFromTitle(titles.get(index)))
                    .createNovel();

            for (Element link : unParsedNovelLinks) {
                String linkAdress = "";
                String[] chapterRange = link.text().trim().split("-");
                if (chapterRange.length > 2) {
                    //stop being retarded ToC, negative chapters are insane
                    continue;
                }
                int start, end;

//                try {
//                    Integer.valueOf(chapterRange[0].substring(0, 3).trim());
//                } catch (NumberFormatException e) {
//                    continue;
//                }
                if (link.tagName().equals("a")) {
                    linkAdress = link.attr("href");
                } else {
                    missing++;
                    chapterRange[0] = chapterRange[0].substring(8, chapterRange[0].length());
                    if (chapterRange.length > 1) {
                        linkAdress = "Missing: " + chapterRange[0].trim() + "-" + chapterRange[1].trim();
                    } else {
                        linkAdress = "Missing: " + chapterRange[0].trim();
                    }
                }

                if (chapterRange.length == 1) {
                    start = Integer.parseInt(chapterRange[0].trim());
                    end = start;
                } else {
                    start = Integer.parseInt(chapterRange[0].trim());
                    end = Integer.parseInt(chapterRange[1].trim());
                }
                LOGGER.log(Level.FINEST, "//Chapter group {0} added.\n", start
                        + (chapterRange.length > 1 ? "-" + end : ""));
                parsedNovelLinks.add(chapterGroupBuilder
                        .setNovel(novel)
                        .setStart(start)
                        .setEnd(end)
                        .setLink(linkAdress)
                        .createChapter());

            }
            LOGGER.log(Level.FINER, "//Novel {0} finished.\n", titles.get(index));
            novels.add(novel);
        }

        if (missing > 0) {
            LOGGER.log(Level.INFO, "{0} chapters are missing.", missing);
        } else {
            LOGGER.log(Level.INFO, "No chapters are missing.");
        }

    }
}
