package com.example.spotidle.spotifyApiManager

import android.util.Log
import com.example.spotidle.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import kotlin.Int.Companion.MAX_VALUE

class ArtistManager {
  fun int_year(release_date: String): Int {
    return release_date.split("-")[0].toInt()
  }

  suspend fun getOldestAlbumId(artistId: String): String {
      val client = OkHttpClient()
      val request = Request.Builder()
          .url("https://api.spotify.com/v1/artists/$artistId/albums")
          .addHeader("Authorization", "Bearer ${MainActivity.TOKEN}")
          .build()
      return withContext(Dispatchers.IO) {
          val response = client.newCall(request).execute()
          val responseBody = response.body?.string() ?: ""
          try {
            val jsonResponse = JSONObject(responseBody)
            val albums = jsonResponse.getJSONArray("items")
            val min_date = MAX_VALUE
            var oldestId = ""
            for (i in 0 until albums.length()) {
              if (int_year(albums.getJSONObject(i).getString("release_date")) < min_date) {
                oldestId = albums.getJSONObject(i).getString("id")
              }
            }
            oldestId
          } catch (e: JSONException) {
                Log.e("Spotify", "Failed to parse oldest album id: ${e.message}")
                Log.e("Spotify", "Response was: $responseBody")
                ""
            }
      }
  }



  suspend fun getAFamousTrackIdName(artistId: String): Pair<String, String> {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://api.spotify.com/v1/artists/$artistId/top-tracks")
        .addHeader("Authorization", "Bearer ${MainActivity.TOKEN}")
        .build()
    return withContext(Dispatchers.IO) {
        val response = client.newCall(request).execute()
        val responseBody = response.body?.string() ?: ""
        try {
            val jsonResponse = JSONObject(responseBody)
            val tracks = jsonResponse.getJSONArray("tracks")
            val oneOfFamous = tracks.getJSONObject((0..(tracks.length())).random())
            val mostFamousId = oneOfFamous.getString("id")
            val mostFamousName = oneOfFamous.getString("name")
            Pair(mostFamousId, mostFamousName)
        } catch (e: JSONException) {
            Log.e("Spotify", "Failed to parse famous track id and name: ${e.message}")
            Log.e("Spotify", "Response was: $responseBody")
            Pair("","")
            }
        }
    }

  suspend fun getGenres(artistId: String): List<String> {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://api.spotify.com/v1/artists/$artistId")
        .addHeader("Authorization", "Bearer ${MainActivity.TOKEN}")
        .build()
    return withContext(Dispatchers.IO) {
        val response = client.newCall(request).execute()
        val genres = mutableListOf<String>()
        if (!response.isSuccessful) {
          Log.e("Spotify", "Error fetching artists genres: ${response.message}")
          return@withContext genres
      }
      val jsonData = response.body?.string() ?: ""
      try {
          val jsonObject = JSONObject(jsonData)
          val itemsArray = jsonObject.getJSONArray("genres")
          for (i in 0 until itemsArray.length()) {
              val genre = itemsArray.getString(i)
              genres.add(genre)
          }
      } catch (e: JSONException) {
          Log.e("Spotify", "Failed to parse artists genres: ${e.message}")
          Log.e("Spotify", "Response was: $jsonData")
      }
      genres
    }
  }

  suspend fun getProfilePicture(artistId: String): String {
      val client = OkHttpClient()
      val request = Request.Builder()
          .url("https://api.spotify.com/v1/artists/$artistId")
          .addHeader("Authorization", "Bearer ${MainActivity.TOKEN}")
          .build()
      return withContext(Dispatchers.IO) {
          val response = client.newCall(request).execute()
          val responseBody = response.body?.string() ?: ""
          try {
              val jsonResponse = JSONObject(responseBody)
              val profilePicture = jsonResponse.getJSONArray("images")
                  .getJSONObject(0)
                  .getString("url")
              profilePicture
          } catch (e: JSONException) {
              Log.e("Spotify", "Failed to parse artist pp: ${e.message}")
              Log.e("Spotify", "Response was: $responseBody")
              ""
          }
      }
  }
}