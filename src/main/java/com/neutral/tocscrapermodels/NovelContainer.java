package com.neutral.tocscrapermodels;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Mr.Neutral
 */
public class NovelContainer implements Iterable<Novel> {

    private final List<Novel> novels;

    public NovelContainer() {
        this.novels = new ArrayList<>();
    }

    public NovelContainer(List<Novel> novels) {
        this.novels = novels;
    }

    public List<Novel> asList() {
        return novels;
    }

    public Novel getNovelByName(String title) {
        for (Novel novel : novels) {
            if (novel.getTitle().equals(title)) {
                return novel;
            }
        }
        return null;
    }

    public Novel getNovelByNameIgnoreStatus(String title) {
        for (Novel novel : novels) {
            if (novel.getTitle().equals(title) || novel.getTitle().equals(title.replace("(Completed)", "").trim()) || novel.getTitle().equals(title.trim() + " (Completed)") || novel.getTitle().equals(title.replace("(Suspend)", "").trim()) || novel.getTitle().equals(title.trim() + " (Suspend)")) {
                return novel;
            }
        }
        return null;
    }

    public boolean contains(String title) {
        for (Novel novel : novels) {
            if (novel.getTitle().equals(title)) {
                return true;
            }
        }
        return false;
    }

    public int size() {
        return novels.size();
    }

    public boolean add(Novel novel) {
        return novels.add(novel);
    }

    @Override
    public Iterator<Novel> iterator() {
        return novels.iterator();
    }
}
