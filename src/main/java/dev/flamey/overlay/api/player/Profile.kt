package dev.flamey.overlay.api.player

data class Profile(
    var username: String,
    var displayName: String = username,
    val rank: Rank, val nicked: Boolean,
    val fkdr: Double = 0.0
)

data class Rank(val level: Int, val experience: Int, val percentage: Int, val rankDisplay: String)