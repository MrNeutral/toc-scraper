package com.neutral.tocscrapermodels;

import com.neutral.tocscrapermodels.Novel.NovelStatus;
import java.util.Objects;

/**
 *
 * @author Mr.Neutral
 */
public class DBRow {

    private final String novelId;
    private final String novelTitle;
    private final String chapterLink;
    private final String chapterTitle;
    private final NovelStatus novelStatus;

    public DBRow(String novelId, String novelTitle, String chapterLink, String chapterTitle, NovelStatus novelStatus) {
        this.novelId = novelId;
        this.novelTitle = novelTitle;
        this.chapterLink = chapterLink;
        this.chapterTitle = chapterTitle;
        this.novelStatus = novelStatus;
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

    public NovelStatus getNovelStatus() {
        return novelStatus;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof DBRow)) {
            return false;
        }
        DBRow row = (DBRow) obj;
        return getNovelId().equals(row.getNovelId());
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(getNovelId());
        return hash;
    }
    
    

}
