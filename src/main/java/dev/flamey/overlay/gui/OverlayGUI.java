package dev.flamey.overlay.gui;

import dev.flamey.overlay.OverlayMod;
import dev.flamey.overlay.hud.Overlay;
import dev.flamey.overlay.utils.Utils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class OverlayGUI extends GuiScreen {

    private GuiButton keyButton, rainbowButton, debugButton;
    private boolean listening, dragging;
    private int x2, y2;

    @Override
    public void initGui() {
        keyButton = new GuiButton(1, width / 2 - 50, height / 2, 100, 20, getKeyBindButtonName());
        rainbowButton = new GuiButton(2, width / 2 - 50, height / 2 + 22, 100, 20, String.format("Rainbow: %s", OverlayMod.INSTANCE.isRainbow()));
        debugButton = new GuiButton(1, width / 2 - 50, height / 2 + 44, 100, 20, String.format("Debug: %s", OverlayMod.INSTANCE.isDebug()));
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

        keyButton.drawButton(mc, mouseX, mouseY);
        rainbowButton.drawButton(mc, mouseX, mouseY);
        debugButton.drawButton(mc, mouseX, mouseY);

        Overlay.INSTANCE.draw();

        if (dragging) {
            Overlay.x = mouseX - x2;
            Overlay.y = mouseY - y2;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void updateScreen() {
        keyButton.displayString = getKeyBindButtonName();
        rainbowButton.displayString = "Rainbow: " + OverlayMod.INSTANCE.isRainbow();
        debugButton.displayString = "Debug: " + OverlayMod.INSTANCE.isDebug();
        super.updateScreen();
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        dragging = false;
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (keyButton.mousePressed(this.mc, mouseX, mouseY) && mouseButton == 0) {
            listening = !listening;
        }
        if (rainbowButton.mousePressed(this.mc, mouseX, mouseY) && mouseButton == 0) {
            OverlayMod.INSTANCE.setRainbow(!OverlayMod.INSTANCE.isRainbow());
        }
        if (debugButton.mousePressed(this.mc, mouseX, mouseY) && mouseButton == 0) {
            OverlayMod.INSTANCE.setDebug(!OverlayMod.INSTANCE.isDebug());
        }
        if (Overlay.INSTANCE.isMouseOver(mouseX, mouseY) && mouseButton == 0) {
            dragging = true;
            x2 = mouseX - Overlay.x;
            y2 = mouseY - Overlay.y;
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private String getKeyBindButtonName() {
        return listening ? "Keybind: ..." : String.format("Keybind: %s", Keyboard.getKeyName(OverlayMod.key));
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (listening) {
            if (keyCode != Keyboard.KEY_ESCAPE) {
                OverlayMod.key = keyCode;
            } else {
                OverlayMod.key = Keyboard.KEY_NONE;
                this.mc.displayGuiScreen(null);
            }
            listening = false;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void onGuiClosed() {
        OverlayMod.INSTANCE.getConfigManager().save();
        super.onGuiClosed();
    }
}
