package com.neutral.tocscrapermodels;

import java.util.UUID;

public class NovelBuilder {

    private String id = UUID.randomUUID().toString();
    private Novel.NovelStatus status = null;
    private String title = null;
    private ChapterGroupContainer chapters = null;

    public NovelBuilder() {
    }

    public NovelBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public NovelBuilder setStatus(Novel.NovelStatus status) {
        this.status = status;
        return this;
    }

    public NovelBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public NovelBuilder setChapters(ChapterGroupContainer chapters) {
        this.chapters = chapters;
        return this;
    }

    public Novel createNovel() {
        Novel novel = new Novel(id, status, title, chapters);
        clear();
        return novel;
    }

    private void clear() {
        id = UUID.randomUUID().toString();
        status = null;
        title = null;
        chapters = null;
    }

}
