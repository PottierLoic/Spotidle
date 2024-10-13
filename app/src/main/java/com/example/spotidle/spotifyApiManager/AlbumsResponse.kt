package com.example.spotidle.spotifyApiManager

data class AlbumsResponse(
    val items: List<Album>
)

data class Album(
    val id: String,
    val name: String,
    val release_date: String,
    val images: List<ImageA>,
)

data class ImageA(
    val url: String,
)