package dev.flamey.overlay.api;

public class Rank {

    public String rankDisplay, percentage;
    public String level;

    public Rank(String rankDisplay, String level, String percentage) {
        this.rankDisplay = rankDisplay;
        this.level = level;
        this.percentage = percentage;
    }
}
