package com.neutral.tocscrapermodels;

import com.neutral.tocscrapermodels.Novel.NovelStatus;
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
        return novels.stream().anyMatch((novel) -> (novel.getTitle().equals(title)));
    }
    
    public boolean containsId(String id) {
        return novels.stream().anyMatch((novel) -> (novel.getId().equals(id)));
    }
    
    public boolean contains(String title, NovelStatus status) {
        return novels.stream().anyMatch((novel) -> (novel.getTitle().equals(title) && novel.getStatus().equals(status)));
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
