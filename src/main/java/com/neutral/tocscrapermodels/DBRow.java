package com.neutral.tocscrapermodels;

/**
 *
 * @author Mr.Neutral
 */
public class DBRow {
    private final String novelId;
    private final String novelTitle;
    private final String chapterLink;
    private final String chapterTitle;

    public DBRow(String novelId, String novelTitle, String chapterLink, String chapterTitle) {
        this.novelId = novelId;
        this.novelTitle = novelTitle;
        this.chapterLink = chapterLink;
        this.chapterTitle = chapterTitle;
    }

    /**
     * @return the novelId
     */
    public String getNovelId() {
        return novelId;
    }

    /**
     * @return the novelTitle
     */
    public String getNovelTitle() {
        return novelTitle.trim();
    }

    /**
     * @return the chapterLink
     */
    public String getChapterLink() {
        return chapterLink;
    }

    /**
     * @return the chapterTitle
     */
    public String getChapterTitle() {
        return chapterTitle;
    }
    
    
}
