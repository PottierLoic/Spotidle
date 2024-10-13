package com.example.spotidle.spotifyApiManager

import android.util.Log
import com.example.spotidle.MainActivity

class AlbumManager {
    suspend fun getAlbumCover(albumId: String): String {
        return try {
            val response = RetrofitInstance.api.getAlbum("Bearer ${MainActivity.TOKEN}", albumId)
            response.images[0].url
        } catch (e: Exception) {
            Log.e("Spotify", "Failed to fetch albums: ${e.message}")
            ""
        }
    }
}