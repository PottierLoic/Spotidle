package com.example.spotidle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.spotidle.ui.connectPage.ConnectPage
import com.example.spotidle.ui.guess.AlbumGuessScreen
import com.example.spotidle.ui.guess.ArtistGuessScreen
import com.example.spotidle.ui.guess.LyricsGuessScreen
import com.example.spotidle.ui.guess.MusicGuessScreen
import com.example.spotidle.ui.home.HomeScreen
import com.example.spotidle.ui.theme.SpotidleTheme
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationResponse
import com.spotify.sdk.android.auth.AuthorizationRequest
import android.util.Log
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import kotlinx.coroutines.*
import org.json.JSONException
import java.io.IOException

private const val CLIENT_ID = "71cb703af64d40e889f5a274b3986da7"
private const val REDIRECT_URI = "spotidle://callback"
private const val REQUEST_CODE = 1337
private var TOKEN = ""

data class TrackInfo(
    val name: String,
    val artist: String,
    val album: String,
    val albumCoverUrl: String,
    val previewUrl: String?
)

data class ArtistData(
    val profilePicture: String?,
    val mostPopularSong: String?,
)

interface TracksCallback {
    fun onTracksReceived(tracks: MutableList<TrackInfo>)
}

interface ArtistDataCallback {
    fun onArtistDataReceived(artistData: ArtistData?)
}

class MainActivity : ComponentActivity() {
    private var spotifyAppRemote: SpotifyAppRemote? = null
    private var isSpotifyConnected = mutableStateOf(false)
    private var username = mutableStateOf("")

    private var musicTrack by mutableStateOf<TrackInfo?>(null)
    private var lyricsTrack by mutableStateOf<TrackInfo?>(null)
    private var albumTrack by mutableStateOf<TrackInfo?>(null)
    private var artistTrack by mutableStateOf<TrackInfo?>(null)

    private var artistData by mutableStateOf<ArtistData?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpotidleTheme {
                MainScreen(
                    spotifyLogin = { connectSpotify() },
                    disconnectSpotify = { disconnectSpotify() },
                    isSpotifyConnected = isSpotifyConnected.value,
                    username = username.value,
                    musicTrack = musicTrack,
                    lyricsTrack = lyricsTrack,
                    albumTrack = albumTrack,
                    artistTrack = artistTrack,
                    artistData = artistData
                )
            }
        }
    }

    private fun connectSpotify() {
        super.onStart()
        val builder =
            AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI)
        builder.setScopes(arrayOf("user-library-read", "user-read-playback-state", "user-modify-playback-state", "user-read-private", "user-read-email"))
        val request = builder.build()
        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request)
        val connectionParams = ConnectionParams.Builder(CLIENT_ID)
            .setRedirectUri(REDIRECT_URI)
            .showAuthView(true)
            .build()
        SpotifyAppRemote.connect(this, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(spotifyAppRemote: SpotifyAppRemote) {
                this@MainActivity.spotifyAppRemote = spotifyAppRemote
                Log.d("Spotify", "Connected to Spotify App Remote")
            }
            override fun onFailure(throwable: Throwable) {
                Log.e("Spotify", "Failed to connect to Spotify App Remote: ${throwable.message}")
            }
        })
    }

    private fun disconnectSpotify() {
        spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
            Log.d("Spotify", "Disconnected from Spotify App Remote")
            isSpotifyConnected.value = false
        }
        isSpotifyConnected.value = false
    }

    override fun onStop() {
        super.onStop()
        spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
            Log.d("Spotify", "Disconnected from Spotify App Remote")
        }
    }

    private fun selectRandomTracks(tracks: MutableList<TrackInfo>, numberOfTracks: Int = 4): List<TrackInfo> {
        return tracks.shuffled().take(numberOfTracks)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == REQUEST_CODE) {
            val response = AuthorizationClient.getResponse(resultCode, intent)
            when (response.type) {
                AuthorizationResponse.Type.TOKEN -> {
                    TOKEN = response.accessToken
                    Log.d("Spotify", "Authorization successful, token: $TOKEN")
//                    fetchUserProfile(TOKEN)
                    isSpotifyConnected.value = true
                    fetchLikedTracks(TOKEN, object : TracksCallback {
                        override fun onTracksReceived(tracks: MutableList<TrackInfo>) {
                            if (tracks.size >= 4) {
                                val selectedTracks = selectRandomTracks(tracks)
                                musicTrack = selectedTracks[0]
                                lyricsTrack = selectedTracks[1]
                                albumTrack = selectedTracks[2]
                                artistTrack = selectedTracks[3]

                                artistTrack?.let { track ->
                                    fetchArtistProfileAndTopTracks(track.artist, TOKEN, object : ArtistDataCallback {
                                        override fun onArtistDataReceived(artistData: ArtistData?) {
                                            if (artistData != null) {
                                                this@MainActivity.artistData = artistData
                                                Log.d("Spotify", "Artist data fetched successfully: $artistData")
                                            } else {
                                                Log.e("Spotify", "Failed to fetch artist data.")
                                            }
                                        }
                                    })
                                }
                            } else {
                                Log.e("Spotify", "Not enough tracks to start all games.")
                            }
                        }
                    })
                    fetchUserProfile(response.accessToken)
                }
                AuthorizationResponse.Type.ERROR -> {
                    Log.e("Spotify", "Authorization error: ${response.error}")
                }
                else -> {
                    Log.d("Spotify", "Authorization flow was cancelled or not completed.")
                }
            }
        }
    }

    private fun fetchUserProfile(token: String) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.spotify.com/v1/me")
            .addHeader("Authorization", "Bearer $token")
            .build()
        CoroutineScope(Dispatchers.IO).launch {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            try {
                val jsonResponse = JSONObject(responseBody)
                val displayName = jsonResponse.getString("display_name")
                withContext(Dispatchers.Main) {
                    username.value = displayName
                }
            } catch (e: JSONException) {
                Log.e("Spotify", "Failed to parse user profile: ${e.message}")
                Log.e("Spotify", "Response was: $responseBody")
            }
        }
    }
}

