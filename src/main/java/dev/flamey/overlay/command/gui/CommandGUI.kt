package dev.flamey.overlay.command

import dev.flamey.overlay.Main
import dev.flamey.overlay.Overlay
import dev.flamey.overlay.utils.Utils
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.ScaledResolution
import org.lwjgl.input.Keyboard

class CommandGUI : GuiScreen() {

    val overlay = Overlay
    private lateinit var keybindButton: GuiButton
    private lateinit var searchButton: GuiButton
    private lateinit var sr: ScaledResolution
    private var listening = false; var dragging = false
    var x2 = 10; var y2 = 10

    companion object {
        @JvmStatic
        var keybind: Int = Keyboard.KEY_NONE
    }

    override fun initGui() {
        sr = ScaledResolution(mc)
        keybindButton = GuiButton(
            1,
            ((sr.scaledWidth - 100) / 2),
            ((sr.scaledHeight - 20) / 2),
            100,
            20,
            "Keybind: ${Keyboard.getKeyName(keybind)}"
        )
        searchButton = GuiButton(
            2,
            sr.scaledWidth - 210,
            sr.scaledHeight - 30,
            200,
            20,
            "Search for a player"
        )
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, p_drawScreen_3_: Float) {
        this.drawDefaultBackground()

        overlay.draw()

        fontRendererObj.drawStringWithShadow("Flamey's Overlay",
            ((sr.scaledWidth - fontRendererObj.getStringWidth("Flamey's Overlay")) / 2).toFloat(),
            ((sr.scaledHeight - fontRendererObj.FONT_HEIGHT) / 2).toFloat() - 35,
            -1
        )

        fontRendererObj.drawStringWithShadow(
            "You can drag & move the overlay",
            ((sr.scaledWidth - fontRendererObj.getStringWidth("You can drag & move the overlay")) / 2).toFloat(),
            ((sr.scaledHeight - fontRendererObj.FONT_HEIGHT) / 2).toFloat() - 20,
            -1
        )

        keybindButton.drawButton(mc, mouseX, mouseY)
        searchButton.drawButton(mc, mouseX, mouseY)

        if (dragging) {
            Overlay.x = mouseX - x2
            Overlay.y = mouseY - y2
        }
    }

    override fun mouseClicked(p_mouseClicked_1_: Int, p_mouseClicked_2_: Int, p_mouseClicked_3_: Int) {
        when {
            keybindButton.isMouseOver -> {
                listening = true
            }
            Utils.isMouseOver(p_mouseClicked_1_, p_mouseClicked_2_, Overlay.x, Overlay.y, Overlay.width, Overlay.height) -> {
                dragging = true
                x2 = p_mouseClicked_1_ - Overlay.x
                y2 = p_mouseClicked_2_ - Overlay.y
            }
            searchButton.isMouseOver && p_mouseClicked_3_ == 1 -> {
                
            }
        }
        super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_2_, p_mouseClicked_3_)
    }

    override fun onGuiClosed() {
        Main.configManager.save()
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
        dragging = false
        super.mouseReleased(mouseX, mouseY, state)
    }

    override fun keyTyped(p_keyTyped_1_: Char, p_keyTyped_2_: Int) {
        if (listening) {
            keybind = p_keyTyped_2_
            listening = false
        }

        if (p_keyTyped_2_ == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(null)
            if (listening) keybind = p_keyTyped_2_
        }
    }

    override fun updateScreen() {
        sr = ScaledResolution(mc)
        keybindButton.displayString = if (listening) "Keybind: ..." else "Keybind: ${Keyboard.getKeyName(keybind)}"
    }

}