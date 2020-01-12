package com.neutral.tocscrapermodels;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

/**
 *
 * @author Mr.Neutral
 */
public class ChapterGroupContainer implements Iterable<ChapterGroup> {

    private final TreeSet<ChapterGroup> chapters;

    public ChapterGroupContainer() {
        this.chapters = new TreeSet<>();
    }

    public ChapterGroupContainer(TreeSet<ChapterGroup> chapters) {
        this.chapters = chapters;
    }

    public ChapterGroup getChapterGroupByRange(int start, int end) {
        for (ChapterGroup chapter : chapters) {
            if (chapter.getStart() == start && chapter.getEnd() == end) {
                return chapter;
            }
        }
        return null;
    }

    public List<ChapterGroup> getChapterGroupsByEnd(int end) {
        ArrayList<ChapterGroup> list = new ArrayList<>();
        chapters.stream().filter(chapter -> chapter.getEnd() == end).forEach(list::add);
        return list;
    }

    public ChapterGroup getSingularChapterGroupByEnd(int end) {
        for (ChapterGroup chapter : chapters) {
            if (chapter.getEnd() == end) {
                return chapter;
            }
        }
        return null;
    }

    public ChapterGroup getChapterGroupByLink(String link) {
        for (ChapterGroup chapter : chapters) {
            if (chapter.getLink().equals(link)) {
                return chapter;
            }
        }
        return null;
    }

    public boolean contains(int start, int end) {
        return chapters.stream().anyMatch(chapter -> chapter.getStart() == start && chapter.getEnd() == end);
    }

    public boolean containsLink(String link) {
        return chapters.stream().anyMatch(chapter -> chapter.getLink().equals(link));
    }

    public void clear() {
        chapters.clear();
    }

    public int size() {
        return chapters.size();
    }

    public boolean add(ChapterGroup chapter) {
        return chapters.add(chapter);
    }

    @Override
    public Iterator<ChapterGroup> iterator() {
        return chapters.iterator();
    }

    public ChapterGroup first() {
        return chapters.first();
    }

    public ChapterGroup last() {
        return chapters.last();
    }

    public ChapterGroup lower(ChapterGroup e) {
        return chapters.lower(e);
    }

    public ChapterGroup higher(ChapterGroup e) {
        return chapters.higher(e);
    }

    public boolean retainAll(Collection<?> c) {
        return chapters.retainAll(c);
    }

    public boolean removeAll(Collection<?> c) {
        return chapters.removeAll(c);
    }    
    

}
