package com.example.spotidle.spotifyApiManager

import android.app.Activity
import android.util.Log
import com.example.spotidle.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject

class UserManager() {
    suspend fun getUserName(): String {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.spotify.com/v1/me")
            .addHeader("Authorization", "Bearer ${MainActivity.TOKEN}")
            .build()
        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            try {
                val jsonResponse = JSONObject(responseBody)
                val displayName = jsonResponse.getString("display_name")
                displayName
            } catch (e: JSONException) {
                Log.e("Spotify", "Failed to parse user profile: ${e.message}")
                Log.e("Spotify", "Response was: $responseBody")
                ""
            }
        }
    }

    suspend fun fetchLikedTracks(): List<String> {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.spotify.com/v1/me/tracks")
            .addHeader("Authorization", "Bearer ${MainActivity.TOKEN}")
            .build()
        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()
            val likedTracks = mutableListOf<String>()
            if (!response.isSuccessful) {
                Log.e("Spotify", "Error fetching liked tracks: ${response.message}")
                return@withContext likedTracks
            }
            val jsonData = response.body?.string() ?: ""
            try {
                val jsonObject = JSONObject(jsonData)
                val itemsArray = jsonObject.getJSONArray("items")
                for (i in 0 until itemsArray.length()) {
                    val trackObject = itemsArray.getJSONObject(i).getJSONObject("track")
                    val trackId = trackObject.getString("id")
                    likedTracks.add(trackId)
                }
            } catch (e: JSONException) {
                Log.e("Spotify", "Failed to parse liked tracks: ${e.message}")
                Log.e("Spotify", "Response was: $jsonData")
            }
            likedTracks
        }
    }

}