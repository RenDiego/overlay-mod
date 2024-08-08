package dev.flamey.overlay.api

import dev.flamey.overlay.Main
import dev.flamey.overlay.api.player.Profile
import dev.flamey.overlay.api.player.Rank
import dev.flamey.overlay.api.server.Bedwars
import dev.flamey.overlay.api.server.SupportedServer
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.round


object API {

    init {
        HttpURLConnection.setFollowRedirects(true)
    }

    fun getProfile(username: String, bedwars: Bedwars = Bedwars.NONE) : Profile {
        val url = URL("${getURL()}/profile/$username")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0")
        connection.connect()

        if (connection.responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
            return Profile(
                username,
                rank = Rank(0, 0, 0, "-"),
                nicked = true,
                fkdr = 0.0
            )
        }

        var result: String

        BufferedReader(
            InputStreamReader(connection.inputStream)
        ).use { reader -> result = reader.readLine() }

        val json = JSONObject(result)
        val rank = json.getJSONObject("rank")
        return Profile(
            username,
            rank = Rank(
                rank.getInt("level"),
                rank.getInt("experience"),
                rank.getInt("percentage"),
                rank.getString("rankDisplay")
            ),
            nicked = false,
            fkdr = getFKDR(username, bedwars)
        )

    }

    fun getFKDR(username: String, mode: Bedwars) : Double {
        if (mode == Bedwars.NONE) return 0.0
        val url = URL("${getURL()}/profile/$username/leaderboard?type=bedwars&interval=total&mode=$mode")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0")
        connection.connect()

        if (connection.responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
            return 0.0
        }

        var result: String

        BufferedReader(
            InputStreamReader(connection.inputStream)
        ).use { reader -> result = reader.readLine() }

        var json = JSONObject(result)
        val finals = json.optJSONObject("Final kills")?.optJSONArray("entries")?.getJSONObject(0)?.getInt("value")
        val deaths = json.optJSONObject("Losses")?.optJSONArray("entries")?.getJSONObject(0)?.getInt("value")

        if (finals != null && deaths != null) {
            return (finals.toDouble() / deaths.toDouble())
        } else {
            return 0.0
        }
    }

    private fun getURL() : String {
        return when (Main.server) {
            SupportedServer.JARTEX -> "https://stats.jartexnetwork.com/api"
            SupportedServer.PIKA -> "https://stats.pika-network.net/api"
            SupportedServer.NONE -> ""
            else -> ""
        }
    }


}