private fun fetchLikedTracks(accessToken: String, callback: TracksCallback) {
    val url = "https://api.spotify.com/v1/me/tracks"
    val client = OkHttpClient()
    val request = Request.Builder()
        .url(url)
        .addHeader("Authorization", "Bearer $accessToken")
        .build()
    client.newCall(request).enqueue(object : okhttp3.Callback {
        override fun onFailure(call: okhttp3.Call, e: IOException) {
            Log.e("Spotify", "Failed to fetch liked tracks: ${e.message}")
            callback.onTracksReceived(mutableListOf())
        }
        override fun onResponse(call: okhttp3.Call, response: Response) {
            val likedTracks = mutableListOf<TrackInfo>()
            if (!response.isSuccessful) {
                Log.e("Spotify", "Error fetching liked tracks: ${response.message}")
                callback.onTracksReceived(likedTracks)
                return
            }
            val jsonData = response.body?.string()
            if (jsonData != null) {
                val jsonObject = JSONObject(jsonData)
                val itemsArray = jsonObject.getJSONArray("items")
                for (i in 0 until itemsArray.length()) {
                    val trackObject = itemsArray.getJSONObject(i).getJSONObject("track")
                    val trackName = trackObject.getString("name")
                    val artistName = trackObject.getJSONArray("artists").getJSONObject(0).getString("name")
                    val albumName = trackObject.getJSONObject("album").getString("name")
                    val albumCoverUrl = trackObject.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url")
                    val previewUrl = if (trackObject.has("preview_url")) trackObject.getString("preview_url") else null
                    likedTracks.add(TrackInfo(trackName, artistName, albumName, albumCoverUrl, previewUrl))
                }
            }
            callback.onTracksReceived(likedTracks)
        }
    })
}

private fun fetchArtistProfileAndTopTracks(artistName: String, accessToken: String, callback: ArtistDataCallback) {
    val searchUrl = "https://api.spotify.com/v1/search?q=$artistName&type=artist"
    val client = OkHttpClient()
    val request = Request.Builder()
        .url(searchUrl)
        .addHeader("Authorization", "Bearer $accessToken")
        .build()
    client.newCall(request).enqueue(object : okhttp3.Callback {
        override fun onFailure(call: okhttp3.Call, e: IOException) {
            Log.e("Spotify", "Failed to fetch artist profile and tracks: ${e.message}")
            callback.onArtistDataReceived(null)
        }
        override fun onResponse(call: okhttp3.Call, response: Response) {
            val jsonData = response.body?.string()
            if (jsonData != null) {
                val jsonObject = JSONObject(jsonData)
                val artistsArray = jsonObject.getJSONObject("artists").getJSONArray("items")
                if (artistsArray.length() > 0) {
                    val artistObject = artistsArray.getJSONObject(0)
                    val artistId = artistObject.getString("id")
                    val profilePictureUrl = artistObject.getJSONArray("images").getJSONObject(0).getString("url")

                    fetchArtistTopTracks(artistId, accessToken, profilePictureUrl, callback)
                }
            }
        }
    })
}

