package com.github.WrongCoy.overlay.hud;

import com.github.WrongCoy.overlay.OverlayMod;
import com.github.WrongCoy.overlay.api.API;
import com.github.WrongCoy.overlay.api.Profile;
import com.github.WrongCoy.overlay.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings("all")
public class OverlayHUD {

    public static OverlayHUD INSTANCE = new OverlayHUD();
    public final List<Profile> profiles = new CopyOnWriteArrayList<>();
    public static int x = 10, y = 10, width = 135, height = 20;
    private final Minecraft mc = Minecraft.getMinecraft();
    public static boolean toggled;
    private boolean fetching;

    int offset;
    public void draw() {
        Utils.drawRect(x, y, width, height, new Color(0, 0, 0, OverlayMod.INSTANCE.getOpacity()).getRGB());

        if (OverlayMod.INSTANCE.isRainbow()) {
            Utils.drawRainbowString("ign", x + 7, y + 7, 5, 1, 1, 2000);
        } else {
            mc.fontRendererObj.drawStringWithShadow("ign", x + 7, y + 7, -1);
        }

        if (OverlayMod.INSTANCE.isRainbow()) {
            Utils.drawRainbowString("fkdr", x + width - (mc.fontRendererObj.getStringWidth("fkdr") / 2) - (35 / 2) - 70, y + 7, 5, 1, 1, 1500);
            Utils.drawRainbowString("kdr", x + width - (mc.fontRendererObj.getStringWidth("kdr") / 2) - (35 / 2) - 35, y + 7, 5, 1, 1, 1000);
            Utils.drawRainbowString("wlr", x + width - (mc.fontRendererObj.getStringWidth("wlr") / 2) - (35 / 2), y + 7, 5, 1, 1, 500);
        } else {
            mc.fontRendererObj.drawStringWithShadow("fkdr", x + width - (mc.fontRendererObj.getStringWidth("fkdr") / 2f) - (35 / 2f) - 70, y + 7, -1);
            mc.fontRendererObj.drawStringWithShadow("wlr", x + width - (mc.fontRendererObj.getStringWidth("wlr") / 2f) - (35 / 2f), y + 7, -1);
            mc.fontRendererObj.drawStringWithShadow("kdr", x + width - (mc.fontRendererObj.getStringWidth("kdr") / 2f) - (35 / 2f) - 35, y + 7, -1);
        }

        offset = height;

        profiles.forEach(player -> {
            drawRow(x, y + offset, player);
            offset += height;
        });
    }

    private void drawRow(int x, int y, Profile player) {
        int opacity = OverlayMod.INSTANCE.getOpacity();
        if (player.nicked || player.bedrock) {
            Utils.drawRect(x, y, width, height, new Color(155, 0, 0, opacity).getRGB());
        } else if (player.statsOff) {
            Utils.drawRect(x, y, width, height, new Color(155, 155, 155, opacity).getRGB());
        } else {
            Utils.drawRect(x, y, width, height, new Color(0, 0, 0, opacity).getRGB());
        }

        String name = player.nicked ? String.format("§e[NICKED]§r %s", player.displayName) : String.format("§8[%s§8] §r%s", player.rank.level, player.rank.rankDisplay.replace("&", "§") + player.displayName);
        String clanName = player.clan == null ? "" : String.format(" §7[%s]", player.clan.tag.isEmpty() ? player.clan.name : player.clan.tag);

        mc.fontRendererObj.drawStringWithShadow(name + clanName, x + 7, y + 6, -1);

        mc.fontRendererObj.drawStringWithShadow(player.fkdr, x + width - (mc.fontRendererObj.getStringWidth(player.fkdr) / 2f) - (35 / 2f) - 70, y + 6, -1);
        mc.fontRendererObj.drawStringWithShadow(player.kdr, x + width - (mc.fontRendererObj.getStringWidth(player.wlr) / 2f) - (35 / 2f) - 35, y + 6, -1);
        mc.fontRendererObj.drawStringWithShadow(player.wlr, x + width - (mc.fontRendererObj.getStringWidth(player.wlr) / 2f) - (35 / 2f), y + 6, -1);
    }

    private void fetch() throws Exception {
        fetching = true;
        final CopyOnWriteArrayList<NetworkPlayerInfo> players = new CopyOnWriteArrayList<>(mc.getNetHandler().getPlayerInfoMap());
        for (NetworkPlayerInfo player : players) {
            String name = player.getGameProfile().getName();
            Profile profile = new Profile(name.trim());
            profiles.add(profile);
        }
        for (Profile profile : profiles) {
            API.getProfile(profile);
            API.getInfo(profile);
            if (profile.nicked) {
                Utils.warn(String.format("Player %s is NICKED!", profile.username));
            } else if (profile.bedrock) {
                Utils.warn(String.format(EnumChatFormatting.DARK_RED + "Player %s is Hacking!!", profile.username));
                Utils.debug(String.format("§d[%s] §c%s§r: §a%s §eFKD - §a%s §eWLR - §a%s §eKDR", profiles.indexOf(profile) + 1, profile.username, profile.fkdr, profile.wlr, profile.kdr));
            } else {
                Utils.debug(String.format("§d[%s] §3%s§r: §a%s §eFKD - §a%s §eWLR - §a%s §eKDR", profiles.indexOf(profile) + 1, profile.username, profile.fkdr, profile.wlr, profile.kdr));
            }
            adjustWidth();
        }
        fetching = false;
    }

