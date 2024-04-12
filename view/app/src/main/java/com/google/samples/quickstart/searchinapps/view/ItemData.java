package com.google.samples.quickstart.searchinapps.view;

public class ItemData {
    private String snippet;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    private String category;

    public ItemData(String snippet, String headline, int heroImage) {
        this.snippet = snippet;
        this.headline = headline;
        this.heroImage = heroImage;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    private String headline;

    public int getHeroImage() {
        return heroImage;
    }

    public void setHeroImage(int heroImage) {
        this.heroImage = heroImage;
    }

    private int heroImage;

    public ItemData(String title, String headline) {
        this.snippet = title;
        this.headline = headline;
    }

    public String getSnippet() {
        return snippet;
    }

    public String getHeadline() {
        return headline;
    }
}
