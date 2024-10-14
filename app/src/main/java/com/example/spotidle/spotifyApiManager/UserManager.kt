package com.example.spotidle.spotifyApiManager

import android.util.Log
import com.example.spotidle.MainActivity

class UserManager() {
    suspend fun getUserName(): String {
        return try {
            val response = RetrofitInstance.api.getUser("Bearer ${MainActivity.TOKEN}")
            response.display_name
        } catch (e: Exception) {
            Log.e("Spotify", "Failed to fetch user: ${e.message}")
            ""
        }
    }

    suspend fun getLikedTracksIds(): List<String> {
        var offset = 0
        val limit = 50
        val ids = mutableListOf<String>()
        val names = mutableListOf<String>()
        val artists = mutableListOf<String>()
        val albums = mutableListOf<String>()
        return try {
            while(true) {
                val response = RetrofitInstance.api.getUserLikedTracks(
                    "Bearer ${MainActivity.TOKEN}",
                    limit,
                    offset
                )
                ids.addAll(response.items.map { it.track.id })
                names.addAll(response.items.map { it.track.name })
                artists.addAll(response.items.map { it.track.album.name })
                albums.addAll(response.items.map { it.track.artists[0].name })
                if (response.next == null) { break }
                offset += limit
            }
            return listOf(ids.toList(), names.toList(), artists.toList(), albums.toList())
        } catch (e: Exception) {
            Log.e("Spotify", "Failed to fetch user liked track: ${e.message}")
            emptyList<String>()
        }
    }
}
