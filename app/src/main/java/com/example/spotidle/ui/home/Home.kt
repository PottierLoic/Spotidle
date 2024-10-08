package com.example.spotidle.ui.home

import GameViewModel
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.spotidle.ui.home.components.SpotifightTitle
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Man
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.imageLoader
import coil.request.ImageRequest
import coil.size.Size
import com.example.spotidle.GameState

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    username: String,
    navController: NavController,
    disconnectSpotify: () -> Unit,
    gameViewModel: GameViewModel,
    tracksList: List<String>
) {
    fun getButtonColor(gameState: GameState): Color {
        return when (gameState) {
            GameState.WIN -> Color(0xFF00C853)
            GameState.LOOSE -> Color(0xFFbf4e4e)
            GameState.PLAYING -> Color(0xFF404a43)
        }
    }
    if(tracksList.size < 3) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    // TODO : Find a better gif
                    .data("https://media.tenor.com/t5DMW5PI8mgAAAAi/loading-green-loading.gif")
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
                contentDescription = "Loading",
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.size(16.dp))
            Text(text = "Loading tracks...", color = Color.White)
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.size(50.dp))
            SpotifightTitle()
            Spacer(modifier = Modifier.size(80.dp))
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Hello, $username !", color = Color.White)
                Spacer(modifier = Modifier.size(40.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { navController.navigate("musicGuess") },
                        modifier = Modifier
                            .size(140.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = getButtonColor(
                                gameViewModel.getGameState("musicGuess")
                            )
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Speaker,
                                contentDescription = "Music Guess",
                                tint = Color.White,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = "Sound",
                                color = Color.White,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    Button(
                        onClick = { navController.navigate("lyricsGuess") },
                        modifier = Modifier
                            .size(140.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = getButtonColor(
                                gameViewModel.getGameState("lyricsGuess")
                            )
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Mic,
                                contentDescription = "Lyrics Guess",
                                tint = Color.White,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = "Lyrics",
                                color = Color.White,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.size(16.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { navController.navigate("albumGuess") },
                        modifier = Modifier
                            .size(140.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = getButtonColor(
                                gameViewModel.getGameState("albumGuess")
                            )
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Album,
                                contentDescription = "Album Guess",
                                tint = Color.White,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = "Album",
                                color = Color.White,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    Button(
                        onClick = { navController.navigate("artistGuess") },
                        modifier = Modifier
                            .size(140.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = getButtonColor(
                                gameViewModel.getGameState("artistGuess")
                            )
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Man,
                                contentDescription = "Artist Guess",
                                tint = Color.White,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = "Artist",
                                color = Color.White,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.size(100.dp))
                Spacer(modifier = Modifier.size(10.dp))
                Button(
                    onClick = { disconnectSpotify() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1DB954),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(0.60f)
                ) {
                    Text(text = "Reset Game")
                }
                Button(
                    onClick = { disconnectSpotify() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFbf4e4e),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(0.60f)
                ) {
                    Text(text = "Disconnect from spotify")
                }
            }
        }
    }
}