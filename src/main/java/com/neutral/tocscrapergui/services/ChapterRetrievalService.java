package com.neutral.tocscrapergui.services;

import io.github.bonigarcia.wdm.DriverManagerType;
import io.github.bonigarcia.wdm.WebDriverManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
    private final StringProperty statusProperty = new SimpleStringProperty();
    //make it require a restart as the driver isn't checked when init-ing
    private boolean quitOnFinish = true;

    public StringProperty statusProperty() {
        return statusProperty;
    }

    public void setQuitOnFinish(boolean quitOnFinish) {
        this.quitOnFinish = quitOnFinish;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    protected void running() {
        statusProperty.set("");
    }

    @Override
    protected void succeeded() {
        statusProperty.set(service.valueProperty().get());
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
    protected void failed() {
        reset();
        statusProperty.set("Page took too long to load...");
    }

    @Override
    protected Task<String> createTask() {
        try {
            initDriver();
        } catch (NullPointerException e) {
            return new Task<>() {
                @Override
                protected String call() throws Exception {
                    return e.getMessage();
                }
            };
        }
        return new Task<>() {
            @Override
            protected String call() throws Exception {
                statusProperty.set("Starting browser...");
                client.get(link);
                try {
                    statusProperty.set("Waiting for page to load...");
                    int count = 0;
                    while (client.getPageSource().split("role=\"document\"")[0].contains("loading")) {
                        Thread.sleep(250);
                        count++;
                        if (count > 20) {
                            failed();
                        }
                    }
                } catch (InterruptedException ex) {
                }
                statusProperty.set("Finished...");
                Document doc = Jsoup.parse(client.getPageSource());
                doc.getElementsByClass("input-group text-justify center-block col-md-6 col-md-offset-3").remove();
                String head
                        = "<body><head><link href=\"https://fonts.googleapis.com/css?family=Merriweather \" rel=\"stylesheet\"></head>";
                if (quitOnFinish) {
                    client.quit();
                }
                return head + doc.select("#plaintext").html() + "</body>";
            }

            @Override
            protected void failed() {
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
