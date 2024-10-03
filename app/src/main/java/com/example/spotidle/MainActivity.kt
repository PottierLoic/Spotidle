package com.example.spotidle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.spotidle.ui.guess.AlbumGuessScreen
import com.example.spotidle.ui.guess.ArtistGuessScreen
import com.example.spotidle.ui.guess.LyricsGuessScreen
import com.example.spotidle.ui.guess.MusicGuessScreen
import com.example.spotidle.ui.home.HomeScreen
import com.example.spotidle.ui.home.components.BottomNavigationBar
import com.example.spotidle.ui.theme.SpotidleTheme
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationResponse
import com.spotify.sdk.android.auth.AuthorizationRequest
import android.util.Log
import android.content.Intent
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import kotlinx.coroutines.*
import java.io.IOException

private const val CLIENT_ID = "fe1e042e58414bbfbac7e10a48dde4db"
private const val REDIRECT_URI = "spotidle://callback"
private const val REQUEST_CODE = 1337
private var TOKEN = ""

interface TracksCallback {
    fun onTracksReceived(tracks: MutableList<String>)
}

class MainActivity : ComponentActivity() {
    private var spotifyAppRemote: SpotifyAppRemote? = null
    private var isSpotifyConnected = mutableStateOf(false)
    private var username = mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpotidleTheme {
                MainScreen(
                    spotifyLogin = { connectSpotify() },
                    isSpotifyConnected = isSpotifyConnected.value,
                    username = username.value
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

    override fun onStop() {
        super.onStop()
        spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
            Log.d("Spotify", "Disconnected from Spotify App Remote")
        }
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
                        override fun onTracksReceived(tracks: MutableList<String>) {
                            Log.d("Spotify", "Tracks: ${tracks.joinToString(", ")}")
                        }
                    })
                    getUserProfile(response.accessToken)
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

    private fun getUserProfile(token: String) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.spotify.com/v1/me")
            .addHeader("Authorization", "Bearer $token")
            .build()
        CoroutineScope(Dispatchers.IO).launch {
            val response = client.newCall(request).execute()
            val jsonResponse = JSONObject(response.body?.string() ?: "")
            val displayName = jsonResponse.getString("display_name")
            withContext(Dispatchers.Main) {
                username.value = displayName
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
            val likedTracks = mutableListOf<String>()
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
                    likedTracks.add(trackName)
                }
            }
            callback.onTracksReceived(likedTracks)
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
    isSpotifyConnected: Boolean,
    username: String
) {
    val items = listOf("Music Guess", "Lyrics Guess", "Home", "Album Guess", "Artist Guess")
    var selectedItem by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF191414),
        bottomBar = {
            BottomNavigationBar(
                items = items,
                selectedItem = selectedItem,
                onItemSelected = { selectedItem = it }
            )
        }
    ) { innerPadding ->
        val screenMod: Modifier = Modifier.padding(innerPadding)

        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(R.drawable.thumbs_up),
            contentDescription = "background_image",
            contentScale = ContentScale.FillBounds
        )

        when (selectedItem) {
            0 -> HomeScreen(
                modifier = screenMod,
                spotifyLogin = { spotifyLogin() },
                isSpotifyConnected = isSpotifyConnected,
                username = username
            )
            1 -> LyricsGuessScreen(modifier = screenMod)
            2 -> MusicGuessScreen(modifier = screenMod)
            3 -> AlbumGuessScreen(modifier = screenMod)
            4 -> ArtistGuessScreen(modifier = screenMod)
        }
    }
}
