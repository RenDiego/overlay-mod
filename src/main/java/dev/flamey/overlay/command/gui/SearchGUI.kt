package dev.flamey.overlay.command.gui

import dev.flamey.overlay.Overlay
import dev.flamey.overlay.api.API
import dev.flamey.overlay.api.server.SupportedServer
import dev.flamey.overlay.utils.Utils
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiTextField
import org.json.JSONObject
import org.lwjgl.opengl.GL11
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.util.Date

class SearchGUI : GuiScreen() {

    private lateinit var textField: GuiTextField
    private lateinit var searchButton: GuiButton
    private lateinit var server: SupportedServer
    private var results = ArrayList<String>()

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

        var offset = 0
        for (result in results) {
            fontRendererObj.drawStringWithShadow(
                result,
                this.width / 2f - 80,
                ((this.height - fontRendererObj.FONT_HEIGHT)) / 2 + 30f + offset,
                -1
            )
            offset += fontRendererObj.FONT_HEIGHT
        }

        textField.drawTextBox()
        searchButton.drawButton(mc, mouseX, mouseY)

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
                val username = textField.text.trim()
                if (username.isEmpty() || server == SupportedServer.NONE) return

                results.clear()
                try {
                    search(username)
                } catch (e: Exception) {
                    e.printStackTrace()
                    results.add("Error: $e")
                }
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        textField.textboxKeyTyped(typedChar, keyCode)
        super.keyTyped(typedChar, keyCode)
    }

    private fun search(username: String) {
        val connection = API.connect(username, server)

        if (connection.responseCode != HttpURLConnection.HTTP_OK) {
            results.add("${connection.responseCode} - Not found.")
            return
        }

        val connectionResult: String
        BufferedReader(
            InputStreamReader(connection.inputStream)
        ).use { reader -> connectionResult = reader.readLine() }

        val json = JSONObject(connectionResult)

        val profile = API.getProfile(json, false, server)
        results.add("${profile.rank.rankDisplay.replace("&", "§") + profile.username}§r | [${Overlay.getLevelColor(profile.rank.level) + profile.rank.level + "§r] " + profile.rank.percentage}%")
        results.add("§eFKDR: §r${Overlay.getFKDRColor(profile.fkdr) + (profile.fkdr)}")
        results.add("§eClan Name: §r${profile.clanName ?: "None"}")
        results.add("§eLast Seen: §r${Date(profile.lastSeen)}")
        results.add("§eFriends: §r${if (profile.friends.isNotEmpty()) profile.friends.joinToString(", ") else "Lonely :("}")

    }

    override fun updateScreen() {
        textField.updateCursorCounter()
        super.updateScreen()
    }

}