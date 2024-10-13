package com.example.spotidle.spotifyApiManager

data class Artist(
    val id: String,
    val name: String,
    val images: List<Image>,
    val genres: List<String>
)

data class Image(
    val url: String,
)

