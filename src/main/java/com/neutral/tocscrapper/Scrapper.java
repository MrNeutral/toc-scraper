package com.neutral.tocscrapper;

import com.neutral.tocscrapper.models.Chapter;
import com.neutral.tocscrapper.models.Novel;
import com.neutral.tocscrapper.sql.Database;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

/**
 *
 * @author Mr.Neutral
 */
public class Scrapper {

    private final String site = "https://toc.qidianunderground.org/";
    private Database dB = new Database();
    private WebDriver driver;
    private File log = new File("log.txt");
//    private FileWriter logWriter = new FileWriter(log, true);
    public static final Logger LOGGER = Logger.getLogger(Scrapper.class.getName());
    private final List<String> titles = new ArrayList<>();
    private final List<Novel> novels = new ArrayList<>();

    public Scrapper() throws Exception {
        FileHandler handler = new FileHandler("log.txt", false);
        handler.setLevel(Level.ALL);
        LOGGER.setLevel(Level.ALL);
        handler.setFormatter(new SimpleFormatter());
        LOGGER.addHandler(handler);
        System.setProperty("webdriver.gecko.driver", "/home/cha0snation/Applications/geckodriver");
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.setHeadless(true);
        try {
            driver = new FirefoxDriver(firefoxOptions);
        } catch (Exception e) {
            driver.quit();
            throw new Exception("Driver Initialization error");
        }
    }

    public void scrape() {
        try {
            driver.navigate().to(site);
//            new WebDriverWait(driver, 5000).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("button")));
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                LOGGER.log(Level.FINER, "Waiting for Cloudflare.");
            }
            String html = String.valueOf(
                    ((JavascriptExecutor) driver)
                            .executeScript("return document.getElementsByTagName('html')[0].innerHTML")
            );
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                LOGGER.log(Level.FINER, "Waiting for JS.");
            }
            driver.quit();
            if (html.contains("Zombie Sister Strategy")) {
                LOGGER.log(Level.FINER, "HTML downloaded");
            }
            Document doc = Jsoup.parse(html);
            parseTitles(doc);
            parseChapterLinks(doc);
        } catch (Exception e) {
            driver.quit();
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }

        writeToCSV();
        updateDB();
    }

    public List<Novel> getNovels() {
        return novels;
    }

    public void writeToCSV() {
        File file = new File("scrape.csv");
        try (FileWriter writer = new FileWriter(file, false)) {
            writer.write("Title, Chapter(s), Link\n");
            for (Novel novel : novels) {
                for (Chapter chapter : novel.getChapters()) {
                    String text = novel.getTitle() + ", " + chapter.getChapters() + ", " + chapter.getLink() + "\n";
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
        List<String> titles = new ArrayList<>();

        try {
            ListIterator<Element> iterator = possibleTitles.listIterator();
            while (iterator.hasNext()) {
                String title = possibleTitles.get(iterator.nextIndex()).text();
                try {
                    String regex = "20\\d\\d-\\d\\d-\\d\\d[A-Z]\\d\\d:\\d\\d:\\d\\d[A-Z]";
                    titles.add(StringEscapeUtils.escapeEcmaScript(title.split(regex)[0].trim()));
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
            List<Chapter> parsedNovelLinks = new ArrayList<>();
            Novel novel = new Novel(titles.get(index), parsedNovelLinks);

            for (Element link : unParsedNovelLinks) {
                String linkAdress = "";
                String chapters = link.text().trim();

                try {
                    Integer.valueOf(chapters.substring(0, 3).trim());
                } catch (NumberFormatException e) {
                    continue;
                }

                if (link.tagName().equals("a")) {
                    linkAdress = link.attr("href");
                } else {
                    missing++;
                    linkAdress = "Missing";
                    chapters = chapters.substring(8, chapters.length());
                }
                LOGGER.log(Level.FINEST, "//Chapter {0} added.\n", chapters);
                parsedNovelLinks.add(new Chapter(novel, chapters, linkAdress));
            }
            LOGGER.log(Level.FINER, "//Novel {0} finished.\n", titles.get(index));
            novels.add(novel);
        }

        if (missing > 0) {
            LOGGER.log(Level.INFO, "{} chapters are missing.", missing);
        } else {
            LOGGER.log(Level.INFO, "No chapters are missing.");
        }

    }
}
