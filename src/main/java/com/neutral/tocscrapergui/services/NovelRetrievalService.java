package com.neutral.tocscrapergui.services;

import com.neutral.tocscrapergui.App;
import com.neutral.tocscrapermodels.Novel;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 *
 * @author Mr.Neutral
 */
public class NovelRetrievalService extends Service<SortedList<Novel>> {

    @Override
    protected Task<SortedList<Novel>> createTask() {
        return new Task<SortedList<Novel>>() {
            @Override
            protected SortedList<Novel> call() throws Exception {
                SortedList<Novel> novels = new SortedList(App.getDB().getNovels()).sorted();
                return novels;
            }
        };
    }
    
}
