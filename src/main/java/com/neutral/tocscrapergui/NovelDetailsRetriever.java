package com.neutral.tocscrapergui;

import com.neutral.tocscrapergui.models.Novel;
import com.neutral.tocscrapergui.models.NovelDetails;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 *
 * @author Mr.Neutral
 */
public class NovelDetailsRetriever {

    private static final String URL = "https://www.novelupdates.com/series/";

    public static NovelDetails getNovelDetails(Novel novel) {
        NovelDetails details = new NovelDetails();
        String novelURL = novel.getTitle()
                .replace(" ", "-")
                .replace(":", "")
                .replace("(Completed)", "")
                .toLowerCase();
        App.LOGGER.log(Level.FINER, "Getting details for {0} with URL: {1}", new Object[]{novel.getTitle(), novelURL});
        Document doc;

        try {
            doc = Jsoup.connect(URL + novelURL).get();
        } catch (IOException e) {
            App.LOGGER.log(Level.SEVERE, e.toString(), e);
            return null;
        }

        details.setImageURL(doc.getElementsByClass("seriesimg").get(0).child(0).attr("src"));

        List<String> authors = new ArrayList<>();
        doc.getElementById("showauthors").children().select("#authtag").forEach((e) -> {
            authors.add(e.text());
        });
        details.setAuthors(authors);

        details.setDescription(doc.getElementById("editdescription").child(0).text());

        details.setEnglishPublisher(doc.getElementById("myepub").text());

        details.setOriginalPublisher(doc.getElementById("myopub").text());

        details.setType(
                doc.getElementById("showtype").children().get(0).text()
                + " "
                + doc.getElementById("showtype").children().get(1).text());

        details.setYear(doc.getElementById("edityear").text());

        details.setLanguage(doc.getElementById("showlang").child(0).text());

        details.setRating(doc.getElementsByClass("uvotes").text().replace("(", "").replace(")", ""));

        for (Element e : doc.getElementsByClass("seriesother")) {
            if (e.text().equals("Release Frequency")) {
                details.setReleaseFrequency(e.nextSibling().toString());
                break;
            }
        }

        List<String> genre = new ArrayList<>();
        doc.getElementById("seriesgenre").children().forEach(e -> genre.add(e.text()));
        details.setGenre(genre);

        List<String> tags = new ArrayList<>();
        doc.getElementById("showtags").children().forEach(e -> tags.add(e.text()));
        details.setTags(tags);

        return details;
    }

    public static void main(String[] args) {
        System.out.println(getNovelDetails(new Novel("12 Hours After")));
    }

}
