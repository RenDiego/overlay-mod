package dev.flamey.overlay.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;


public class Utils {

    public static void drawRect(int x, int y, int width, int height, Color color) {
        Gui.drawRect(x, y, x + width, y + height, color.getRGB());
    }

    // from GuiButton
    public static boolean isMouseOver(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }

    public static List<EntityPlayer> getPlayerFromRange(int range) {
        List<EntityPlayer> entities = Minecraft.getMinecraft().theWorld.playerEntities;
        return entities.stream().filter((entity) -> {
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            return player.getDistanceToEntity(entity) <= range;
        }).collect(Collectors.toList());
    }

}
