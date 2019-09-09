package com.neutral.tocscrapper.models;

import java.util.UUID;

/**
 *
 * @author Mr.Neutral
 */
public class Chapter {

    private final String id = UUID.randomUUID().toString();
    private Novel novel;
    private final String chapters;
    private final String link;

    public Chapter(Novel novel, String chapters, String link) {
        this.novel = novel;
        this.chapters = chapters;
        this.link = link;
    }

    public Novel getNovel() {
        return novel;
    }

    public String getId() {
        return id;
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
