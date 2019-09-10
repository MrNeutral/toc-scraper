package com.neutral.tocscrapergui.models;

import java.util.List;
import java.util.UUID;

/**
 *
 * @author Mr.Neutral
 */
public class Novel implements Comparable<Novel> {

    private String id;
    private boolean completed;
    private final String title;
    private final List<Chapter> chapters;

    public Novel(String title) {
        this.title = title;
        chapters = null;
        id = null;
    }

    public Novel(String title, List<Chapter> chapters, boolean completed) {
        this.title = title;
        this.chapters = chapters;
        this.completed = completed;
        id = UUID.randomUUID().toString();
    }

    public Novel(String id, boolean completed, String title, List<Chapter> chapters) {
        this.id = id;
        this.completed = completed;
        this.title = title;
        this.chapters = chapters;
    }

    public boolean isCompleted() {
        return completed;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<Chapter> getChapters() {
        return chapters;
    }

    @Override
    public String toString() {
        return title;
    }

    @Override
    public int compareTo(Novel o) {
        return title.compareTo(o.getTitle());
    }

}
