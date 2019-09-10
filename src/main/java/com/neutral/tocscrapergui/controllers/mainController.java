package com.neutral.tocscrapergui.controllers;

import static com.neutral.tocscrapergui.App.LOGGER;
import static com.neutral.tocscrapergui.App.NOVEL_DETAILS_RETRIEVAL_SERVICE;
import static com.neutral.tocscrapergui.App.NOVEL_RETRIEVAL_SERVICE;
import com.neutral.tocscrapergui.models.Chapter;
import com.neutral.tocscrapergui.models.Novel;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Service;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

/**
 * FXML Controller class
 *
 * @author Mr.Neutral
 */
public class mainController implements Initializable {

    @FXML
    private BorderPane borderPane;

    @FXML
    private ListView<Novel> novelListView;

    @FXML
    private ListView<Chapter> chapterListView;

    @FXML
    private HBox mainHBox;

    @FXML
    private ImageView novelImageView;

    @FXML
    private TextArea detailsTextArea;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        novelListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        novelListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Novel>() {
            @Override
            public void changed(ObservableValue<? extends Novel> ov, Novel t, Novel t1) {
                chapterListView.setItems(new SortedList<>(FXCollections.observableArrayList(t1.getChapters())).sorted());
                try {
                    if (NOVEL_DETAILS_RETRIEVAL_SERVICE.stateProperty().get() == Service.State.RUNNING) {
                        NOVEL_DETAILS_RETRIEVAL_SERVICE.cancel();
                    }
                    NOVEL_DETAILS_RETRIEVAL_SERVICE.setNovel(t1);
                    NOVEL_DETAILS_RETRIEVAL_SERVICE.start();
                    detailsTextArea.textProperty().bind(NOVEL_DETAILS_RETRIEVAL_SERVICE.statusProperty());
                    if (NOVEL_DETAILS_RETRIEVAL_SERVICE.novelImageProperty().get() != null) {
                        novelImageView.imageProperty().bind(NOVEL_DETAILS_RETRIEVAL_SERVICE.novelImageProperty());
                    } else {
                        NOVEL_DETAILS_RETRIEVAL_SERVICE.setOnSucceeded(e -> novelImageView.imageProperty().bind(NOVEL_DETAILS_RETRIEVAL_SERVICE.novelImageProperty()));
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, e.toString(), e);
                }
            }

        });

        try {
            NOVEL_RETRIEVAL_SERVICE.start();
            NOVEL_RETRIEVAL_SERVICE.setOnRunning(e -> {
                chapterListView.getItems().add(new Chapter("Loading Novels..."));
            });
            novelListView.itemsProperty().bind(NOVEL_RETRIEVAL_SERVICE.valueProperty());
            NOVEL_RETRIEVAL_SERVICE.setOnSucceeded(e -> {
                novelListView.getSelectionModel().selectFirst();
            });
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }
    }

}
