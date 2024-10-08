package com.example.spotidle.spotifyApiManager

import android.util.Log
import com.example.spotidle.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject

class AlbumManager {
    suspend fun getAlbumCover(albumId: String): String {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.spotify.com/v1/albums/$albumId")
            .addHeader("Authorization", "Bearer ${MainActivity.TOKEN}")
            .build()
        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            try {
                val jsonResponse = JSONObject(responseBody)
                val albumCover = jsonResponse.getJSONArray("images")
                    .getJSONObject(0)
                    .getString("url")
                albumCover
            } catch (e: JSONException) {
                Log.e("Spotify", "Failed to parse album cover: ${e.message}")
                Log.e("Spotify", "Response was: $responseBody")
                ""
            }
        }
    }
}