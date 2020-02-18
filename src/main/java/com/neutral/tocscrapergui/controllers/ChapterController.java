package com.neutral.tocscrapergui.controllers;

import com.neutral.tocscrapergui.App;
import static com.neutral.tocscrapergui.App.chapterRetrieval;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebView;

/**
 *
 * @author Mr.Neutral
 */
public class ChapterController implements Initializable {

    private String link = null;

    @FXML
    private WebView webView;

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
        chapterRetrieval.setLink(link);
        chapterRetrieval.start();
        chapterRetrieval.statusProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                webView.getEngine().loadContent(chapterRetrieval.statusProperty().get());
            }
        });
        webView.getEngine().setUserStyleSheetLocation(App.class.getResource("/com/neutral/tocscrapergui/styles.css").toString());
    }
}
