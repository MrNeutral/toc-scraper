package com.neutral.tocscraper;

/**
 *
 * @author Mr.Neutral
 */
public class Main {

    public static void main(String[] args) {
        try {
            new Scraper().scrape();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
