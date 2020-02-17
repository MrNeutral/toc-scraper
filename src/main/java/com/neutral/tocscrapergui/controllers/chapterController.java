package com.neutral.tocscrapergui.controllers;

import io.github.bonigarcia.wdm.DriverManagerType;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

/**
 *
 * @author Mr.Neutral
 */
public class chapterController implements Initializable {

    private String link = null;
    private static final WebDriver CLIENT;

    @FXML
    private Label textLabel;

    static {
        FirefoxOptions opt = new FirefoxOptions();
        opt.setHeadless(true);
        WebDriverManager.getInstance(DriverManagerType.FIREFOX).forceCache().setup();
        CLIENT = new FirefoxDriver(opt);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void getChapter() {
        getWebpage();
    }

    private void getWebpage() {
        CLIENT.get(link);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
        }
        Document doc = Jsoup.parse(CLIENT.getPageSource());
        doc.getElementsByClass("input-group text-justify center-block col-md-6 col-md-offset-3").remove();
        textLabel.setText(doc.select("#plaintext").text());
    }
}
