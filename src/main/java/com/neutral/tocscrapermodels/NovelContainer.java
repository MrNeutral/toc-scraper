package com.neutral.tocscrapermodels;

import com.neutral.tocscrapermodels.Novel.NovelStatus;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author Mr.Neutral
 */
public class NovelContainer implements Iterable<Novel> {

    private final Set<Novel> novels;

    public NovelContainer() {
        this.novels = new TreeSet<>();
    }

    public NovelContainer(Set<Novel> novels) {
        this.novels = novels;
    }

    public Set<Novel> asList() {
        return novels;
    }

    public Novel getNovelByTitle(String title) {
        for (Novel novel : novels) {
            if (novel.getTitle().equals(title)) {
                return novel;
            }
        }
        return null;
    }

    public Novel getNovelById(String id) {
        for (Novel novel : novels) {
            if (novel.getId().equals(id)) {
                return novel;
            }
        }
        return null;
    }

    public boolean contains(String title) {
        return novels.stream().anyMatch(novel -> (novel.getTitle().equals(title)));
    }

    public boolean containsId(String id) {
        return novels.stream().anyMatch(novel -> (novel.getId().equals(id)));
    }

    public boolean contains(String title, NovelStatus status) {
        return novels.stream().anyMatch(novel -> (novel.getTitle().equals(title) && novel.getStatus().equals(status)));
    }

    public int size() {
        return novels.size();
    }

    public boolean add(Novel novel) {
        return novels.add(novel);
    }

    public void clear() {
        novels.clear();
    }

    @Override
    public Iterator<Novel> iterator() {
        return novels.iterator();
    }
}
