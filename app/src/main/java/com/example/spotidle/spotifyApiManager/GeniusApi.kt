package com.example.spotidle.spotifyApiManager

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface GeniusApi {
    @GET("songs")
    fun getSongLyrics(
        @Header("Authorization") accessToken: String,
        @Query("id") songId: Int
    ): Call<SongResponse>
}