private fun fetchArtistTopTracks(artistId: String, accessToken: String, profilePictureUrl: String, callback: ArtistDataCallback) {
    val topTracksUrl = "https://api.spotify.com/v1/artists/$artistId/top-tracks?market=US"
    val client = OkHttpClient()
    val request = Request.Builder()
        .url(topTracksUrl)
        .addHeader("Authorization", "Bearer $accessToken")
        .build()
    client.newCall(request).enqueue(object : okhttp3.Callback {
        override fun onFailure(call: okhttp3.Call, e: IOException) {
            Log.e("Spotify", "Failed to fetch top tracks: ${e.message}")
            callback.onArtistDataReceived(null)
        }
        override fun onResponse(call: okhttp3.Call, response: Response) {
            val jsonData = response.body?.string()
            if (jsonData != null) {
                val jsonObject = JSONObject(jsonData)
                val tracksArray = jsonObject.getJSONArray("tracks")
                val mostPopularSong = if (tracksArray.length() > 0) tracksArray.getJSONObject(0).getString("name") else null
                val artistData = ArtistData(
                    profilePicture = profilePictureUrl,
                    mostPopularSong = mostPopularSong,
                )
                callback.onArtistDataReceived(artistData)
            }
        }
    })
}


// kind of template for fetching only one item

//private fun fetchUserProfile(accessToken: String) {
//    val url = "https://api.spotify.com/v1/me"
//    val client = OkHttpClient()
//    val request = Request.Builder()
//        .url(url)
//        .addHeader("Authorization", "Bearer $accessToken")
//        .build()
//    client.newCall(request).enqueue(object : okhttp3.Callback {
//        override fun onFailure(call: okhttp3.Call, e: IOException) {
//            Log.e("Spotify", "Failed to fetch user profile: ${e.message}")
//        }
//        override fun onResponse(call: okhttp3.Call, response: Response) {
//            if (!response.isSuccessful) {
//                Log.e("Spotify", "Error fetching user profile: ${response.message}")
//                return
//            }
//            val jsonData = response.body?.string()
//            if (jsonData != null) {
//                val jsonObject = JSONObject(jsonData)
//                val displayName = jsonObject.getString("display_name")
//                val email = if (jsonObject.has("email")) {
//                    jsonObject.getString("email")
//                } else {
//                    "Email not available"
//                }
//                Log.d("Spotify", "User Profile: Name = $displayName, Email = $email")
//            }
//        }
//    })
//}


@Composable
fun MainScreen(
    spotifyLogin: () -> Unit,
    disconnectSpotify: () -> Unit,
    isSpotifyConnected: Boolean,
    username: String,
    musicTrack: TrackInfo?,
    lyricsTrack: TrackInfo?,
    albumTrack: TrackInfo?,
    artistTrack: TrackInfo?,
    artistData: ArtistData?
) {
    val navController = rememberNavController()
    if(!isSpotifyConnected) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1ED760))
        )
        Image(
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.2f),
            painter = painterResource(R.drawable.thumbs_up),
            contentDescription = "background_image",
            contentScale = ContentScale.FillBounds,
        )
        ConnectPage(spotifyLogin = { spotifyLogin() } )
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF191414))
        )
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(R.drawable.thumbs_up),
            contentDescription = "background_image",
            contentScale = ContentScale.FillBounds
        )
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.fillMaxSize()
        ) {
            composable("home") {
                HomeScreen(
                    username = username,
                    navController = navController,
                    disconnectSpotify = disconnectSpotify
                )
            }
            composable("lyricsGuess") {
                LyricsGuessScreen(navController = navController) // TODO
            }
            composable("musicGuess") {
                musicTrack?.let { track ->
                    MusicGuessScreen(navController = navController, track = track)
                } ?: run {
                    Log.e("Spotify", "Music track is null, cannot navigate to MusicGuessScreen")
                }
            }
            composable("albumGuess") {
                albumTrack?.let { track ->
                    AlbumGuessScreen(navController = navController, track = track)
                } ?: run {
                    Log.e("Spotify", "Album track is null, cannot navigate to AlbumGuessScreen")
                }
            }
            composable("artistGuess") {
                artistTrack?.let { track ->
                    ArtistGuessScreen(
                        navController = navController,
                        track = track,
                        artist = artistData ?: ArtistData(
                            profilePicture = null,
                            mostPopularSong = "Unknown",
                        )
                    )
                } ?: run {
                    Log.e("Spotify", "Artist track is null, cannot navigate to ArtistGuessScreen")
                }
            }
        }
    }
}
