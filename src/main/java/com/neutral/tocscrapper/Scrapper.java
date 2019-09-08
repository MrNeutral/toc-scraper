package com.neutral.tocscrapper;

import com.neutral.tocscrapper.models.Chapter;
import com.neutral.tocscrapper.models.Novel;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * @author Mr.Neutral
 */
public class Scrapper {
    
    private final String site = "https://toc.qidianunderground.org/";
    private WebDriver driver;
    private File log = new File("log.txt");
    private FileWriter logWriter = new FileWriter(log, true);
    private final List<String> titles = new ArrayList<>();
    private final List<Novel> novels = new ArrayList<>();
    
    public Scrapper() throws Exception {
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
            new WebDriverWait(driver, 1000).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("button")));
            String html = String.valueOf(
                    ((JavascriptExecutor) driver)
                            .executeScript("return document.getElementsByTagName('html')[0].innerHTML")
            );
            logWriter.write("**HTML**\n" + html + "\n**HTML**\n");
            Document doc = Jsoup.parse(html);
            driver.quit();
            parseTitles(doc);
            parseChapterLinks(doc);
        } catch (Exception e) {
            driver.quit();
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        
        File file = new File("scrape.csv");
        try (FileWriter writer = new FileWriter(file, false)) {
            writer.write("Title\tChapter(s)\tLink\n");
            for (Novel novel : novels) {
                for (Chapter chapter : novel.getChapters()) {
                    String text = novel.getTitle() + "\t" + chapter.getChapters() + "\t" + chapter.getLink() + "\n";
                    writer.write(text);
                    logWriter.write(text + " written\n");
                }
            }
            logWriter.close();
        } catch (Exception e) {
        }
    }
    
    public List<Novel> getNovels() {
        return novels;
    }
    
    private void parseTitles(Document doc) throws Exception {
        List<Element> possibleTitles = doc.select(".content p");
        List<String> titles = new ArrayList<>();
        
        try {
            ListIterator<Element> iterator = possibleTitles.listIterator();
            while (iterator.hasNext()) {
                String title = possibleTitles.get(iterator.nextIndex()).text();
                try {
//                    if (title.contains("about")) {
//                        title.substring(0, title.indexOf("about"));
//                    } else {
//                        int numberIndex = 0;
//                        for(int i = 0; i < title.length(); ++i){
//                            try {
//                                Integer.valueOf(title.charAt(i));
//                                numberIndex = i;
//                                break;
//                            } catch (NumberFormatException e){
//                                try {
//                                    
//                                }
//                            }
//                        }
//                    title = title.split("\n")[0];
                    titles.add(title);
                    logWriter.write("Title " + title + " added.\n");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                } finally {
                    iterator.next();
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            driver.quit();
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
            
            for (Element link : unParsedNovelLinks) {
                String linkAdress = "";
                String chapters = link.text();
                
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
                logWriter.write("Chapter " + chapters + " added.\n");
                parsedNovelLinks.add(new Chapter(chapters, linkAdress));
            }
            logWriter.write("Title:" + titles.get(index) + "\n");
            novels.add(new Novel(titles.get(index), parsedNovelLinks));
        }
        
        if (missing > 0) {
            System.out.println(missing + " chapters are missing.");
        } else {
            System.out.println("No chapters are missing.");
        }
        
    }
}
