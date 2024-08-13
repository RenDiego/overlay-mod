package dev.flamey.overlay.api

import dev.flamey.overlay.Main
import dev.flamey.overlay.api.player.Profile
import dev.flamey.overlay.api.player.Rank
import dev.flamey.overlay.api.server.SupportedServer
import net.minecraft.client.Minecraft
import net.minecraft.util.ChatComponentText
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.CopyOnWriteArrayList


object API {

    private val fetchedProfiles = CopyOnWriteArrayList<Profile>()

    init {
        HttpURLConnection.setFollowRedirects(true)
    }

    fun getProfile(username: String, server: SupportedServer = Main.server, infetched: Boolean = true): Profile {

        if (infetched) {
            fetchedProfiles.find { it.username == username }?.let {
                return it
            }
        }

        val connection = connect(username, server)

        if (connection.responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
            val profile = Profile(
                username,
                rank = Rank(0, 0, 0, ""),
                nicked = true,
                fkdr = 0.0
            )
            fetchedProfiles.add(profile)
            return profile
        } else if (connection.responseCode == 429) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(ChatComponentText("[Overlay] got rate limited bruh"))
        }

        val result: String

        BufferedReader(
            InputStreamReader(connection.inputStream)
        ).use { reader -> result = reader.readLine() }

        val json = JSONObject(result)
        val rank = json.getJSONObject("rank")
        val profile = Profile(
            username,
            rank = Rank(
                rank.getInt("level"),
                rank.getInt("experience"),
                rank.getInt("percentage"),
                rank.getString("rankDisplay")
            ),
            nicked = false,
            fkdr = getFKDR(username, server),
            bedrock = username.startsWith(".")
        )
        fetchedProfiles.add(profile)
        return profile

    }

    fun getProfile(json: JSONObject, infetched: Boolean, server: SupportedServer = Main.server): Profile {
        val username = json.getString("username")
        if (infetched) {
            fetchedProfiles.find { it.username == username }?.let {
                return it
            }
        }

        println(json)

        val rank = json.getJSONObject("rank")
        val profile = Profile(
            username,
            rank = Rank(
                rank.getInt("level"),
                rank.getInt("experience"),
                rank.getInt("percentage"),
                rank.getString("rankDisplay")
            ),
            nicked = false,
            fkdr = getFKDR(username, server),
            clanName = json.optJSONObject("clan")?.getString("name"),
            discordBoosting = json.getBoolean("discord_boosting"),
            discordVerified = json.getBoolean("discord_verified"),
            emailVerified = json.getBoolean("email_verified"),
            lastSeen = json.getLong("lastSeen"),
            friends = json.getJSONArray("friends").map { it as JSONObject; it.getString("username") }.toTypedArray()
        )
        fetchedProfiles.add(profile)
        return profile
    }

    fun connect(username: String, server: SupportedServer = Main.server): HttpURLConnection {
        val url = URL("${getURL(server)}/profile/$username")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty(
            "User-Agent",
            "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0"
        )
        connection.connect()

        return connection
    }

    private fun getFKDR(username: String, server: SupportedServer): Double {
        fetchedProfiles.find { it.username == username }?.let {
            return it.fkdr
        }

        val url = URL("${getURL(server)}/profile/$username/leaderboard?type=bedwars&interval=total&mode=ALL_MODES")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty(
            "User-Agent",
            "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0"
        )
        connection.connect()

        if (connection.responseCode != HttpURLConnection.HTTP_OK) {
            return 0.0
        }

        var result: String

        BufferedReader(
            InputStreamReader(connection.inputStream)
        ).use { reader -> result = reader.readLine() }

        val json = JSONObject(result)
        val finals = json.optJSONObject("Final kills")?.optJSONArray("entries")?.getJSONObject(0)?.getInt("value")
        val deaths = json.optJSONObject("Losses")?.optJSONArray("entries")?.getJSONObject(0)?.getInt("value")

        return if (finals != null) {
            if (deaths != null) {
                finals.toDouble() / deaths.toDouble()
            } else {
                finals.toDouble()
            }
        } else 0.0
    }

    fun getURL(server: SupportedServer): String {
        return when (server) {
            SupportedServer.JARTEX -> "https://stats.jartexnetwork.com/api"
            SupportedServer.PIKA -> "https://stats.pika-network.net/api"
            SupportedServer.NONE -> ""
        }
    }


}