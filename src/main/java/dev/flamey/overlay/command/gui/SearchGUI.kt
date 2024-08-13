package dev.flamey.overlay.command.gui

import dev.flamey.overlay.api.API
import dev.flamey.overlay.api.player.Profile
import dev.flamey.overlay.api.player.Rank
import dev.flamey.overlay.api.server.SupportedServer
import dev.flamey.overlay.utils.Utils
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiTextField
import org.lwjgl.opengl.GL11

class SearchGUI : GuiScreen() {

    private lateinit var textField: GuiTextField
    private lateinit var searchButton: GuiButton
    private lateinit var server: SupportedServer
    private lateinit var profile: Profile

    override fun initGui() {
        textField = GuiTextField(
            1,
            fontRendererObj,
            (this.width / 2 - (100 / 2)) - 30,
            (this.height / 2 - (20 / 2)),
            100,
            20
        )
        textField.isFocused = true
        textField.setCanLoseFocus(true)
        searchButton = GuiButton(
            2,
            this.width / 2 + (30 / 2) + 7,
            (this.height / 2 - (20 / 2)),
            60,
            20,
            "Search"
        )
        server = SupportedServer.NONE
        profile = Profile(
            username = "",
            rank = Rank(0, 0, 0, ""),
            nicked = false,
            fkdr = 0.0
        )
        super.initGui()
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.drawDefaultBackground()

        GL11.glPushMatrix()

        GL11.glScalef(2f, 2f, 2f)

        this.drawCenteredString(
            fontRendererObj,
            "Search for a player",
            (this.width / 2) / 2,
            ((this.height - fontRendererObj.FONT_HEIGHT) / 2) / 2 - 25,
            -1
        )

        GL11.glPopMatrix()

        this.drawCenteredString(
            fontRendererObj,
            "Server: $server",
            this.width / 2,
            ((this.height - fontRendererObj.FONT_HEIGHT)) / 2 - 23,
            -1
        )

        textField.drawTextBox()
        searchButton.drawButton(mc, mouseX, mouseY)

        if (!profile.nicked && profile.username != "") {
            this.drawCenteredString(
                fontRendererObj,
                "${profile.rank.rankDisplay.replace("&", "§") + profile.username}§r | ${profile.rank.level} ${profile.rank.percentage}",
                this.width / 2,
                ((this.height - fontRendererObj.FONT_HEIGHT)) / 2 + 25,
                -1
            )
        } else if (profile.username != "") {
            this.drawCenteredString(
                fontRendererObj,
                "404 - not found.",
                this.width / 2,
                ((this.height - fontRendererObj.FONT_HEIGHT)) / 2 + 25,
                -1
            )
        }

        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        textField.mouseClicked(mouseX, mouseY, mouseButton)

        when {
            Utils.isMouseOver(mouseX, mouseY, this.width / 2 - fontRendererObj.getStringWidth("Server: $server") / 2, ((this.height - fontRendererObj.FONT_HEIGHT)) / 2 - 23, fontRendererObj.getStringWidth("Server: $server"), fontRendererObj.FONT_HEIGHT) -> {
                val values = SupportedServer.entries.toTypedArray()
                val currentIndex = values.indexOf(server)
                val nextIndex = (currentIndex + 1) % values.size
                server = values[nextIndex]
            }
            searchButton.isMouseOver -> {
                val username = textField.text
                if (username.isEmpty() || server == SupportedServer.NONE) return
                try {
                    API.getProfile(username = username, server = this.server).also { this.profile = it }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                textField.text = ""
                textField.setFocused(false)
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        textField.textboxKeyTyped(typedChar, keyCode)
        super.keyTyped(typedChar, keyCode)
    }

    override fun updateScreen() {
        textField.updateCursorCounter()
        super.updateScreen()
    }

}