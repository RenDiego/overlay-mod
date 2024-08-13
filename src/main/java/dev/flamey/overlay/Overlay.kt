package dev.flamey.overlay

import dev.flamey.overlay.api.API
import dev.flamey.overlay.api.player.Profile
import dev.flamey.overlay.api.server.SupportedServer
import dev.flamey.overlay.utils.Utils
import net.minecraft.client.Minecraft
import net.minecraft.client.network.NetworkPlayerInfo
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
    private var gaming = false
        set(value) {
            if (value) reset()
            field = value
        }
    var offset = 0

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
        val realmsg = e.message.unformattedText

        val server: SupportedServer? = Main.server

        val joinPattern = when (server) {
            SupportedServer.PIKA -> Regex("BedWars ► (\\w+) has joined! \\(\\d+/\\d+\\)")
            SupportedServer.JARTEX -> Regex("BedWars ❖ (\\w+) has joined the game! \\(\\d+/\\d+\\)")
            SupportedServer.NONE -> Regex("")
            null -> Regex("")
        }

        val leavePattern = when (server) {
            SupportedServer.PIKA -> Regex("BedWars ► (\\w+) has quit! \\(\\d+/\\d+\\)")
            SupportedServer.JARTEX -> Regex("BedWars ❖ (\\w+) has left the game! \\(\\d+/\\d+\\)")
            SupportedServer.NONE -> Regex("")
            null -> Regex("")
        }

        when (server) {
            SupportedServer.PIKA -> {
                when {
                    // Username has joined!
                    joinPattern.matches(realmsg) -> {
                        val username = joinPattern.find(realmsg)?.groupValues?.get(1)
                        if (username != mc.thePlayer.name) {
                            username?.let { join(it) }
                            gaming = false
                        } else {
                            gaming = true
                        }
                    }
                    leavePattern.matches(realmsg) -> {
                        val username = joinPattern.find(realmsg)?.groupValues?.get(1)
                        username?.let { leave(it) }
                        gaming = false
                    }
                    msg.equals("§r                  §r§e§nGoodluck with your BedWars Game§r") -> {
                        thread(start = true) {
                            Thread.sleep(1500)
                            val players = CopyOnWriteArrayList(mc.thePlayer.sendQueue.playerInfoMap)
                            sortByTeam(players, this.profiles)
                        }
                    }
                    else -> {
                        gaming = false
                    }
                }
            }
            SupportedServer.JARTEX -> {
                when {
                    // Username has joined!
                    joinPattern.matches(realmsg) -> {
                        val username = joinPattern.find(realmsg)?.groupValues?.get(1)
                        if (username != mc.thePlayer.name) {
                            username?.let { join(it) }
                        } else {
                            gaming = true
                        }
                    }
                    leavePattern.matches(realmsg) -> {
                        val username = joinPattern.find(realmsg)?.groupValues?.get(1)
                        username?.let { leave(it) }
                    }
                    msg.equals("§r                 §r§f§m---§r §r§6§lThe game has started! §r§f§m---§r") -> {
                        println("START")
                        thread(start = true) {
                            Thread.sleep(1500)
                            val players = CopyOnWriteArrayList(mc.thePlayer.sendQueue.playerInfoMap)
                            sortByTeam(players, this.profiles)
                        }
                    }
                    else -> {
                        gaming = false
                    }
                }
            }
            SupportedServer.NONE -> {

            }
            null -> {

            }
        }

//        when (Main.server) {
//            SupportedServer.PIKA -> {
//                val joinPattern = Regex("BedWars ► (\\w+) has joined! \\(\\d+/\\d+\\)")
//                val leavePattern = Regex("BedWars ► (\\w+) has quit! \\(\\d+/\\d+\\)")
//                when {
//                    joinPattern.matches(unformattedmsg) -> {
//                        val username = joinPattern.find(unformattedmsg)?.groupValues?.get(1)
//                        if (username != null && username != mc.thePlayer.name) join(username) else if (username != mc.thePlayer.name) {
//                            gaming = true
//                            return
//                        }
//                        println("$username has joined")
//                    }
//                    leavePattern.matches(unformattedmsg) -> {
//                        val username = leavePattern.find(unformattedmsg)?.groupValues?.get(1)
//                        if (username != null) {
//                            leave(username)
//                            println("$username has left")
//                        }
//                    }
//                    msg.equals("§r                  §r§e§nGoodluck with your BedWars Game§r") -> {
//                        thread(start = true) {
//                            Thread.sleep(1500)
//                            val players = CopyOnWriteArrayList(mc.thePlayer.sendQueue.playerInfoMap)
//                            sortByTeam(players, this.profiles)
//                        }
//                    }
//                    else -> {
//                        gaming = false
//                        return
//                    }
//                }
//            }
//            SupportedServer.JARTEX -> {
//                val joinPattern = Regex("BedWars ❖ (\\w+) has joined the game! \\(\\d+/\\d+\\)")
//                val leavePattern = Regex("BedWars ❖ (\\w+) has left the game! \\(\\d+/\\d+\\)")
//                when {
//                    joinPattern.matches(unformattedmsg) -> {
//                        val username = joinPattern.find(unformattedmsg)?.groupValues?.get(1)
//                        if (username != null && username != mc.thePlayer.name) join(username) else if (username != mc.thePlayer.name) {
//                            gaming = true
//                            return
//                        }
//                        println("$username has joined")
//                    }
//                    leavePattern.matches(unformattedmsg) -> {
//                        val username = leavePattern.find(unformattedmsg)?.groupValues?.get(1)
//                        if (username != null) {
//                            leave(username)
//                            println("$username has left")
//                        }
//                    }
//                    msg.equals("§r                 §r§f§m---§r §r§6§lThe game has started! §r§f§m---§r") -> {
//                        println("START")
//                        thread(start = true) {
//                            Thread.sleep(1500)
//                            val players = CopyOnWriteArrayList(mc.thePlayer.sendQueue.playerInfoMap)
//                            sortByTeam(players, this.profiles)
//                        }
//                    }
//                    else -> {
//                        gaming = false
//                        return
//                    }
//                }
//            }
//
//            SupportedServer.NONE -> {
//                return
//            }
//        }


        if (gaming) {
            thread(start = true) {
                fetch()
            }
            gaming = false
        }

    }

    private fun leave(username: String) {
        profiles.removeIf { it.username == username }
    }

    private fun join(username: String) {
        println("requesting from join $username")
        if (!(profiles.any { it.username == username })) {
            thread(start = true) {
                val profile = API.getProfile(username)
                profiles.add(profile)
            }
        }
    }

    private fun sortByTeam(players: List<NetworkPlayerInfo>, profiles: CopyOnWriteArrayList<Profile>) {
        players.forEach { p ->
            profiles.find { it.username == p.gameProfile.name }?.let { profile ->
                profile.displayName = ScorePlayerTeam.formatPlayerName(p.playerTeam, p.gameProfile.name)
            }
        }
        profiles.sortByDescending { it.displayName }
    }

    private fun fetch() {
        val players = CopyOnWriteArrayList(mc.thePlayer.sendQueue.playerInfoMap)
        for (player in players) {
            val profile = API.getProfile(player.gameProfile.name)
            println("requesting from fetch ${player.gameProfile.name}")
            profiles.addIfAbsent(profile)
        }
    }

    fun reset() {
        profiles.clear()
    }

    fun reload() {
        profiles.clear()
        thread(start = true) {
            fetch()
        }
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
            else -> "§l§7"
        }
    }

}