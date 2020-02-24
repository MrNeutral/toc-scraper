package com.neutral.tocscrapermodels;

import java.util.List;

/**
 *
 * @author Mr.Neutral
 */
public class NovelDetails {

    private String title;
    private String imageURL;
    private String type;
    private String description;
    private List<String> genre;
    private List<String> tags;
    private String rating;
    private String language;
    private List<String> authors;
    private String year;
    private String releaseFrequency;
    private String status;
    private String originalPublisher;
    private String englishPublisher;

    public NovelDetails() {
        this.imageURL = "Not found";
        this.type = "Not found";
        this.description = "Not found";
        this.genre = List.of("Not found");
        this.tags = List.of("Not found");
        this.rating = "Not found";
        this.language = "Not found";
        this.authors = List.of("Not found");
        this.year = "Not found";
        this.releaseFrequency = "Not found";
        this.originalPublisher = "Not found";
        this.englishPublisher = "Not found";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    /**
     * @return the imageURL
     */
    public String getImageURL() {
        return imageURL;
    }

    /**
     * @param imageURL the imageURL to set
     */
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the genre
     */
    public List<String> getGenre() {
        return genre;
    }

    /**
     * @param genre the genre to set
     */
    public void setGenre(List<String> genre) {
        this.genre = genre;
    }

    /**
     * @return the tags
     */
    public List<String> getTags() {
        return tags;
    }

    /**
     * @param tags the tags to set
     */
    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    /**
     * @return the rating
     */
    public String getRating() {
        return rating;
    }

    /**
     * @param rating the rating to set
     */
    public void setRating(String rating) {
        this.rating = rating;
    }

    /**
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language the language to set
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * @return the authors
     */
    public List<String> getAuthors() {
        return authors;
    }

    /**
     * @param authors the authors to set
     */
    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    /**
     * @return the year
     */
    public String getYear() {
        return year;
    }

    /**
     * @param year the year to set
     */
    public void setYear(String year) {
        this.year = year;
    }

    /**
     * @return the releaseFrequency
     */
    public String getReleaseFrequency() {
        return releaseFrequency;
    }

    /**
     * @param releaseFrequency the releaseFrequency to set
     */
    public void setReleaseFrequency(String releaseFrequency) {
        this.releaseFrequency = releaseFrequency;
    }

    /**
     * @return the originalPublisher
     */
    public String getOriginalPublisher() {
        return originalPublisher;
    }

    /**
     * @param originalPublisher the originalPublisher to set
     */
    public void setOriginalPublisher(String originalPublisher) {
        this.originalPublisher = originalPublisher;
    }

    /**
     * @return the englishPublisher
     */
    public String getEnglishPublisher() {
        return englishPublisher;
    }

    /**
     * @param englishPublisher the englishPublisher to set
     */
    public void setEnglishPublisher(String englishPublisher) {
        this.englishPublisher = englishPublisher;
    }

    @Override
    public String toString() {
        return "Title: " + title + "\n\n"
                + "Type: " + type + "\n\n"
                + "Description: " + description + "\n\n"
                + "Genre: " + genre + "\n\n"
                + "Tags: " + tags + "\n\n"
                + "Rating: " + rating + "\n\n"
                + "Language: " + language + "\n\n"
                + "Authors: " + authors + "\n\n"
                + "Year: " + year + "\n\n"
                + "Release Frequency: " + releaseFrequency + "\n\n"
                + "Status: " + status + "\n\n"
                + "Original Publisher: " + originalPublisher + "\n\n"
                + "English Publisher: " + englishPublisher;
    }

}
