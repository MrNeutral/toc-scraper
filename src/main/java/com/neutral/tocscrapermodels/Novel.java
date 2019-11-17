package com.neutral.tocscrapermodels;

import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author Mr.Neutral
 */
public class Novel {

    private final String id;
    private final NovelStatus status;
    private final String title;
    private final ChapterContainer chapters;

    public Novel(String id) {
        this.id = id;
        this.status = null;
        this.title = null;
        this.chapters = null;
    }

    public Novel(String title, NovelStatus status) {
        this.id = null;
        this.status = status;
        this.title = title;
        this.chapters = null;
    }

    public Novel(String id, NovelStatus status, String title, ChapterContainer chapters) {
        this.id = id;
        this.status = status;
        this.title = title;
        this.chapters = chapters;
    }

    public Novel(String title, ChapterContainer chapters, NovelStatus status) {
        this.title = title;
        this.chapters = chapters;
        this.status = status;
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public ChapterContainer getChapters() {
        return chapters;
    }

    @Override
    public String toString() {
        return title;
    }

    public NovelStatus getStatus() {
        return status;
    }

    public static NovelStatus parseStatusFromTitle(String title) {
        return (title.contains("Completed"))
                ? NovelStatus.COMPLETED : ((title.contains("Suspend"))
                ? NovelStatus.SUSPENDED : NovelStatus.ONGOING);
    }

    public static NovelStatus parseStatus(String status) {
        return NovelStatus.valueOf(status.toUpperCase());
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
        return getTitle().equals(novel.getTitle());
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(getTitle());
        return hash;
    }

    public static enum NovelStatus {
        COMPLETED("Completed"),
        SUSPENDED("Suspended"),
        UNAVAILABLE("Unavailable"),
        ONGOING("Ongoing");

        private final String name;

        private NovelStatus(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

    }

}
