package com.neutral.tocscrapermodels;

import java.util.Objects;

/**
 *
 * @author Mr.Neutral
 */
public class Novel implements Comparable<Novel> {

    private final String id;
    private final NovelStatus status;
    private final String title;
    private ChapterGroupContainer chapters;

//    public Novel(String id) {
//        this.id = id;
//        this.status = null;
//        this.title = null;
//        this.chapters = null;
//    }
//
//    public Novel(String title, NovelStatus status) {
//        this.id = null;
//        this.status = status;
//        this.title = title;
//        this.chapters = null;
//    }
//
    Novel(String id, NovelStatus status, String title, ChapterGroupContainer chapters) {
        this.id = id;
        this.status = status;
        this.title = title;
        this.chapters = chapters;
    }
//
//    public Novel(String title, ChapterGroupContainer chapters, NovelStatus status) {
//        this.title = title;
//        this.chapters = chapters;
//        this.status = status;
//        this.id = UUID.randomUUID().toString();
//    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public ChapterGroupContainer getChapters() {
        if (chapters == null) {
            chapters = new ChapterGroupContainer();
        }
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
        return title.contains("Completed")
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
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Novel other = (Novel) obj;
        return Objects.equals(this.title, other.title);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.title);
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

        public static NovelStatus from(String text) {
            return NovelStatus.valueOf(text.toUpperCase());
        }

        @Override
        public String toString() {
            return name;
        }

    }

    @Override
    public int compareTo(Novel o) {
        return title.compareTo(o.title);
    }

}
