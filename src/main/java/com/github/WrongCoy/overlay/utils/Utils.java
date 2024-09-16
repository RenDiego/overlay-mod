package com.github.WrongCoy.overlay.utils;

import com.github.WrongCoy.overlay.OverlayMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ChatComponentText;

import java.awt.*;


public class Utils {

    public static Minecraft mc = Minecraft.getMinecraft();

    public static void drawRainbowString(String text, int x, int y, int seconds, float saturation, float brightness) {
        long a = 0L;
        for (int f = 0; f < text.length(); ++f) {
            final int hue = Color.getHSBColor(((System.currentTimeMillis() + a) % (seconds * 1000L)) / (seconds * 1000f), saturation, brightness).getRGB();
            mc.fontRendererObj.drawStringWithShadow(String.valueOf(text.charAt(f)), x, y, hue);
            x += mc.fontRendererObj.getCharWidth(text.charAt(f));
            a -= 90L;
        }
    }

    public static void drawRainbowString(String text, int x, int y, int seconds, float saturation, float brightness, long offset) {
        long a = 0L;
        for (int f = 0; f < text.length(); ++f) {
            final int hue = Color.getHSBColor((((System.currentTimeMillis() + a) + offset) % (seconds * 1000L)) / (seconds * 1000f), saturation, brightness).getRGB();
            mc.fontRendererObj.drawStringWithShadow(String.valueOf(text.charAt(f)), x, y, hue);
            x += mc.fontRendererObj.getCharWidth(text.charAt(f));
            a -= 90L;
        }
    }

    public static void drawRect(int x, int y, int width, int height, int color) {
        Gui.drawRect(x, y, x + width, y + height, color);
    }

    public static void warn(String message) {
        if (mc.thePlayer != null) {
            mc.thePlayer.playSound("random.orb", 1.0F, 1.0F);
            mc.thePlayer.addChatMessage(new ChatComponentText("§f[§dOverlay§f]§r " + message));
        }
    }

    public static void debug(String message) {
        if (mc.thePlayer != null && OverlayMod.INSTANCE.isDebug()) {
            mc.thePlayer.addChatMessage(new ChatComponentText("§f[§eDEBUG§f]§r " + message));
        }
    }

}
