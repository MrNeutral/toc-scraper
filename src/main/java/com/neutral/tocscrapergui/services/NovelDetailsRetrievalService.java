package com.neutral.tocscrapergui.services;

import com.neutral.tocscrapergui.NovelDetailsRetriever;
import com.neutral.tocscrapergui.models.Novel;
import com.neutral.tocscrapergui.models.NovelDetails;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.image.Image;

/**
 *
 * @author Mr.Neutral
 */
public class NovelDetailsRetrievalService extends Service<NovelDetails> {

    private Novel novel;
    private StringProperty statusProperty = new SimpleStringProperty();
    private ObjectProperty<Image> novelImageProperty = new SimpleObjectProperty<>();

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
        statusProperty.set("Details not found.");
    }

    @Override
    protected void running() {
        statusProperty.set("Getting details...");
    }

    @Override
    protected void succeeded() {
        statusProperty.set("Title: "
                + novel.getTitle() + "\n\n"
                + valueProperty().getValue().toString());
        novelImageProperty.set(new Image(valueProperty().getValue().getImageURL()));
        reset();
    }

    @Override
    protected void cancelled() {
        reset();
    }

    @Override
    public boolean cancel() {
        novel = null;
        return super.cancel();
    }

    @Override
    protected Task<NovelDetails> createTask() {
        return new Task<>() {
            @Override
            protected NovelDetails call() throws Exception {
                if (novel == null) {
                    throw new Exception("No valid novel.");
                }
                NovelDetails details = NovelDetailsRetriever.getNovelDetails(novel);
                return details;
            }
        };
    }

}
