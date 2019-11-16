package com.neutral.tocscrapermodels;

import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author Mr.Neutral
 */
public class Chapter implements Comparable<Chapter> {

    private final String id;
    private final Novel novel;
    private final String title;
    private final String link;

    public Chapter(String title) {
        this.title = title;
        this.link = null;
        this.novel = null;
        id = UUID.randomUUID().toString();
    }

    public Chapter(String title, String link) {
        this.title = title;
        this.link = link;
        this.novel = null;
        id = UUID.randomUUID().toString();
    }

    public Chapter(Novel novel, String title, String link) {
        this.novel = novel;
        this.title = title;
        this.link = link;
        id = UUID.randomUUID().toString();
    }

    public Chapter(String id, Novel novel, String title, String link) {
        this.id = id;
        this.novel = novel;
        this.title = title;
        this.link = link;
    }

    public Integer getDigits() {
        return Integer.valueOf(title.split("-")[0].trim());
    }

    public String getDigitsAsString() {
        return title.split("-")[0].trim();
    }

    public static Integer getDigits(String string) {
        return Integer.valueOf(string.split("-")[0].trim());
    }

    public static String getDigitsAsString(String string) {
        return string.split("-")[0].trim();
    }

    public Novel getNovel() {
        return novel;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public boolean isMissing() {
        return link.equals("Missing");
    }

    @Override
    public int compareTo(Chapter o) {
        return getDigits().compareTo(o.getDigits());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        try {
            Chapter chapter = (Chapter) obj;
            return novel.equals(chapter.getNovel()) && getDigits().equals(chapter.getDigits());
        } catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.novel);
        hash = 17 * hash + Objects.hashCode(getDigits());
        return hash;
    }

}
