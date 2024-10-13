package com.example.spotidle.spotifyApiManager

import android.util.Log
import com.example.spotidle.MainActivity

class TrackManager {
    suspend fun getTrackSample(trackId: String): String {
        return try {
            val response = RetrofitInstance.api.getTrack("Bearer ${MainActivity.TOKEN}", trackId)
            response.preview_url
        } catch (e: Exception) {
            Log.e("Spotify", "Failed to fetch track: ${e.message}")
            ""
        }
    }

    suspend fun getTitle(trackId: String): String {
        return try {
            val response = RetrofitInstance.api.getTrack("Bearer ${MainActivity.TOKEN}", trackId)
            response.name
        } catch (e: Exception) {
            Log.e("Spotify", "Failed to fetch track: ${e.message}")
            ""
        }
    }

    suspend fun getAlbumIdNameFromTrack(trackId: String): Pair<String, String> {
        return try {
            val response = RetrofitInstance.api.getTrack("Bearer ${MainActivity.TOKEN}", trackId)
            Pair(response.album.id, response.album.name)
        } catch (e: Exception) {
            Log.e("Spotify", "Failed to fetch track: ${e.message}")
            Pair("", "")
        }
    }

    suspend fun getArtistIdNameFromTrack(trackId: String): Pair<String, String> {
        return try {
            val response = RetrofitInstance.api.getTrack("Bearer ${MainActivity.TOKEN}", trackId)
            Pair(response.artists[0].id, response.artists[0].name)
        } catch (e: Exception) {
            Log.e("Spotify", "Failed to fetch track: ${e.message}")
            Pair("", "")
        }
    }
}