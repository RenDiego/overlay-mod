package dev.flamey.overlay.api;

import dev.flamey.overlay.OverlayMod;
import dev.flamey.overlay.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.EnumChatFormatting;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CopyOnWriteArrayList;

public class API {

    public static CopyOnWriteArrayList<Profile> fetchedProfiles = new CopyOnWriteArrayList<>();

    public static void getProfile(Profile profile) throws Exception {
        String username = profile.username.trim();
        for (Profile player : fetchedProfiles) {
            if (player.username.equals(profile.username)) {
                profile.rank = player.rank;
                profile.bedrock = player.bedrock;
                profile.displayName = player.displayName;
                profile.nicked = player.nicked;
                profile.clan = player.clan;
                profile.statsOff = player.statsOff;
                return;
            }
        }

        HttpURLConnection connection = connect(OverlayMod.INSTANCE.getServer().getURL() + "/profile/" + username);

        if (connection.getResponseCode() == 400) {
            System.out.println(OverlayMod.INSTANCE.getServer().getURL() + "/profile/" + username + " not found (Bad Request)");
        }

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK && connection.getResponseCode() != 429) {
            Profile player = new Profile(profile.username);
            player.nicked = true;
            player.fkdr = "§7-";
            player.wlr = "§7-";
            player.kdr = "§7-";
            fetchedProfiles.addIfAbsent(player);
            return;
        } else if (connection.getResponseCode() == 429) {
            Thread.sleep(1500);
            connection = connect(OverlayMod.INSTANCE.getServer().getURL() + "/profile/" + username);
            Utils.warn("Rate limited waiting 1500ms");
        }

        String result = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();

        JSONObject json = new JSONObject(result);
        JSONObject rank = json.getJSONObject("rank");
        JSONObject clan = json.optJSONObject("clan") == null ? null : json.optJSONObject("clan");

        profile.rank = new Rank(rank.getString("rankDisplay"), getLevelColor(rank.getInt("level")) + rank.getInt("level"), rank.getInt("percentage") + "%");
        profile.clan = clan == null ? null : new Clan(clan.getString("name"), clan.getString("tag"));
        fetchedProfiles.add(profile);
    }

    public static void getInfo(Profile profile) throws IOException, InterruptedException {

        for (Profile player : fetchedProfiles) {
            if (player.username.equals(profile.username) && !player.fkdr.equals("§e..")) {
                profile.fkdr = player.fkdr;
                profile.wlr = player.wlr;
                profile.kdr = player.kdr;
                return;
            }
        }

        HttpURLConnection connection = connect(OverlayMod.INSTANCE.getServer().getURL() + "/profile/" + profile.username + "/leaderboard?type=bedwars&interval=total&mode=ALL_MODES");

        if (connection.getResponseCode() == 400) {
            System.out.println(OverlayMod.INSTANCE.getServer().getURL() + "/profile/" + profile.username + " not found (Bad Request)");
        }

        if (connection.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT) {
            Utils.warn(profile.username + " has his stats off");
            profile.statsOff = true;
            profile.fkdr = "§7-";
            profile.wlr = "§7-";
            profile.kdr = "§7-";
            return;
        } else if (connection.getResponseCode() == 429) {
            Thread.sleep(1500);
            connection = connect(OverlayMod.INSTANCE.getServer().getURL() + "/profile/" + profile.username);
        }

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            Utils.warn(connection.getResponseCode() + " Failed");
            return;
        }

        String result = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
        JSONObject json = new JSONObject(result);

        int finalDeaths = 0, finalKills = 0, deaths = 0, kills = 0, losses = 0, wins = 0;

        try {
            JSONArray winsEntries = json.getJSONObject("Wins").optJSONArray("entries");

            if (winsEntries != null) {
                wins = winsEntries.getJSONObject(0).getInt("value");
            }

            JSONArray lossesEntries = json.getJSONObject("Losses").optJSONArray("entries");

            if (lossesEntries != null) {
                losses = lossesEntries.getJSONObject(0).getInt("value");
            }

            JSONArray finalKillsEntries = json.optJSONObject("Final kills").optJSONArray("entries");
            JSONArray finalDeathsEntries = json.optJSONObject("Final deaths").optJSONArray("entries");

            if (finalKillsEntries != null) {
                finalKills = finalKillsEntries.getJSONObject(0).getInt("value");
            }

            if (finalDeathsEntries != null) {
                finalDeaths = finalDeathsEntries.getJSONObject(0).getInt("value");
            }

            JSONArray killsEntries = json.optJSONObject("Kills").optJSONArray("entries");
            JSONArray deathsEntries = json.optJSONObject("Deaths").optJSONArray("entries");

            if (killsEntries != null) {
                kills = killsEntries.getJSONObject(0).getInt("value");
            }

            if (deathsEntries != null) {
                deaths = deathsEntries.getJSONObject(0).getInt("value");
            }

            if (deathsEntries == null && winsEntries == null && finalDeathsEntries == null && finalKillsEntries == null) {
                Utils.warn(EnumChatFormatting.DARK_PURPLE + profile.username + " is a new account!");
            }

        } catch (JSONException e) {
            Utils.warn("Failed to get profile's info: " + profile.username);
            e.printStackTrace();
        }

        double fkdr = finalDeaths == 0 ? finalKills : (double) Math.round(((double) finalKills / (double) finalDeaths) * 10) / 10;
        double wlr = losses == 0 ? wins : (double) Math.round(((double) wins / (double) losses) * 10) / 10;

        double kdr = deaths == 0 ? kills : (double) Math.round(((double) kills / (double) deaths) * 10) / 10;

        profile.fkdr = getFKDRColor(fkdr) + fkdr;
        profile.wlr = getFKDRColor(wlr) + wlr;
        profile.kdr = getFKDRColor(kdr) + kdr;
    }

    public static String getFKDRColor(double fkdr) {
        if (fkdr >= 0.0 && fkdr <= 1.0) {
            return "§2";
        } else if (fkdr > 1.0 && fkdr <= 5.0) {
            return "§a";
        } else {
            return "§4";
        }
    }

    public static HttpURLConnection connect(String urlLink) throws IOException {
        URL url = new URL(urlLink);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0"
        );
        conn.connect();

        return conn;
    }


    // dude
    public static String getLevelColor(int level) {
        if (level >= 1 && level <= 5) {
            return "§7";
        } else if (level > 5 && level <= 10) {
            return "§f";
        } else if (level > 10 && level <= 15) {
            return "§b";
        } else if (level > 15 && level <= 20) {
            return "§a";
        } else if (level > 20 && level <= 25) {
            return "§e";
        } else if (level > 25 && level <= 30) {
            return "§d";
        } else if (level > 30 && level <= 35) {
            return "§9";
        } else if (level > 35 && level <= 40) {
            return "§c";
        } else if (level > 40 && level <= 45) {
            return "§6";
        } else if (level > 45 && level <= 50) {
            return "§2";
        } else if (level > 50 && level <= 55) {
            return "§4";
        } else if (level > 55 && level <= 60) {
            return "§3";
        } else if (level > 65 && level <= 75) {
            return "§l§e";
        } else if (level > 75 && level <= 100) {
            return "§l§6";
        } else {
            return "§l§7";
        }
    }

}