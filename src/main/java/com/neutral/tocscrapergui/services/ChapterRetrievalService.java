package com.neutral.tocscrapergui.services;

import io.github.bonigarcia.wdm.DriverManagerType;
import io.github.bonigarcia.wdm.WebDriverManager;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

/**
 *
 * @author Mr.Neutral
 */
public class ChapterRetrievalService extends Service<String> {

    private WebDriver client;
    private String link;
    private ChapterRetrievalService service = this;
    private DriverManagerType driverManagerType = DriverManagerType.FIREFOX;
    //make it require a restart as the driver isn't checked when init-ing
    private boolean quitOnFinish = true;

    public void setQuitOnFinish(boolean quitOnFinish) {
        this.quitOnFinish = quitOnFinish;
    }

    public boolean isQuitOnFinish() {
        return quitOnFinish;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    protected void succeeded() {
        reset();
    }

    @Override
    protected void cancelled() {
        reset();
    }

    @Override
    public boolean cancel() {
        return super.cancel();
    }

    @Override
    public void reset() {
        if (quitOnFinish) {
            client.quit();
        }
        super.reset();
    }

    @Override
    protected void failed() {
        reset();
    }

    @Override
    protected Task<String> createTask() {
        return new Task<>() {
            @Override
            protected String call() {
                try {
                    updateMessage("Starting browser...");
                    initDriver();
                    client.get(link);
                    try {
                        updateMessage("Waiting for page to load...");
                        int count = 0;
                        Thread.sleep(500);
                        while (client.getPageSource().split("role=\"document\"")[0].contains("loading")) {
                            Thread.sleep(250);
                            count++;
                            if (count > 20) {
                                failed();
                                return "";
                            }
                        }
                    } catch (InterruptedException ex) {
                    }

                    updateMessage("Finished...");
                    Document doc = Jsoup.parse(client.getPageSource());
                    doc.getElementsByClass("input-group text-justify center-block col-md-6 col-md-offset-3").remove();
                    String head
                            = "<body><head><link href=\"https://fonts.googleapis.com/css?family=Merriweather \" rel=\"stylesheet\"></head>";
                    if (quitOnFinish) {
                        client.quit();
                    }
                    return head + doc.select("#plaintext").html() + "</body>";
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    failed();
                    return "";
                }
            }

            @Override
            protected void failed() {
                updateMessage("Failed to load page...");
                service.failed();
            }
        };
    }

    private void initDriver() throws NullPointerException {
        if (!quitOnFinish) {
            return;
        }
        switch (driverManagerType) {
            case FIREFOX: {
                FirefoxOptions opt = new FirefoxOptions();
                opt.setHeadless(true);
                WebDriverManager.getInstance(driverManagerType).forceCache().setup();
                client = new FirefoxDriver(opt);
                break;
            }
            case CHROME: {
                ChromeOptions opt = new ChromeOptions();
                opt.setHeadless(true);
                WebDriverManager.getInstance(driverManagerType).forceCache().setup();
                client = new ChromeDriver(opt);
                break;
            }
            default:
                throw new NullPointerException("No valid browser found. Only Firefox and Chrome are supported.");
        }

    }

}
