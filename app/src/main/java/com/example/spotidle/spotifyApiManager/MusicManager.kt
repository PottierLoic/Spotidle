package com.example.spotidle.spotifyApiManager

import android.util.Log
import com.example.spotidle.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject

class MusicManager {
    suspend fun getSampleSong(trackId: String): String? {
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

    suspend fun getTitleName(trackId: String): String {
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
                Log.e("Spotify", "Failed to parse title name: ${e.message}")
                Log.e("Spotify", "Response was: $responseBody")
                ""
            }
        }
    }

    suspend fun getAlbumName(trackId: String): Pair<String, String> {
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
                val albumName = albumObject.getString("name")
                val albumId = albumObject.getString("id")
                Pair(albumId, albumName)
            } catch (e: JSONException) {
                Log.e("Spotify", "Failed to parse album name: ${e.message}")
                Log.e("Spotify", "Response was: $responseBody")
                Pair("", "")
            }
        }
    }


}