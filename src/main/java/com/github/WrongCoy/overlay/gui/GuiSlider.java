package com.github.WrongCoy.overlay.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.opengl.GL11;

public class GuiSlider extends GuiButton {

    private final double minVal, maxVal;
    private double currentVal;
    private boolean dragging = false;
    private final String text;

    public int getHoverState(boolean isMouseOver) {
        return 0;
    }

    public GuiSlider(int id, int x, int y, int width, int height, String text, int minVal, int maxVal, int currentVal) {
        super(id, x, y, width, height, text);
        this.minVal = minVal;
        this.maxVal = maxVal;
        this.currentVal = (currentVal - this.minVal) / (this.maxVal - this.minVal);
        this.text = text;
        this.updateSlider();
    }

    public void updateSlider() {
        if (this.currentVal < 0.0) {
            this.currentVal = 0.0;
        }
        if (this.currentVal > 1.0) {
            this.currentVal = 1.0;
        }
        this.displayString = getText();
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            this.currentVal = (double) (xPosition - (this.xPosition + 4)) / (this.width - 8);
            this.updateSlider();
            dragging = true;
            return true;
        }
        return false;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        dragging = false;
        super.mouseReleased(mouseX, mouseY);
    }

    @Override
    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
        if (this.dragging) {
            this.currentVal = (double) (mouseX - (this.xPosition + 4)) / (this.width - 8);
            this.updateSlider();
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.drawTexturedModalRect(this.xPosition + (int)(this.currentVal * (this.width - 8)), this.yPosition, 0, 66, 4, 20);
        this.drawTexturedModalRect(this.xPosition + (int)(this.currentVal * (this.width - 8)) + 4, this.yPosition, 196, 66, 4, 20);
        super.mouseDragged(mc, mouseX, mouseY);
    }

    public String getText() {
        return this.text + ": " + this.getValueAsInt();
    }

    public int getValueAsInt() {
        return (int) ((this.maxVal - this.minVal) * this.currentVal + this.minVal);
    }

    public void setCurrentVal(double currentVal) {
        this.currentVal = (currentVal - this.minVal) / (this.maxVal - this.minVal);
    }
}
