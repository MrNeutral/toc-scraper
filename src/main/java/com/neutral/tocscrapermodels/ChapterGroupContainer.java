package com.neutral.tocscrapermodels;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

/**
 *
 * @author Mr.Neutral
 */
public class ChapterGroupContainer implements Iterable<ChapterGroup> {

    private final TreeSet<ChapterGroup> chapters;

    public ChapterGroupContainer() {
        this.chapters = new TreeSet<>();
    }

    public ChapterGroupContainer(ChapterGroup... chapterGroups) {
        this.chapters = new TreeSet<>(List.of(chapterGroups));
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

    public boolean retainAll(ChapterGroupContainer c) {
        return chapters.retainAll(c.getChapters());
    }

    public boolean removeAll(ChapterGroupContainer c) {
        return chapters.removeAll(c.getChapters());
    }

    public boolean addAll(ChapterGroupContainer c) {
        return chapters.addAll(c.chapters);
    }

    public Stream<ChapterGroup> stream() {
        return chapters.stream();
    }

    public Set<ChapterGroup> getChapters() {
        return chapters;
    }

    @Override
    public boolean equals(Object o) {
        return chapters.equals(o);
    }

    @Override
    public int hashCode() {
        return chapters.hashCode();
    }

    @Override
    public String toString() {
        return "ChapterGroupContainer{" + "chapters=" + chapters + "}\n";
    }

}
