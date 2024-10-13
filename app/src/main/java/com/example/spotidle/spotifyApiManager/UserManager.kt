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
        val likedTracks = mutableListOf<String>()
        return try {
            while(true) {
                val response = RetrofitInstance.api.getUserLikedTracks(
                    "Bearer ${MainActivity.TOKEN}",
                    limit,
                    offset
                )
                likedTracks.addAll(response.items.map { it.track.id })
                if (response.next == null) { break }
                offset += limit
            }
            return likedTracks.toList()
        } catch (e: Exception) {
            Log.e("Spotify", "Failed to fetch user liked track: ${e.message}")
            emptyList<String>()
        }
    }
}