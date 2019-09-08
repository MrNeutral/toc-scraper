package com.neutral.tocscrapper;

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
            Scrapper scrapper = new Scrapper();
            scrapper.scrape();
        } catch (Exception e) {
            System.exit(1);
        }
    }
}
