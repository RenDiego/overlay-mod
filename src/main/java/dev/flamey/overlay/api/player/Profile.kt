package dev.flamey.overlay.api.player

data class Profile(
    var username: String,
    val rank: Rank, val nicked: Boolean,
    val fkdr: Double = 0.0
)