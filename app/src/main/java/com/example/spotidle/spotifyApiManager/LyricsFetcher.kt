package com.example.spotidle.spotifyApiManager

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LyricsFetcher(private val accessToken: String) {

    fun fetchLyrics(songId: Int, onResult: (String?) -> Unit) {
        val call = RetrofitClient.instance.getSongLyrics(accessToken, songId)
        call.enqueue(object : Callback<SongResponse> {
            override fun onResponse(call: Call<SongResponse>, response: Response<SongResponse>) {
                if (response.isSuccessful) {
                    val lyrics = response.body()?.response?.song?.lyrics
                    onResult(lyrics)
                } else {
                    onResult(null) // Gérer les erreurs ici
                }
            }

            override fun onFailure(call: Call<SongResponse>, t: Throwable) {
                onResult(null) // Gérer les erreurs ici
            }
        })
    }
}