    public void fetch(String username) throws Exception {
        if (fetching) return;
        Profile profile = new Profile(username);
        profiles.add(profile);
        API.getProfile(profile);
        API.getInfo(profile);
        if (profile.nicked) {
            Utils.warn("§e" + username + "§r is nicked!");
            profiles.add(profile);
            return;
        } else if (profile.bedrock) {
            Utils.warn(String.format(EnumChatFormatting.DARK_RED + "Player %s is HACKING!!", username));
        }
        Utils.debug(String.format("§3%s§r: §a%s §eFKD - §a%s §eWLR - §a%s §eKDR", profile.username, profile.fkdr, profile.wlr, profile.kdr));
    }

    public void reload() {
        Utils.warn("Reloading...");
        profiles.clear();
        toggled = true;
        Runnable runnable = () -> {
            try {
                Thread.sleep(500);
                fetch();
                sort();
            } catch (Exception e) {
                Utils.warn("Failed to reload players");
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void sort() {
        final CopyOnWriteArrayList<NetworkPlayerInfo> players = new CopyOnWriteArrayList<>(mc.getNetHandler().getPlayerInfoMap());
        for (NetworkPlayerInfo p : players) {
            Profile profile = profiles.stream().filter(profile1 -> profile1.username.equals(p.getGameProfile().getName())).findFirst().orElse(null);
            if (profile != null) {
                profile.displayName = ScorePlayerTeam.formatPlayerName(p.getPlayerTeam(), p.getGameProfile().getName());
            }
        }
        Set<String> names = players.stream().map(player -> player.getGameProfile().getName()).collect(Collectors.toSet());
        profiles.removeIf(player -> !names.contains(player.username));
        profiles.sort(Comparator.comparing(Profile::getDisplayName));
    }

    public void adjustWidth() {
        Profile p1 = profiles.stream()
                .max(Comparator.comparingInt(p -> p.nicked ? String.format("[NICKED] %s", p.username).length() : String.format("[%s] %s %s %s", p.rank.level, p.rank.rankDisplay, p.username, p.clan == null ? "" : p.clan.tag.isEmpty() ? p.clan.name : p.clan.tag).length()))
                .orElse(null);
        if (p1 != null) {
            String uhh = p1.nicked ? String.format("[NICKED] %s", p1.username) : String.format("[%s] %s [%s]", p1.rank.level, p1.rank.rankDisplay.replace("&", "§") + p1.username, p1.clan == null ? "" : p1.clan.tag.isEmpty() ? "[" + p1.clan.name + "]" : "[" + p1.clan.tag + "]");
            int usernameLength = mc.fontRendererObj.getStringWidth(uhh) - 20;
            width = 135 + usernameLength;
        }
    }

    public void onChat(IChatComponent component) {
        String message = component.getUnformattedText();

        Pattern joinPattern = null, leavePattern = null;
        switch (OverlayMod.INSTANCE.getServer()) {
            case PIKA:
                joinPattern = Pattern.compile("BedWars ► (.*?)has joined!");
                leavePattern = Pattern.compile("BedWars ► (.*?)has quit!");
                break;
            case JARTEX:
                joinPattern = Pattern.compile("BedWars ❖ (.*?)has joined the game!");
                leavePattern = Pattern.compile("BedWars ❖ (.*?)has left the game!");
                break;
        }

        // NOTE: this method will not be called if the OverlayMod.INSTANCE.getServer() is NONE
        Matcher joinMatcher = joinPattern.matcher(message);
        Matcher leaveMatcher = leavePattern.matcher(message);

        if (joinMatcher.find()) {
            String username = joinMatcher.group(1).trim();
            if (username.contains(mc.thePlayer.getGameProfile().getName())) {
                profiles.clear();
                try {
                    toggled = true;
                    Runnable fetchRunnable = () -> {
                        try {
                            Thread.sleep(500);
                            fetch();
                        } catch (Exception e) {
                            Utils.warn("Failed to fetch players");
                            e.printStackTrace();
                        }
                    };
                    Thread thread = new Thread(fetchRunnable);
                    thread.start();
                } catch (Exception e) {
                    Utils.warn("Failed to fetch players: " + e.getMessage());
                    e.printStackTrace();
                }
                return;
            } else {
                Runnable join = () -> onJoin(username);
                Thread thread = new Thread(join);
                thread.start();
                return;
            }
        }

        if (leaveMatcher.find()) {
            String username = leaveMatcher.group(1);
            onLeave(username);
            return;
        }

        if (message.equals(mc.thePlayer.getGameProfile().getName().trim() + " reconnected!")) {
            try {
                toggled = true;
                Runnable fetchRunnable = () -> {
                    try {
                        Thread.sleep(500);
                        fetch();
                    } catch (Exception e) {
                        Utils.warn("Failed to fetch players");
                        e.printStackTrace();
                    }
                };
                Thread thread = new Thread(fetchRunnable);
                thread.start();
                return;
            } catch (Exception e) {
                Utils.warn("Failed to fetch players: " + e.getMessage());
                e.printStackTrace();
            }
        }

        if (component.getFormattedText().equals("§r                 §r§f§m---§r §r§6§lThe game has started! §r§f§m---§r") || component.getFormattedText().equals("§r                  §r§e§nGoodluck with your BedWars Game§r")) {
            Runnable sorta = () -> {
                try {
                    Thread.sleep(1000);
                    sort();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            };
            Thread thread = new Thread(sorta);
            thread.start();
        }

    }

    public void onJoin(String username) {
        try {
            fetch(username);
        } catch (Exception e) {
            Utils.warn("Failed to fetch player: " + e.getMessage());
            e.printStackTrace();
        }
        adjustWidth();
    }

    public void onLeave(String username) {
        profiles.removeIf(player -> player.username.equals(username.trim()));
        adjustWidth();
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + (height + offset);
    }

}
