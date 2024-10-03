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
import androidx.compose.ui.tooling.preview.Preview
import com.example.spotidle.ui.guess.AlbumGuessScreen
import com.example.spotidle.ui.guess.ArtistGuessScreen
import com.example.spotidle.ui.guess.LyricsGuessScreen
import com.example.spotidle.ui.guess.MusicGuessScreen
import com.example.spotidle.ui.home.HomeScreen
import com.example.spotidle.ui.home.components.BottomNavigationBar
import com.example.spotidle.ui.theme.SpotidleTheme
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationResponse
import com.spotify.sdk.android.auth.AuthorizationRequest
import android.util.Log
import android.content.Intent

private const val CLIENT_ID = "fe1e042e58414bbfbac7e10a48dde4db"
private const val REDIRECT_URI = "spotidle://callback"
private const val REQUEST_CODE = 1337

class MainActivity : ComponentActivity() {
    private var spotifyAppRemote: SpotifyAppRemote? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpotidleTheme {
                MainScreen(spotifyLogin = { connectSpotify() })
            }
        }
    }

    fun connectSpotify() {
        super.onStart()
        val builder =
            AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI)
        builder.setScopes(arrayOf("user-read-playback-state", "user-modify-playback-state"))
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == REQUEST_CODE) {
            val response = AuthorizationClient.getResponse(resultCode, intent)
            when (response.type) {
                AuthorizationResponse.Type.TOKEN -> {
                    // Handle successful response
                    Log.d("Spotify", "Authorization successful, token: ${response.accessToken}")
                }

                AuthorizationResponse.Type.ERROR -> {
                    // Handle error response
                    Log.e("Spotify", "Authorization error: ${response.error}")
                }

                else -> {
                    // Handle other cases
                    Log.d("Spotify", "Authorization flow was cancelled or not completed.")
                }
            }
        }
    }
}

@Composable
fun MainScreen(spotifyLogin: () -> Unit) {
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
            0 -> HomeScreen(modifier = screenMod, spotifyLogin = { spotifyLogin() })
            1 -> LyricsGuessScreen(modifier = screenMod)
            2 -> MusicGuessScreen(modifier = screenMod)
            3 -> AlbumGuessScreen(modifier = screenMod)
            4 -> ArtistGuessScreen(modifier = screenMod)
        }
    }
}
