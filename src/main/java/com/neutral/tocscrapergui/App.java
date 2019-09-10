package com.neutral.tocscrapergui;

import com.neutral.tocscrapergui.models.Novel;
import com.neutral.tocscrapergui.services.NovelDetailsRetrievalService;
import com.neutral.tocscrapergui.services.NovelRetrievalService;
import com.neutral.tocscrapergui.sql.Database;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    public static final Logger LOGGER = Logger.getLogger(App.class.getName());
    private static final List<Novel> NOVELS = new ArrayList<>();
    private static final Database DB = new Database();
    public static final NovelRetrievalService NOVEL_RETRIEVAL_SERVICE = new NovelRetrievalService();
    public static final NovelDetailsRetrievalService NOVEL_DETAILS_RETRIEVAL_SERVICE = new NovelDetailsRetrievalService();

    public static Database getDB() {
        return DB;
    }

    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("ToCScraper");
        scene = new Scene(loadFXML("main"));
        stage.setScene(scene);
        stage.show();
    }

    public static List<Novel> getNovels() {
        return NOVELS;
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        try {
            FileHandler handler = new FileHandler("log.txt", false);
            handler.setLevel(Level.ALL);
            LOGGER.setLevel(Level.ALL);
            handler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(handler);
            LOGGER.log(Level.FINE, "Logger Initiated");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }

        launch();
    }

}
