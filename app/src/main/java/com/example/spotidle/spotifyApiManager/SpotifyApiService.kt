package com.example.spotidle.spotifyApiManager

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface SpotifyApiService {
    @GET("v1/artists/{artistId}/albums")
    suspend fun getAlbums(
        @Header("Authorization") authHeader: String,
        @Path("artistId") artistId: String
    ): AlbumsResponse

    @GET("v1/artists/{artistId}/top-tracks")
    suspend fun getTopTracks(
        @Header("Authorization") authHeader: String,
        @Path("artistId") artistId: String,
        @Query("market") market: String = "US"
    ): TracksResponse

    @GET("v1/artists/{artistId}")
    suspend fun getArtist(
        @Header("Authorization") authHeader: String,
        @Path("artistId") artistId: String
    ): Artist

    @GET("v1/albums/{albumId}")
    suspend fun getAlbum(
        @Header("Authorization") authHeader: String,
        @Path("albumId") albumId: String
    ): Album

    @GET("v1/tracks/{trackId}")
    suspend fun getTrack(
        @Header("Authorization") authHeader: String,
        @Path("trackId") trackId: String
    ): Track

    @GET("v1/me")
    suspend fun getUser(
        @Header("Authorization") authHeader: String,
    ): User

    @GET("v1/me/tracks")
    suspend fun getUserLikedTracks(
        @Header("Authorization") authHeader: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): TopTracks
}
