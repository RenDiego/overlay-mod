package com.github.WrongCoy.overlay.gui;

import com.github.WrongCoy.overlay.OverlayMod;
import com.github.WrongCoy.overlay.hud.OverlayHUD;
import com.github.WrongCoy.overlay.utils.Utils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

import java.io.IOException;

public class OverlayGUI extends GuiScreen {

    private boolean dragging;
    private int x2, y2;

    @Override
    public void initGui() {
        this.buttonList.add(new GuiButton(0, width / 2 - 50, height / 2, 100, 20, "Settings"));
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        String text = OverlayMod.isOutdated ? "Overlay mod v@VER@ (Outdated)" : "Overlay mod v@VER@";
        int textWidth = fontRendererObj.getStringWidth(text) / 2;
        GlStateManager.pushMatrix();

        GlStateManager.scale(2, 2, 1);
        fontRendererObj.drawStringWithShadow(text, width / 4f - textWidth, height / 4f - 20f, -1);

        GlStateManager.popMatrix();

        if (OverlayMod.INSTANCE.isRainbow()) {
            Utils.drawRainbowString("You can drag and move the Overlay", (width - fontRendererObj.getStringWidth("You can drag and move the Overlay")) / 2, height / 2 - 15, 5, 1f, 1f);
        } else {
            fontRendererObj.drawStringWithShadow("You can drag and move the Overlay", (width - fontRendererObj.getStringWidth("You can drag and move the Overlay")) / 2f, height / 2f - 15, -1);
        }

        OverlayHUD.INSTANCE.draw();

        if (dragging) {
            OverlayHUD.x = mouseX - x2;
            OverlayHUD.y = mouseY - y2;
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        dragging = false;
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (this.buttonList.get(0).mousePressed(mc, mouseX, mouseY) && mouseButton == 0) {
            mc.displayGuiScreen(new SettingsMenu());
        }
        if (OverlayHUD.INSTANCE.isMouseOver(mouseX, mouseY) && mouseButton == 0) {
            dragging = true;
            x2 = mouseX - OverlayHUD.x;
            y2 = mouseY - OverlayHUD.y;
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void onGuiClosed() {
        OverlayMod.INSTANCE.getConfigManager().save();
        super.onGuiClosed();
    }
}
