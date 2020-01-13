package com.neutral.tocscrapermodels;

import java.util.Objects;

/**
 *
 * @author Mr.Neutral
 */
public class ChapterGroup implements Comparable<ChapterGroup> {

    private final String id;
    private final Novel novel;
    private final int start;
    private final int end;
    private final String link;

//    public ChapterGroup(int start, int end) {
//        this.start = start;
//        this.end = end;
//        this.link = null;
//        this.novel = null;
//        id = UUID.randomUUID().toString();
//    }
//
//    public ChapterGroup(String title, String link) {
//        this.start = start;
//        this.end = end;
//        this.link = link;
//        this.novel = null;
//        id = UUID.randomUUID().toString();
//    }
//
//    public ChapterGroup(Novel novel, String title, String link) {
//        this.novel = novel;
//        this.title = title;
//        this.link = link;
//        id = UUID.randomUUID().toString();
//    }
    ChapterGroup(String id, Novel novel, int starts, int ends, String link) {
        this.id = id;
        this.novel = novel;
        this.start = starts;
        this.end = ends;
        this.link = link;
    }

//    public Integer getDigits() {
//        return Integer.valueOf(title.split("-")[0].trim());
//    }
//
//    public String getDigitsAsString() {
//        return title.split("-")[0].trim();
//    }
//
//    public static Integer getDigits(String string) {
//        return Integer.valueOf(string.split("-")[0].trim());
//    }
//
//    public static String getDigitsAsString(String string) {
//        return string.split("-")[0].trim();
//    }
    public Novel getNovel() {
        return novel;
    }

    public String getId() {
        return id;
    }

//    public String getTitle() {
//        return title;
//    }
    public String getLink() {
        return link;
    }

    public boolean isMissing() {
        return link.equals("Missing");
    }

//    @Override
//    public int compareTo(ChapterGroup o) {
//        return getDigits().compareTo(o.getDigits());
//    }
//    @Override
//    public boolean equals(Object obj) {
//        if (obj == null || !(obj instanceof ChapterGroup)) {
//            return false;
//        }
//        try {
//            ChapterGroup chapter = (ChapterGroup) obj;
//            if (novel != null) {
//                return novel.equals(chapter.getNovel()) && getDigits().equals(chapter.getDigits());
//            }
//            return getDigits().equals(chapter.getDigits());
//        } catch (ClassCastException e) {
//            return false;
//        }
//    }
//    @Override
//    public int hashCode() {
//        int hash = 7;
//        if (novel != null) {
//            hash = 17 * hash + Objects.hashCode(this.novel);
//        }
//        hash = 17 * hash + Objects.hashCode(getDigits());
//        return hash;
//    }
    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.novel);
        hash = 79 * hash + this.start;
        hash = 79 * hash + this.end;
        hash = 79 * hash + Objects.hashCode(this.link);
        return hash;
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
        final ChapterGroup other = (ChapterGroup) obj;
        if (this.start != other.start) {
            return false;
        }
        if (this.end != other.end) {
            return false;
        }
        if (!Objects.equals(this.link, other.link)) {
            return false;
        }
        return Objects.equals(this.novel, other.novel);
    }

    @Override
    public int compareTo(ChapterGroup o) {
        return Integer.compare(end, o.end) == 0 ? Integer.compare(start, o.start) : Integer.compare(end, o.end);
    }

    @Override
    public String toString() {
        return "ChapterGroup{" + "id=" + id + ", novel=" + novel + ", start=" + start + ", end=" + end + ", link=" + link + "}\n";
    }

}
