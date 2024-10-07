package com.example.spotidle.spotifyApiManager

data class SongResponse(
    val response: Response // Cette classe doit contenir un champ "song"
)

data class Response(
    val song: Song // Cette classe doit contenir les détails de la chanson
)

data class Song(
    val id: Int,
    val title: String,
    val lyrics: String // Cette classe doit contenir le texte des paroles
)