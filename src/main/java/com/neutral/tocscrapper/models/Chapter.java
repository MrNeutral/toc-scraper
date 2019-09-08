package com.neutral.tocscrapper.models;

/**
 *
 * @author Mr.Neutral
 */
public class Chapter {

    private final String chapters;
    private final String link;

    public Chapter(String chapters, String link) {
        this.chapters = chapters;
        this.link = link;
    }

    public String getChapters() {
        return chapters;
    }

    public String getLink() {
        return link;
    }

    public boolean isMissing() {
        return link.equals("Missing");
    }
}
