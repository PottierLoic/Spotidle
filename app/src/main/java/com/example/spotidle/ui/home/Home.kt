package com.example.spotidle.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    username: String,
    navController: NavController,
    disconnectSpotify: () -> Unit
) {
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
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Speaker,
                        contentDescription = "Music Guess",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }
                Button(
                    onClick = { navController.navigate("lyricsGuess") },
                    modifier = Modifier
                        .size(140.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Mic,
                        contentDescription = "Lyrics Guess",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
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
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Album,
                        contentDescription = "Album Guess",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }
                Button(
                    onClick = { navController.navigate("artistGuess") },
                    modifier = Modifier
                        .size(140.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Man,
                        contentDescription = "Artist Guess",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.size(100.dp))
            Button(
                onClick = { disconnectSpotify() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1DB954),
                    contentColor = Color.White
                ),
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "Disconnect from spotify")
            }
        }
    }
}