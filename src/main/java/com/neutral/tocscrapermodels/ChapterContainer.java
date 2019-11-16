package com.neutral.tocscrapermodels;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Mr.Neutral
 */
public class ChapterContainer implements Iterable<Chapter> {

    private final List<Chapter> chapters;

    public ChapterContainer() {
        this.chapters = new ArrayList<>();
    }

    public ChapterContainer(List<Chapter> chapters) {
        this.chapters = chapters;
    }

    public List<Chapter> asList() {
        return chapters;
    }

    public Chapter getChapterByTitle(String title) {
        for (Chapter chapter : chapters) {
            if (chapter.getTitle().equals(title)) {
                return chapter;
            }
        }
        return null;
    }

    public Chapter getChapterByDigits(int digits) {
        for (Chapter chapter : chapters) {
            if (chapter.getDigits() == digits) {
                return chapter;
            }
        }
        return null;
    }

    public boolean contains(String title) {
        for (Chapter chapter : chapters) {
            if (chapter.getTitle().equals(title)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean containsLink(String link) {
        for (Chapter chapter : chapters) {
            if (chapter.getLink().equals(link)) {
                return true;
            }
        }
        return false;
    }

    public void clear() {
        chapters.clear();
    }

    public boolean contains(int digits) {
        for (Chapter chapter : chapters) {
            if (chapter.getDigits() == digits) {
                return true;
            }
        }
        return false;
    }

    public int size() {
        return chapters.size();
    }

    public boolean add(Chapter chapter) {
        return chapters.add(chapter);
    }

    @Override
    public Iterator<Chapter> iterator() {
        return chapters.iterator();
    }
}
