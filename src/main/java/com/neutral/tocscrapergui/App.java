package com.neutral.tocscrapergui;

import com.neutral.tocscrapergui.services.ChapterRetrievalService;
import com.neutral.tocscrapergui.services.NovelDetailsRetrievalService;
import com.neutral.tocscrapergui.services.NovelRetrievalService;
import com.neutral.tocscrapergui.sql.DatabaseRetriever;
import com.neutral.tocscrapermodels.NovelContainer;
import java.io.IOException;
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
    public static App App = null;
    public static final Logger LOGGER = Logger.getLogger(App.class.getName());
    private static final NovelContainer novels = new NovelContainer();
    private static final DatabaseRetriever DB = new DatabaseRetriever();
    public static final NovelRetrievalService novelRetrieval = new NovelRetrievalService();
    public static final NovelDetailsRetrievalService novelDetailsRetrieval = new NovelDetailsRetrievalService();
    public static final ChapterRetrievalService chapterRetrieval = new ChapterRetrievalService();

    public App() {
        App = this;
    }

    public static DatabaseRetriever getDB() {
        return DB;
    }

    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("ToCScraper");
        scene = new Scene(loadFXML("main"));
        stage.setScene(scene);
        stage.show();
    }

    public static NovelContainer getNovels() {
        return novels;
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
