package com.neutral.tocscrapergui.services;

import com.neutral.tocscrapergui.App;
import com.neutral.tocscrapergui.NovelDetailsRetriever;
import com.neutral.tocscrapermodels.Novel;
import com.neutral.tocscrapermodels.NovelDetails;
import java.util.logging.Level;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import org.jsoup.HttpStatusException;

/**
 *
 * @author Mr.Neutral
 */
public class NovelDetailsRetrievalService extends Service<NovelDetails> {

    private Novel novel;
    private final NovelDetailsRetrievalService service = this;
    private final StringProperty statusProperty = new SimpleStringProperty();
    private final ObjectProperty<Image> novelImageProperty = new SimpleObjectProperty<>();

    public void setNovel(Novel novel) {
        this.novel = novel;
    }

    public StringProperty statusProperty() {
        return statusProperty;
    }

    public ObjectProperty<Image> novelImageProperty() {
        return novelImageProperty;
    }

    @Override
    protected void failed() {
        reset();
        statusProperty.set("Details not found.");
    }

    @Override
    protected void running() {
        novelImageProperty.set(null);
        statusProperty.set("Getting details...");
    }

    @Override
    protected void succeeded() {
        statusProperty.set("Title: "
                + novel.getTitle() + "\n\n"
                + "Status: "
                + novel.getStatus() + "\n\n"
                + valueProperty().get());
        novelImageProperty.set(new Image(valueProperty().get().getImageURL()));
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
    protected Task<NovelDetails> createTask() {
        return new Task<>() {
            @Override
            protected NovelDetails call() {
                if (novel == null) {
                    App.LOGGER.log(Level.SEVERE, "No valid novel.");
                    failed();
                }
                NovelDetails details = null;
                try {
                    details = NovelDetailsRetriever.getNovelDetails(novel);
                } catch (HttpStatusException e) {
                    App.LOGGER.log(Level.FINER, e.toString());
                    failed();
                }
                return details;
            }

            @Override
            protected void failed() {
                service.failed();
            }

        };
    }

}
