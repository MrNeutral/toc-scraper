package com.neutral.tocscrapergui.controllers;

import static com.neutral.tocscrapergui.App.App;
import static com.neutral.tocscrapergui.App.logger;
import static com.neutral.tocscrapergui.App.novelDetailsRetrieval;
import static com.neutral.tocscrapergui.App.novelRetrieval;
import com.neutral.tocscrapermodels.ChapterGroup;
import com.neutral.tocscrapermodels.ChapterGroupBuilder;
import com.neutral.tocscrapermodels.Novel;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Service;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author Mr.Neutral
 */
public class mainController implements Initializable {

    @FXML
    private BorderPane borderPane;

    @FXML
    private Label novelsLabel;

    @FXML
    private TextField novelSearch;

    @FXML
    private ListView<Novel> novelListView;

    @FXML
    private ListView<ChapterGroup> chapterListView;

    @FXML
    private HBox mainHBox;

    @FXML
    private ImageView novelImageView;

    @FXML
    private TextArea detailsTextArea;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        novelImageListeners();
        chapterListViewListeners();
        novelListViewListeners();
        novelSearchListeners();
        getNovelsAndDetails();
    }

    private void novelImageListeners() {
        novelImageView.imageProperty().addListener(new ChangeListener<Image>() {
            @Override
            public void changed(ObservableValue<? extends Image> ov, Image t, Image t1) {
                novelImageView.setCursor((t1 == null) ? Cursor.DEFAULT : Cursor.CLOSED_HAND);
            }

        });
        novelImageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if (novelImageView.getImage() != null) {
                    var string = novelListView.getSelectionModel().selectedItemProperty().get().getTitle()
                            .replace("[^a-zA-Z0-9 ]", "")
                            .replace(" ", "-")
                            .toLowerCase();
                    App.getHostServices().showDocument("https://www.novelupdates.com/series/" + string);
                }
            }
        });
    }

    private void chapterListViewListeners() {
        chapterListView.setCellFactory(new Callback<ListView<ChapterGroup>, ListCell<ChapterGroup>>() {
            @Override
            public ListCell<ChapterGroup> call(ListView<ChapterGroup> p) {
                ListCell<ChapterGroup> chapter = new ListCell<>() {
                    @Override
                    protected void updateItem(ChapterGroup t, boolean bln) {
                        super.updateItem(t, bln);
                        if (t == null) {
                            setText(null);
                            setGraphic(null);
                            return;
                        }
                        if (bln) {
                            setText(null);
                        } else {
                            if (t.getStart() == 0 && t.getEnd() == 0) {
                                setText("Loading Novels...");
                            } else if (t.getStart() == 0 && t.getEnd() == -1) {
                                setText("Failure to retrieve Novels...");
                            } else {
                                setText(t.getStart() + "-" + t.getEnd());
                            }
                        }
                    }

                };
                chapter.setOnMouseClicked(
                        new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent t
                    ) {
                        if (t.getClickCount() > 1 && chapter.getItem().getLink() != null) {
                            App.getHostServices().showDocument(chapter.getItem().getLink());
//                            try {
//                                Stage stage = new Stage();
//                                var scene = new Scene(new FXMLLoader(App.class.getResource("/fxml/" + "webView" + ".fxml")).load());
//                                stage.setScene(scene);
//                                stage.show();
//                            } catch (IOException e) {
//
//                            }
                        }
                    }
                }
                );
                return chapter;
            }
        }
        );
    }

    private void novelListViewListeners() {
        novelListView.getSelectionModel()
                .setSelectionMode(SelectionMode.SINGLE);
        novelListView.getSelectionModel()
                .selectedItemProperty().addListener(new ChangeListener<Novel>() {
                    @Override
                    public void changed(ObservableValue<? extends Novel> ov, Novel t, Novel t1) {
                        chapterListView
                                .setItems(new SortedList<>(FXCollections
                                        .observableArrayList(t1.getChapters().getChapters())).sorted());

                        try {

                            if (novelDetailsRetrieval.stateProperty().get() == Service.State.RUNNING) {
                                novelDetailsRetrieval.cancel();
                            }
                            novelDetailsRetrieval.setNovel(t1);
                            novelDetailsRetrieval.start();
                            detailsTextArea.textProperty().bind(novelDetailsRetrieval.statusProperty());

                            if (novelDetailsRetrieval.novelImageProperty().get() != null) {
                                novelImageView.imageProperty().bind(novelDetailsRetrieval.novelImageProperty());
                            } else {
                                novelDetailsRetrieval.setOnSucceeded(e -> novelImageView.imageProperty().bind(novelDetailsRetrieval.novelImageProperty()));
                            }

                        } catch (Exception e) {
                            logger.log(Level.SEVERE, e.toString(), e);
                        }
                    }
                }
                );
    }

    private void getNovelsAndDetails() {
        try {
            novelRetrieval.start();
            novelRetrieval.setOnRunning(e -> {
                //temporary fix
                chapterListView.getItems().add(new ChapterGroupBuilder().setStart(0).setEnd(0).createChapter());
            });
            novelRetrieval.setOnSucceeded(e -> {
                chapterListView.getItems().clear();
                novelListView.setItems(novelRetrieval.valueProperty().get());
                novelListView.getSelectionModel().selectFirst();
                novelsLabel.setText("Novels: " + novelListView.getItems().size());
            });

            novelDetailsRetrieval.setOnFailed(e -> {
                chapterListView.getItems().clear();
                //temporary fix
                chapterListView.getItems().add(new ChapterGroupBuilder().setStart(0).setEnd(-1).createChapter());
            });
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.toString(), e);
        }
    }

    private void novelSearchListeners() {
        novelSearch.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                if (t1.isBlank()) {
                    novelListView.setItems(novelRetrieval.valueProperty().get());
                    return;
                }
                novelListView.setItems(
                        new SortedList<>(
                                FXCollections.observableArrayList(
                                        novelRetrieval.valueProperty().get().stream()
                                                .filter(novel -> novel.getTitle().toLowerCase().startsWith(t1.toLowerCase()))
                                                .collect(Collectors.toList())
                                )
                        ).sorted());
            }
        });
    }
}
