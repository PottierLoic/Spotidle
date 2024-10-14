package com.example.spotidle.ui.guess

import GameViewModel
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.imageLoader
import coil.request.ImageRequest
import coil.size.Size
import com.example.spotidle.GameState
import com.example.spotidle.spotifyApiManager.AlbumManager
import com.example.spotidle.spotifyApiManager.ArtistManager
import com.example.spotidle.spotifyApiManager.TrackManager
import com.example.spotidle.ui.guess.components.GuessSection
import com.example.spotidle.ui.guess.components.SpotifightScaffold
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.round

@Composable
fun ArtistGuessScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    idTrack: String,
    gameViewModel: GameViewModel,
    suggestions: List<String>
) {
    val albumManager = AlbumManager()
    val trackManager = TrackManager()
    val artistManager = ArtistManager()
    val context = LocalContext.current

    var artistName by remember { mutableStateOf("") }
    var oldestAlbumCoverUrl by remember { mutableStateOf("") }
    var popularSong by remember { mutableStateOf("") }
    var musicalGenre = remember { mutableStateListOf<String>() }
    var profilePicture by remember { mutableStateOf("") }

    var displayedHint by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val pair: Pair<String, String> = trackManager.getArtistIdNameFromTrack(idTrack)
                val artistId = pair.first
                artistName = pair.second
                oldestAlbumCoverUrl =
                    albumManager.getAlbumCover(artistManager.getOldestAlbumId(artistId))
                val popularSongPair: Pair<String, String> =
                    artistManager.getAFamousTrackIdName(artistId)
                popularSong = popularSongPair.second
                musicalGenre.clear()
                musicalGenre.addAll(artistManager.getGenres(artistId))
                profilePicture = artistManager.getProfilePicture(artistId)
                profilePicture = artistManager.getProfilePicture(artistId)
            } catch (e: Exception) {
                Log.e("Spotify", "Failed to get artist details: ${e.message}")
            }
        }
    }

    Box{
        if (gameViewModel.gameState == GameState.WIN) {
            val painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("https://i.gifer.com/6SSp.gif")
                    .size(Size.ORIGINAL)
                    .crossfade(true)
                    .build(),
                imageLoader = LocalContext.current.imageLoader.newBuilder()
                    .components {
                        add(GifDecoder.Factory())
                    }
                    .build()
            )
            Image(
                painter = painter,
                contentDescription = "Victory GIF",
                modifier = Modifier
                    .size(800.dp)
                    .align(Alignment.Center)
                    .zIndex(45f),
                contentScale = ContentScale.Crop
            )
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
            when (displayedHint) {
                0 -> {
                    Image(
                        painter = rememberAsyncImagePainter(oldestAlbumCoverUrl),
                        contentDescription = "Album Cover",
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent)
                    )
                }
                1 -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = popularSong,
                            color = Color.White,
                            fontSize = 20.sp,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
                2 -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent)
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
                                    fontSize = 20.sp,
                                    modifier = Modifier
                                        .padding(vertical = 4.dp)
                                        .align(Alignment.CenterHorizontally)
                                )
                            }
                        }
                    }
                }
                in 3..4 -> {
                    Image(
                        painter = rememberAsyncImagePainter(profilePicture),
                        contentDescription = "Artist Profile Picture",
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent)
                    )
                }
            }
        }
        GuessSection(
            correctGuessName = artistName,
            toGuess = "artist",
            onGuessSubmit = { guess ->
                if (guess.equals(artistName, ignoreCase = true)) {
                    gameViewModel.gameState = GameState.WIN
                } else {
                    gameViewModel.attempts += 1
                    displayedHint += 1
                    if (gameViewModel.attempts >= 4) {
                        gameViewModel.gameState = GameState.LOOSE
                    }
                }
            },
            gameViewModel = gameViewModel,
            onHintClick = { hintIndex ->
                if (hintIndex <= gameViewModel.attempts) {
                    displayedHint = hintIndex
                }
            },
            suggestions = suggestions
        )
    }

}