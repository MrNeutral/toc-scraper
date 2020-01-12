package com.neutral.tocscrapermodels;

import java.util.UUID;

public class ChapterGroupBuilder {

    private String id = UUID.randomUUID().toString();
    private Novel novel;
    private int start;
    private int end;
    private String link;

    public ChapterGroupBuilder() {
    }

    public ChapterGroupBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public ChapterGroupBuilder setNovel(Novel novel) {
        this.novel = novel;
        return this;
    }

    public ChapterGroupBuilder setStart(int start) {
        this.start = start;
        return this;
    }

    public ChapterGroupBuilder setEnd(int end) {
        this.end = end;
        return this;
    }

    public ChapterGroupBuilder setLink(String link) {
        this.link = link;
        return this;
    }

    public ChapterGroup createChapter() {
        ChapterGroup chapterGroup = new ChapterGroup(id, novel, start, end, link);
        clear();
        return chapterGroup;
    }

    private void clear() {
        id = UUID.randomUUID().toString();
        novel = null;
        start = 0;
        end = 0;
        link = null;
    }

}
