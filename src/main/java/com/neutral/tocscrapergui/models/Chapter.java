package com.neutral.tocscrapergui.models;

import java.util.UUID;

/**
 *
 * @author Mr.Neutral
 */
public class Chapter implements Comparable<Chapter> {

    private final String id;
    private final Novel novel;
    private final String chapters;
    private final String link;

    public Chapter(String chapters) {
        this.chapters = chapters;
        id = null;
        link = null;
        novel = null;
    }

    public Chapter(Novel novel, String chapters, String link) {
        this.novel = novel;
        this.chapters = chapters;
        this.link = link;
        id = UUID.randomUUID().toString();
    }

    public Chapter(String id, Novel novel, String chapters, String link) {
        this.id = id;
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

    @Override
    public String toString() {
        return chapters;
    }

    @Override
    public int compareTo(Chapter o) {
        Integer ch1 = Integer.valueOf(chapters.split("-")[0].trim());
        Integer ch2 = Integer.valueOf(o.getChapters().split("-")[0].trim());;
        return ch1.compareTo(ch2);
    }

}
