package com.neutral.tocscraper.models;

import java.util.List;
import java.util.UUID;

/**
 *
 * @author Mr.Neutral
 */
public class Novel {

    private String id = UUID.randomUUID().toString();
    private final String title;
    private final List<Chapter> chapters;

    public Novel(String title, List<Chapter> chapters) {
        this.title = title;
        this.chapters = chapters;
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
        return title + ": " + chapters.size() + " chapter links";
    }

}
