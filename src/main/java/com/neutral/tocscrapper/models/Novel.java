package com.neutral.tocscrapper.models;

import java.util.List;

/**
 *
 * @author Mr.Neutral
 */
public class Novel {

    private final String title;
    private final List<Chapter> chapters;

    public Novel(String title, List<Chapter> chapters) {
        this.title = title;
        this.chapters = chapters;
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
