package dev.flamey.overlay

import dev.flamey.overlay.api.API
import dev.flamey.overlay.api.player.Profile
import dev.flamey.overlay.api.server.Bedwars
import dev.flamey.overlay.utils.Utils
import net.minecraft.client.Minecraft
import net.minecraft.scoreboard.ScorePlayerTeam
import net.minecraftforge.client.event.ClientChatReceivedEvent
import java.awt.Color
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.concurrent.thread

object Overlay {

    var x = 10; var y = 10; var width = 200; var height = 20
    private val profiles = CopyOnWriteArrayList<Profile>()
    private val mc = Minecraft.getMinecraft()
    var offset = 0
    private var mode = Bedwars.NONE

    fun draw() {
        Utils.drawRect(x, y, width, height, Color(0, 0, 0, 125))
        mc.fontRendererObj.drawStringWithShadow(
            "ign",
            x + 7f,
            y + 7f,
            -1
        )

        mc.fontRendererObj.drawStringWithShadow(
            "fkdr",
            x + width - mc.fontRendererObj.getStringWidth("fkdr") - 5.5f,
            y + 7f,
            -1
        )
        offset = height

        profiles.forEach { profile ->
            drawRow(y + offset, profile)
            offset += height
        }
    }

    private fun drawRow(y: Int, profile: Profile) {
        Utils.drawRect(x, y, width, height, Color(0, 0, 0, 125))

        val level = profile.rank.level
        val fkdr = BigDecimal(profile.fkdr).setScale(1, RoundingMode.HALF_UP).toDouble()

        val rankDisplay = profile.rank.rankDisplay.replace("&", "§")

        mc.fontRendererObj.drawStringWithShadow(
            if (profile.nicked) {
                "§e[NICK]§r ${rankDisplay + profile.displayName}"
            } else {
                "§8[${getLevelColor(level) + level}§8]§r ${rankDisplay + profile.displayName}"
            },
            x + 7f,
            y + 7f,
            -1
        )

        mc.fontRendererObj.drawStringWithShadow(
            if (profile.nicked) "§7§k" + "-" else getFKDRColor(fkdr) + fkdr.toString(),
            (x + width) - 35f / 2 - mc.fontRendererObj.getStringWidth(if (profile.nicked) "-" else getFKDRColor(fkdr) + fkdr.toString()) / 2,
            y + 7f,
            -1
        )

    }

    fun chat(e: ClientChatReceivedEvent) {

        val msg = e.message.formattedText
        val unformattedmsg = e.message.unformattedText

        val joinPattern = Regex("BedWars ❖ (\\w+) has joined the game! \\(\\d+/\\d+\\)")
        val leavePattern = Regex("BedWars ❖ (\\w+) has left the game! \\(\\d+/\\d+\\)")

        when {
            joinPattern.matches(unformattedmsg) -> {
                val username = joinPattern.find(unformattedmsg)?.groupValues?.get(1).toString()
                join(username)
            }
            leavePattern.matches(unformattedmsg) -> {
                val username = leavePattern.find(unformattedmsg)?.groupValues?.get(1).toString()
                leave(username)
            }
            msg.equals("§r                 §r§f§m---§r §r§6§lThe game has started! §r§f§m---§r") -> {
                println("START")
                thread(start = true) {
                    Thread.sleep(1500)
                    val players = CopyOnWriteArrayList(mc.thePlayer.sendQueue.playerInfoMap)
                    players.forEach { p ->
                        profiles.find { it.username == p.gameProfile.name }?.let { profile ->
                            println("player found ${profile.username}")
                            profile.displayName = ScorePlayerTeam.formatPlayerName(p.playerTeam, p.gameProfile.name)
                        }
                    }
                    profiles.sortByDescending { it.displayName }
                }
            }
        }

        when {
            msg.contains("§r§6§nBW1-") -> {
                reset()
                mode =
                    Bedwars.SOLO
            }

            msg.contains("§r§6§nBW2-") -> {
                reset()
                mode = Bedwars.DOUBLES
            }

            msg.contains("§r§6§nBW4-") -> {
                reset()
                mode = Bedwars.QUAD
            }

            else -> return
        }

        if (mode != Bedwars.NONE) {
            thread(start = true) {
                fetch()
            }
        }

    }

    private fun leave(username: String) {
        println("player $username has left")
        profiles.removeIf { it.username == username }
    }

    private fun join(username: String) {
        println("player $username has joined")
        profiles.sortBy { it.username }
        if (!(profiles.any { it.username == username }) && username != mc.thePlayer.name) {
            thread(start = true) {
                val profile = API.getProfile(username, mode)
                profiles.add(profile)
            }
        }
    }

    private fun fetch() {
        val players = CopyOnWriteArrayList(mc.thePlayer.sendQueue.playerInfoMap)
        for (player in players) {
            val profile = API.getProfile(player.gameProfile.name, mode)
            profiles.addIfAbsent(profile)
        }
        profiles.sortByDescending { it.fkdr }
    }

    fun reset() {
        println("RESET")
        mode = Bedwars.NONE
        profiles.clear()
    }

    private fun getFKDRColor(fkdr: Double) : String {
        return when (fkdr) {
            in 0.0..1.0 -> "§2"
            in 1.0..5.0 -> "§a"
            else -> "§4"
        }
    }

    private fun getLevelColor(level: Int) : String {
        return when (level) {
            in 1..5 -> "§7"
            in 5..10 -> "§f"
            in 10..15 -> "§b"
            in 15..20 -> "§a"
            in 20..25 -> "§e"
            in 25..30 -> "§d"
            in 30..35 -> "§9"
            in 35..40 -> "§c"
            in 40..45 -> "§6"
            in 45..50 -> "§2"
            in 50..55 -> "§4"
            in 55..60 -> "§3"
            in 65..75 -> "§l§e"
            in 75..100 -> "§l§6"
            else -> "§k"
        }
    }

}