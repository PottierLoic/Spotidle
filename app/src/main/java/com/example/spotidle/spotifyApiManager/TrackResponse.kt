package com.example.spotidle.spotifyApiManager

data class TracksResponse(
    val tracks: List<Track>
)

data class Track(
    val id: String,
    val name: String,
    val preview_url: String,
    val album: Album,
    val artists: List<Artist>
)

data class TopTracks(
    var next: String?,
    val items: List<Meta>
)

data class Meta(
    val track: Track
)