package com.example.spotidle

import GameViewModel
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
import com.spotify.android.appremote.api.SpotifyAppRemote
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.*

import com.example.spotidle.spotifyApiManager.AuthManager
import com.example.spotidle.spotifyApiManager.UserManager

class MainActivity : ComponentActivity() {
    companion object {
        const val CLIENT_ID = "71cb703af64d40e889f5a274b3986da7"
        const val REDIRECT_URI = "spotidle://callback"
        const val REQUEST_CODE = 1337
        var TOKEN = ""
    }
    private var spotifyAppRemote: SpotifyAppRemote? = null
    private var isSpotifyConnected = mutableStateOf(false)
    private var username : MutableState<String> = mutableStateOf("")
    private var authManager: AuthManager = AuthManager(this)
    private var userManager: UserManager = UserManager()
    private var fourRandTracksId: MutableState<List<String>> = mutableStateOf(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpotidleTheme {
                MainScreen(
                    spotifyLogin = {
                        authManager.connectSpotify(
                            onSuccess = { remote ->
                                spotifyAppRemote = remote
                                isSpotifyConnected.value = true
                            },
                            onFailure = { throwable ->
                                Log.e("MainActivity", "Failed to connect: ${throwable.message}")
                            }
                        ) },
                    disconnectSpotify = { authManager.disconnectSpotify(spotifyAppRemote, isSpotifyConnected) },
                    isSpotifyConnected = isSpotifyConnected.value,
                    username = username.value,
                    fourRandTracksId = fourRandTracksId.value
                )
            }
        }
    }

    override fun onStop() {
        super.onStop()
        spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
            Log.d("Spotify", "Disconnected from Spotify App Remote")
        }
    }

    private fun setUserName () {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                username.value = userManager.getUserName()
            } catch (e: Exception) {
                Log.e("Spotify", "Failed to get user name: ${e.message}")
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        authManager.handleAuthorizationResult(
            requestCode,
            resultCode,
            intent,
            onSuccess = { token ->
                TOKEN = token
                isSpotifyConnected.value = true
                // Get username
                setUserName()
                // Get liked tracks
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val tracks: List<String> = userManager.fetchLikedTracks()
                        if (tracks.size < 4) {
                            throw IllegalArgumentException("Not enough tracks available: only ${tracks.size} tracks found.")
                        }
                        fourRandTracksId.value = tracks.shuffled().take(4)
                        Log.d("MOI", ">>> ${fourRandTracksId.value[0]}")
                        Log.d("MOI", ">>> ${fourRandTracksId.value[1]}")
                        Log.d("MOI", ">>> ${fourRandTracksId.value[2]}")
                    } catch (e: Exception) {
                        Log.e("Spotify", "Failed to fetch liked tracks: ${e.message}")
                    }
                }
            },
            onError = { error ->
                Log.e("Spotify", "Authorization error: $error")
            }
        )
    }
}

@Composable
fun MainScreen(
    spotifyLogin: () -> Unit,
    disconnectSpotify: () -> Unit,
    isSpotifyConnected: Boolean,
    username: String,
    fourRandTracksId: List<String>
) {
    val musicGameViewModel: GameViewModel = viewModel(key = "musicGameViewModel")
    val lyricsGameViewModel: GameViewModel = viewModel(key = "lyricsGameViewModel")
    val albumGameViewModel: GameViewModel = viewModel(key = "albumGameViewModel")
    val artistGameViewModel: GameViewModel = viewModel(key = "artistGameViewModel")

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
                    disconnectSpotify = disconnectSpotify,
                )
            }
            composable("lyricsGuess") {
                LyricsGuessScreen(
                    navController = navController,
                    idTrack = fourRandTracksId[0],
                    gameViewModel = lyricsGameViewModel
                )
            }
            composable("musicGuess") {
                MusicGuessScreen(
                    navController = navController,
                    idTrack = fourRandTracksId[1],
                    gameViewModel = musicGameViewModel
                )
            }
            composable("albumGuess") {
                AlbumGuessScreen(
                    navController = navController,
                    idTrack = fourRandTracksId[2],
                    gameViewModel = albumGameViewModel
                )
            }
            composable("artistGuess") {
                ArtistGuessScreen(
                    navController = navController,
                    idTrack = fourRandTracksId[3],
                    gameViewModel = artistGameViewModel
                )
            }
        }
    }
}
