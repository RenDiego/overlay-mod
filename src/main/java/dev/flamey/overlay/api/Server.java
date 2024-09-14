package dev.flamey.overlay.api;

public enum Server {

    JARTEX,
    PIKA,
    NONE;

    public String getURL() {
        switch (this) {
            case JARTEX:
                return "https://stats.jartexnetwork.com/api";
            case PIKA:
                return "https://stats.pika-network.net/api";
            case NONE:
                return "";
        }
        return "";
    }

}
