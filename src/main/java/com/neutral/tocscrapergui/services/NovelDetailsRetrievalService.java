package com.neutral.tocscrapergui.services;

import com.neutral.tocscrapergui.App;
import com.neutral.tocscrapergui.NovelDetailsRetriever;
import com.neutral.tocscrapermodels.Novel;
import com.neutral.tocscrapermodels.NovelDetails;
import java.util.logging.Level;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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
    private final ObjectProperty<Image> novelImageProperty = new SimpleObjectProperty<>();

    public void setNovel(Novel novel) {
        this.novel = novel;
    }

    public ObjectProperty<Image> novelImageProperty() {
        return novelImageProperty;
    }

    @Override
    protected void failed() {
        reset();
    }

    @Override
    protected void running() {
        novelImageProperty.set(null);
    }

    @Override
    protected void succeeded() {
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
                updateMessage("Getting details...");
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
                updateMessage(details.toString());
                return details;
            }

            @Override
            protected void failed() {
                service.failed();
                updateMessage("Details not found...");
            }

        };
    }

}
