package com.example.spotidle.ui.guess

import GameViewModel
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.spotidle.spotifyApiManager.TrackManager
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.imageLoader
import coil.request.ImageRequest
import coil.size.Scale
import coil.size.Size
import com.example.spotidle.GameState
import com.example.spotidle.spotifyApiManager.AlbumManager
import com.example.spotidle.ui.guess.components.GuessSection
import com.example.spotidle.ui.guess.components.SpotifightScaffold
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun MusicGuessScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    idTrack: String,
    gameViewModel: GameViewModel,
    suggestions: List<String>
) {

    val trackManager = TrackManager()
    val albumManager = AlbumManager()
    var correctSongName by remember { mutableStateOf("") }
    var sampleUrl by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var albumCoverUrl by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                correctSongName = trackManager.getTitle(idTrack)
                sampleUrl = trackManager.getTrackSample(idTrack)
                albumCoverUrl = albumManager.getAlbumCover(trackManager.getAlbumIdNameFromTrack(trackId = idTrack).first)
            } catch (e: Exception) {
                Log.e("Spotify", "Failed to get title details: ${e.message}")
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
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
        Box(
            modifier = Modifier
                .size((context.resources.displayMetrics.widthPixels / 4).dp)
                .aspectRatio(1f)
                .background(Color.Transparent)
        ) {
            if (gameViewModel.gameState == GameState.WIN || gameViewModel.gameState == GameState.LOOSE) {
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current).data(data = albumCoverUrl)
                            .apply {
                                crossfade(true)
                                scale(Scale.FILL)
                            }.build()
                    ),
                    contentDescription = "Album Cover",
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center),
                    contentScale = ContentScale.Crop
                )
            }
            Button(
                onClick = {
                    isPlaying = !isPlaying;
                    if (!isPlaying) mediaPlayer?.pause() else mediaPlayer?.start();
                    if (isPlaying) {
                        if (sampleUrl != null && mediaPlayer == null) {
                            mediaPlayer = MediaPlayer().apply {
                                setDataSource(sampleUrl)
                                prepareAsync()
                                setOnPreparedListener {
                                    start()
                                }
                                setOnCompletionListener {
                                    isPlaying = false
                                }
                            }
                        } else {
                            mediaPlayer?.start()
                        }
                    } else {
                        mediaPlayer?.pause()
                    }
                },
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.Center)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play"
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        GuessSection(
            correctGuessName = correctSongName,
            toGuess = "song",
            onGuessSubmit = { guess ->
                if (guess.equals(correctSongName, ignoreCase = true)) {
                    gameViewModel.gameState = GameState.WIN
                } else {
                    gameViewModel.attempts += 1
                    if (gameViewModel.attempts >= 4) {
                        gameViewModel.gameState = GameState.LOOSE
                    }
                }
            },
            gameViewModel = gameViewModel,
            suggestions = suggestions
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }
}
