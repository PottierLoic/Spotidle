package com.example.spotidle.ui.guess

import GameViewModel
import android.util.Log
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.spotidle.spotifyApiManager.AlbumManager
import com.example.spotidle.spotifyApiManager.TrackManager
import coil.request.ImageRequest
import coil.size.Scale
import com.example.spotidle.GameState
import com.example.spotidle.ui.guess.components.GuessSection
import com.example.spotidle.ui.guess.components.SpotifightScaffold
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("AutoboxingStateCreation")
@Composable
fun AlbumGuessScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    idTrack: String,
    gameViewModel: GameViewModel,
    suggestions: List<String>
) {
    val trackManager = TrackManager()
    val albumManager = AlbumManager()
    val context = LocalContext.current
    var correctAlbumName by remember { mutableStateOf("") }
    var coverImageUrl by remember { mutableStateOf("") }
    var blurAmount by remember { mutableFloatStateOf(25f) }

    LaunchedEffect(gameViewModel.attempts, gameViewModel.gameState) {
        blurAmount = when (gameViewModel.gameState) {
            GameState.WIN, GameState.LOOSE -> 0f
            GameState.PLAYING -> {
                (25f - 5f * gameViewModel.attempts).coerceAtLeast(0f)
            }
        }
    }

    CoroutineScope(Dispatchers.Main).launch {
        try {
            val pair: Pair<String, String> = trackManager.getAlbumIdNameFromTrack(idTrack)
            correctAlbumName = pair.second
            coverImageUrl = albumManager.getAlbumCover(pair.first)
        } catch (e: Exception) {
            Log.e("Spotify", "Failed to get album details: ${e.message}")
        }
    }

    SpotifightScaffold(navController = navController) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Affichage de l'image de couverture floutée
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current).data(data = coverImageUrl)
                        .apply {
                            crossfade(true)
                            scale(Scale.FILL)
                        }.build()
                ),
                contentDescription = "Album Cover",
                modifier = Modifier
                    .blur(radius = blurAmount.dp)
                    .fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Si l'utilisateur gagne, afficher un GIF sur toute la page
            if (gameViewModel.gameState == GameState.WIN) {
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data("https://yourgifurl.com/yourgif.gif") // URL du GIF
                            .apply {
                                crossfade(true)
                                scale(Scale.FILL)
                            }.build()
                    ),
                    contentDescription = "Victory GIF",
                    modifier = Modifier
                        .fillMaxSize() // Occupe toute la page
                        .align(Alignment.Center),
                    contentScale = ContentScale.Crop
                )
            }

            // Section de devinettes
            GuessSection(
                correctGuessName = correctAlbumName,
                toGuess = "album",
                onGuessSubmit = { guess ->
                    if (guess.equals(correctAlbumName, ignoreCase = true)) {
                        blurAmount = 0f
                        gameViewModel.gameState = GameState.WIN
                    } else {
                        gameViewModel.attempts += 1
                        blurAmount = (blurAmount - 5f).coerceAtLeast(0f)
                        if (gameViewModel.attempts >= 4) {
                            blurAmount = 0f
                            gameViewModel.gameState = GameState.LOOSE
                        }
                    }
                },
                gameViewModel = gameViewModel,
                suggestions = suggestions
            )
        }
    }
}
