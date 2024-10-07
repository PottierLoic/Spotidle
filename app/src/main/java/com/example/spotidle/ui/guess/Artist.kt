package com.example.spotidle.ui.guess

import GameViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.spotidle.ui.guess.components.GuessSection
import com.example.spotidle.ui.guess.components.SpotifightScaffold

@Composable
fun ArtistGuessScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    idTrack: String,
    gameViewModel: GameViewModel = viewModel()
) {
    val context = LocalContext.current
    val fillerArtistName = "TODO REPLACE"
    val fillerAlbumCover = "https://i.scdn.co/image/ab67616d00001e02ff9ca10b55ce82ae553c8228"
    val fillerPopularSong = "La kifance"
    val fillerMusicalGenre = "Afrotrap"
    val fillerProfilePicture = "https://i.scdn.co/image/ab67616d00001e02ff9ca10b55ce82ae553c8228"


    SpotifightScaffold(navController = navController) {
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .size((context.resources.displayMetrics.widthPixels / 4).dp)
                .aspectRatio(1f)
                .background(Color(0xFF1ED760))
        ) {
            if (gameViewModel.attempts >= 0) {
                Image(
                    painter = rememberAsyncImagePainter(fillerAlbumCover),
                    contentDescription = "Album Cover",
                    modifier = Modifier
                        .fillMaxHeight(0.5f)
                        .fillMaxWidth(0.5f)
                        .align(Alignment.TopStart)
                        .background(Color.Transparent)
                )
            }
            if (gameViewModel.attempts >= 1) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight(0.5f)
                        .fillMaxWidth(0.5f)
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Text(
                        text = fillerPopularSong,
                        color = Color.White,
                        fontSize = 15.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            if (gameViewModel.attempts >= 2) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight(0.5f)
                        .fillMaxWidth(0.5f)
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                ) {
                    Text(
                        text = fillerMusicalGenre,
                        color = Color.White,
                        fontSize = 15.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            if (gameViewModel.attempts >= 3) {
                Image(
                    painter = rememberAsyncImagePainter(fillerProfilePicture),
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
            correctGuessName = fillerArtistName,
            onGuessSubmit = { guess ->
                if (guess.equals(fillerArtistName, ignoreCase = true)) {
                    gameViewModel.gameState = GameState.WIN
                } else {
                    gameViewModel.attempts += 1
                    if (gameViewModel.attempts >= 4) {
                        gameViewModel.gameState = GameState.LOOSE
                    }
                }
            },
            toGuess = "artist",
            viewModel = gameViewModel
        )
    }

}