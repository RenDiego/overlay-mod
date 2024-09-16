package com.github.WrongCoy.overlay.gui;

import com.github.WrongCoy.overlay.OverlayMod;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class SettingsMenu extends GuiScreen {

    private GuiButton keyButton, rainbowButton, debugButton;
    private GuiSlider opacitySlider;
    private boolean listening;

    @Override
    public void initGui() {
        keyButton = new GuiButton(1, width / 2 - 100, height / 2, 100, 20, getKeyBindButtonName());
        rainbowButton = new GuiButton(2, width / 2 - 100, height / 2 + 22, 100, 20, String.format("Rainbow: %s", OverlayMod.INSTANCE.isRainbow()));
        debugButton = new GuiButton(3, width / 2 - 100, height / 2 + 44, 100, 20, String.format("Debug: %s", OverlayMod.INSTANCE.isDebug()));
        opacitySlider = new GuiSlider(4, width / 2 + 2, height / 2, 100, 20, "Opacity", 0, 255, OverlayMod.INSTANCE.getOpacity());
        this.buttonList.add(keyButton);
        this.buttonList.add(rainbowButton);
        this.buttonList.add(debugButton);
        this.buttonList.add(opacitySlider);
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        GlStateManager.pushMatrix();
        GlStateManager.scale(2, 2, 1);
        fontRendererObj.drawStringWithShadow("Settings", width / 4f - (fontRendererObj.getStringWidth("Settings") / 2f), height / 4f - 20f, -1);
        GlStateManager.popMatrix();

        super.drawScreen(mouseX, mouseY, partialTicks);
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

    private String getKeyBindButtonName() {
        return listening ? "Keybind: ..." : String.format("Keybind: %s", Keyboard.getKeyName(OverlayMod.key));
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
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void updateScreen() {
        keyButton.displayString = getKeyBindButtonName();
        rainbowButton.displayString = "Rainbow: " + OverlayMod.INSTANCE.isRainbow();
        debugButton.displayString = "Debug: " + OverlayMod.INSTANCE.isDebug();
        OverlayMod.INSTANCE.setOpacity(opacitySlider.getValueAsInt());
        super.updateScreen();
    }

    @Override
    public void onGuiClosed() {
        OverlayMod.INSTANCE.getConfigManager().save();
        super.onGuiClosed();
    }

}
