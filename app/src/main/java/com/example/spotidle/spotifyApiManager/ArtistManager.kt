package com.example.spotidle.spotifyApiManager

import android.util.Log
import com.example.spotidle.MainActivity

class ArtistManager {
  private fun intYear(releaseDate: String): Int {
    return releaseDate.split("-")[0].toInt()
  }

    suspend fun getOldestAlbumId(artistId: String): String {
        return try {
            val response = RetrofitInstance.api.getAlbums("Bearer ${MainActivity.TOKEN}", artistId)
            response.items.minByOrNull { intYear(it.release_date) }?.id ?: ""
        } catch (e: Exception) {
            Log.e("Spotify", "Failed to fetch albums: ${e.message}")
            ""
        }
    }

    suspend fun getAFamousTrackIdName(artistId: String): Pair<String, String> {
        return try {
            val response = RetrofitInstance.api.getTopTracks("Bearer ${MainActivity.TOKEN}", artistId)
            val randFamousTrack = response.tracks[(0..(response.tracks.size)).random()]
            Pair(randFamousTrack.id, randFamousTrack.name)
        } catch (e: Exception) {
            Log.e("Spotify", "Failed to fetch top tracks: ${e.message}")
            Pair("", "")
        }
    }

    suspend fun getGenres(artistId: String): List<String> {
        return try {
            val response = RetrofitInstance.api.getArtist("Bearer ${MainActivity.TOKEN}", artistId)
            response.genres
        } catch (e: Exception) {
            Log.e("Spotify", "Failed to fetch artist: ${e.message}")
            emptyList()
        }
    }

    suspend fun getProfilePicture(artistId: String): String {
        return try {
            val response = RetrofitInstance.api.getArtist("Bearer ${MainActivity.TOKEN}", artistId)
            response.images[0].url
        } catch (e: Exception) {
            Log.e("Spotify", "Failed to fetch artist: ${e.message}")
            ""
        }
    }
}