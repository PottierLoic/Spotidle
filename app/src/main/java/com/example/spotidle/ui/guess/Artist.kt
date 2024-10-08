package com.example.spotidle.ui.guess

import QuizzViewModel
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.spotidle.GameState
import com.example.spotidle.spotifyApiManager.AlbumManager
import com.example.spotidle.spotifyApiManager.ArtistManager
import com.example.spotidle.spotifyApiManager.TrackManager
import com.example.spotidle.ui.guess.components.GuessSection
import com.example.spotidle.ui.guess.components.SpotifightScaffold
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ArtistGuessScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    idTrack: String,
    gameValidate: (validated: Boolean) -> Unit,
    gameState: GameState,
) {
    val quizzViewModel: QuizzViewModel = viewModel()
    val albumManager = AlbumManager()
    val trackManager = TrackManager()
    val artistManager = ArtistManager()
    val context = LocalContext.current

    var artistName by remember { mutableStateOf("") }
    var oldestAlbumCoverUrl by remember { mutableStateOf("") }
    var popularSong by remember { mutableStateOf("") }
    var popularity by remember { mutableStateOf("") }
    var musicalGenre = remember { mutableStateListOf<String>() }
    var profilePicture by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val pair: Pair<String, String> = trackManager.getArtistIdName(idTrack)
                val artistId = pair.first
                artistName = pair.second
                oldestAlbumCoverUrl = albumManager.getAlbumCover(artistManager.getOldestAlbumId(artistId))
                val popularSongPair: Pair<String, String> = artistManager.getAFamousTrackIdName(artistId)
                popularSong = popularSongPair.second
                popularity = trackManager.getPopularity(popularSongPair.first)
                musicalGenre.clear()
                musicalGenre.addAll(artistManager.getGenres(artistId))
                Log.d("LOIC", musicalGenre.joinToString(", "))
                profilePicture = artistManager.getProfilePicture(artistId)
            } catch (e: Exception) {
                Log.e("Spotify", "Failed to get artist details: ${e.message}")
            }
        }
    }

    SpotifightScaffold(navController = navController) {
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .size((context.resources.displayMetrics.widthPixels / 4).dp)
                .aspectRatio(1f)
                .background(Color(0xFF1ED760))
        ) {
            if (quizzViewModel.attempts >= 0) {
                Image(
                    painter = rememberAsyncImagePainter(oldestAlbumCoverUrl),
                    contentDescription = "Album Cover",
                    modifier = Modifier
                        .fillMaxHeight(0.5f)
                        .fillMaxWidth(0.5f)
                        .align(Alignment.TopStart)
                        .background(Color.Transparent)
                )
            }
            if (quizzViewModel.attempts >= 1) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight(0.5f)
                        .fillMaxWidth(0.5f)
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Text(
                        text = popularSong,
                        color = Color.White,
                        fontSize = 15.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            if (quizzViewModel.attempts >= 2) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight(0.5f)
                        .fillMaxWidth(0.5f)
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        musicalGenre.forEach { genre ->
                            Text(
                                text = genre,
                                color = Color.White,
                                fontSize = 15.sp,
                                modifier = Modifier
                                    .padding(vertical = 4.dp)
                                    .align(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }
            }
            if (quizzViewModel.attempts >= 3) {
                Image(
                    painter = rememberAsyncImagePainter(profilePicture),
                    contentDescription = "Artist Profile Picture",
                    modifier = Modifier
                        .fillMaxHeight(0.5f)
                        .fillMaxWidth(0.5f)
                        .align(Alignment.BottomEnd)
                        .background(Color.Transparent)
                )
            }
        }
        GuessSection(
            correctGuessName = artistName,
            toGuess = "artist",
            onGuessSubmit = { guess ->
                if (guess.equals(artistName, ignoreCase = true)) {
                    gameValidate(true)
                } else {
                    quizzViewModel.attempts += 1
                    if (quizzViewModel.attempts >= 4) {
                        gameValidate(false)
                    }
                }
            },
            gameState = gameState,
            quizzViewModel = quizzViewModel
        )
    }

}