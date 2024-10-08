package com.example.spotidle.spotifyApiManager

import android.util.Log
import com.example.spotidle.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject

class TrackManager {
    suspend fun getTrackSample(trackId: String): String? {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.spotify.com/v1/tracks/$trackId")
            .addHeader("Authorization", "Bearer ${MainActivity.TOKEN}")
            .build()
        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            try {
                val jsonResponse = JSONObject(responseBody)
                jsonResponse.getString("preview_url")
            } catch (e: JSONException) {
                Log.e("Spotify", "Failed to parse track sample: ${e.message}")
                null
            }
        }
    }

    suspend fun getTitle(trackId: String): String {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.spotify.com/v1/tracks/$trackId")
            .addHeader("Authorization", "Bearer ${MainActivity.TOKEN}")
            .build()
        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            try {
                val jsonResponse = JSONObject(responseBody)
                val title = jsonResponse.getString("name")
                title
            } catch (e: JSONException) {
                Log.e("Spotify", "Failed to parse track title: ${e.message}")
                Log.e("Spotify", "Response was: $responseBody")
                ""
            }
        }
    }

    suspend fun getAlbumIdName(trackId: String): Pair<String, String> {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.spotify.com/v1/tracks/$trackId")
            .addHeader("Authorization", "Bearer ${MainActivity.TOKEN}")
            .build()
        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            try {
                val jsonResponse = JSONObject(responseBody)
                val albumObject = jsonResponse.getJSONObject("album")
                val albumId = albumObject.getString("id")
                val albumName = albumObject.getString("name")
                Pair(albumId, albumName)
            } catch (e: JSONException) {
                Log.e("Spotify", "Failed to parse album id and name: ${e.message}")
                Log.e("Spotify", "Response was: $responseBody")
                Pair("", "")
            }
        }
    }

    suspend fun getArtistIdName(trackId: String): Pair<String, String> {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.spotify.com/v1/tracks/$trackId")
            .addHeader("Authorization", "Bearer ${MainActivity.TOKEN}")
            .build()
        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            try {
                val jsonResponse = JSONObject(responseBody)
                val artistObject = jsonResponse.getJSONArray("artists")
                    .getJSONObject(0)
                val artistId = artistObject.getString("id")
                val artistName = artistObject.getString("name")
                Pair(artistId, artistName)
            } catch (e: JSONException) {
                Log.e("Spotify", "Failed to parse artist id and name: ${e.message}")
                Log.e("Spotify", "Response was: $responseBody")
                Pair("", "")
            }
        }
    }

    suspend fun getPopularity(trackId: String): String {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.spotify.com/v1/tracks/$trackId")
            .addHeader("Authorization", "Bearer ${MainActivity.TOKEN}")
            .build()
        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            try {
                val jsonResponse = JSONObject(responseBody)
                val popularityAmount = jsonResponse.getString("popularity")
                popularityAmount
            } catch (e: JSONException) {
                Log.e("Spotify", "Failed to parse popularity: ${e.message}")
                Log.e("Spotify", "Response was: $responseBody")
                ""
            }
        }
    }
}