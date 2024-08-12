package dev.flamey.overlay.command.gui

import dev.flamey.overlay.Main
import dev.flamey.overlay.Overlay
import dev.flamey.overlay.utils.Utils
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11

class CommandGUI : GuiScreen() {

    val overlay = Overlay
    private lateinit var keybindButton: GuiButton
    private lateinit var searchButton: GuiButton
    private var listening = false; var dragging = false
    var x2 = 10; var y2 = 10

    companion object {
        @JvmStatic
        var keybind: Int = Keyboard.KEY_NONE
    }

    override fun initGui() {
        keybindButton = GuiButton(
            1,
            ((this.width - 100) / 2),
            ((this.height - 20) / 2),
            100,
            20,
            "Keybind: ${Keyboard.getKeyName(keybind)}"
        )
        searchButton = GuiButton(
            2,
            this.width - 210,
            this.height - 30,
            200,
            20,
            "Search for a player"
        )
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, p_drawScreen_3_: Float) {
        this.drawDefaultBackground()

        overlay.draw()

        GL11.glPushMatrix()

        GL11.glScaled(2.0, 2.0, 2.0)

        this.drawCenteredString(this.fontRendererObj, "bedwars overlay mod", (this.width / 2) / 2, ((this.height - fontRendererObj.FONT_HEIGHT) / 2) / 2 - 20, -1)

        GL11.glPopMatrix()

        fontRendererObj.drawStringWithShadow(
            "You can drag & move the overlay",
            ((this.width - fontRendererObj.getStringWidth("You can drag & move the overlay")) / 2).toFloat(),
            ((this.height - fontRendererObj.FONT_HEIGHT) / 2).toFloat() - 18,
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
        keybindButton.displayString = if (listening) "Keybind: ..." else "Keybind: ${Keyboard.getKeyName(keybind)}"
    }

}