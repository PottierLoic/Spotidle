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
import android.content.Context
import android.content.SharedPreferences
import androidx.activity.viewModels
import com.example.spotidle.spotifyApiManager.Track
import com.example.spotidle.spotifyApiManager.TrackInfo

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    companion object {
        const val CLIENT_ID = "71cb703af64d40e889f5a274b3986da7"
        const val REDIRECT_URI = "spotidle://callback"
        const val REQUEST_CODE = 1337
        var TOKEN = ""
    }
    private var spotifyAppRemote: SpotifyAppRemote? = null
    private var authManager: AuthManager = AuthManager(this)
    private var userManager: UserManager = UserManager()

    fun saveToken(context: Context, token: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("SpotifyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("auth_token", token)
        editor.apply()
    }

    fun getToken(context: Context): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("SpotifyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("auth_token", null)
    }

    fun removeToken(context: Context) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("SpotifyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("auth_token")
        editor.apply()
    }

    private fun fetchLikedTracks() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val trackInfo: TrackInfo =  userManager.getLikedTracksIds()
                val tracks: List<String> = trackInfo.ids
                mainViewModel.tracksSuggestions = trackInfo.names
                mainViewModel.albumsSuggestions = trackInfo.albums
                mainViewModel.artistsSuggestions = trackInfo.artists
                if (tracks.size < 4) {
                    throw IllegalArgumentException("Not enough tracks available: only ${tracks.size} tracks found.")
                }
                mainViewModel.fourRandTracksId.value = tracks.shuffled().take(4)
            } catch (e: Exception) {
                Log.e("Spotify", "Failed to fetch liked tracks: ${e.message}")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        removeToken(this)
        val storedToken = getToken(this)
        if (storedToken != null) {
            TOKEN = storedToken
            mainViewModel.isSpotifyConnected.value = true
            fetchLikedTracks()
        }
        setContent {
            SpotidleTheme {
                MainScreen(
                    spotifyLogin = {
                        authManager.connectSpotify(
                            onSuccess = { remote ->
                                spotifyAppRemote = remote
                                mainViewModel.isSpotifyConnected.value = true
                            },
                            onFailure = { throwable ->
                                Log.e("MainActivity", "Failed to connect: ${throwable.message}")
                            }
                        )
                    },
                    disconnectSpotify = {
                        authManager.disconnectSpotify(spotifyAppRemote, mainViewModel.isSpotifyConnected)
                        removeToken(this)
                        mainViewModel.isSpotifyConnected.value = false
                    },
                    isSpotifyConnected = mainViewModel.isSpotifyConnected.value,
                    fourRandTracksId = mainViewModel.fourRandTracksId.value,
                    tracksSuggestions = mainViewModel.tracksSuggestions,
                    albumsSuggestions = mainViewModel.albumsSuggestions,
                    artistsSuggestions = mainViewModel.artistsSuggestions,
                    fetchLikedTracks = { fetchLikedTracks() }
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
                mainViewModel.username.value = userManager.getUserName()
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
                saveToken(this, token)
                mainViewModel.isSpotifyConnected.value = true
                setUserName()
                fetchLikedTracks()
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
    fourRandTracksId: List<String>,
    tracksSuggestions: List<String>,
    artistsSuggestions: List<String>,
    albumsSuggestions: List<String>,
    fetchLikedTracks: () -> Unit
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
                    navController = navController,
                    disconnectSpotify = disconnectSpotify,
                    resetGame = {
                        fetchLikedTracks()
                        musicGameViewModel.reset()
                        lyricsGameViewModel.reset()
                        albumGameViewModel.reset()
                        artistGameViewModel.reset()
                        navController.navigate("home")
                    },
                    musicViewModel = musicGameViewModel,
                    lyricsViewModel = lyricsGameViewModel,
                    albumViewModel = albumGameViewModel,
                    artistViewModel = artistGameViewModel,
                    tracksList = fourRandTracksId
                )
            }
            composable("lyricsGuess") {
                LyricsGuessScreen(
                    navController = navController,
                    idTrack = fourRandTracksId[0],
                    gameViewModel = lyricsGameViewModel,
                    suggestions = tracksSuggestions
                )
            }
            composable("musicGuess") {
                MusicGuessScreen(
                    navController = navController,
                    idTrack = fourRandTracksId[1],
                    gameViewModel = musicGameViewModel,
                    suggestions = tracksSuggestions
                )
            }
            composable("albumGuess") {
                AlbumGuessScreen(
                    navController = navController,
                    idTrack = fourRandTracksId[2],
                    gameViewModel = albumGameViewModel,
                    suggestions = albumsSuggestions
                )
            }
            composable("artistGuess") {
                ArtistGuessScreen(
                    navController = navController,
                    idTrack = fourRandTracksId[3],
                    gameViewModel = artistGameViewModel,
                    suggestions = artistsSuggestions
                )
            }
        }
    }
}
