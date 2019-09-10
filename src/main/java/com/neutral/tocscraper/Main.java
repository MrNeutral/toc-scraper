package com.neutral.tocscraper;

/**
 *
 * @author Mr.Neutral
 */
public class Main {

//    public static void main(String[] args) {
//        App.main(args);
//    }
    public static void main(String[] args) {
        try {
            Scraper scrapper = new Scraper();
            scrapper.scrape();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
