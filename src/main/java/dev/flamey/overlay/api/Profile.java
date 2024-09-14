package dev.flamey.overlay.api;

public class Profile {

    public boolean bedrock, nicked, statsOff;
    public String username, displayName;
    public String fkdr, wlr, kdr;
    public Clan clan;
    public Rank rank;

    public Profile(String username) {
        this.username = username;
        this.displayName = username;
        this.rank = new Rank("§7", "§e..", "0%");
        this.clan = null;
        this.nicked = false;
        this.bedrock = username.startsWith(".");
        this.statsOff = false;
        this.kdr = "§e..";
        this.wlr = "§e..";
        this.fkdr = "§e..";
    }

}
