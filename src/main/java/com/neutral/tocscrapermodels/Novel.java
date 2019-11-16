package com.neutral.tocscrapermodels;

import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author Mr.Neutral
 */
public class Novel {

    private final String id;
    private final boolean completed;
    private final String title;
    private final ChapterContainer chapters;

    public Novel(String id, boolean completed, String title, ChapterContainer chapters) {
        this.id = id;
        this.completed = completed;
        this.title = title;
        this.chapters = chapters;
    }

    public Novel(String title, ChapterContainer chapters) {
        this.title = title;
        this.chapters = chapters;
        this.completed = false;
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBaseTitle() {
        return removeExtra(title);
    }

    public ChapterContainer getChapters() {
        return chapters;
    }

    @Override
    public String toString() {
        return title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public static String removeExtra(String title) {
        return title.split("\\(Completed\\)")[0].split("\\(Suspend\\)")[0].trim();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Novel)) {
            return false;
        }
        Novel novel = (Novel) obj;
        return getBaseTitle().equals(novel.getBaseTitle());
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(getBaseTitle());
        return hash;
    }

}
