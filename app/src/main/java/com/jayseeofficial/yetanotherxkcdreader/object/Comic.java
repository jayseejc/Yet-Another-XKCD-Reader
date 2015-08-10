package com.jayseeofficial.yetanotherxkcdreader.object;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jon on 09/08/15.
 */
public class Comic {
    private String month;

    private int num;

    private String link;

    private int year;

    private String news;

    @SerializedName("safe_title")
    private
    String safeTitle;

    private String transcript;

    @SerializedName("alt")
    private
    String altText;

    @SerializedName("img")
    private
    String imageUrl;

    private String title;

    private String day;

    public String getMonth() {
        return month;
    }

    public int getNum() {
        return num;
    }

    public String getLink() {
        return link;
    }

    public int getYear() {
        return year;
    }

    public String getNews() {
        return news;
    }

    public String getSafeTitle() {
        return safeTitle;
    }

    public String getTranscript() {
        return transcript;
    }

    public String getAltText() {
        return altText;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDay() {
        return day;
    }
}
