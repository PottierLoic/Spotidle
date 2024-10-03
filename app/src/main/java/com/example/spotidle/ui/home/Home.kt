package com.example.spotidle.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.spotidle.MainActivity
import com.example.spotidle.R
import com.example.spotidle.ui.home.components.SpotifightTitle

@Composable
fun HomeScreen(modifier: Modifier = Modifier, spotifyLogin: () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SpotifightTitle()
        Button(onClick = { spotifyLogin() }) {
            Image(
                painter = painterResource(id = R.drawable.spotify_logo),
                contentDescription = "Spotify Logo",
                modifier = Modifier
                    .size(32.dp)
                    .padding(end = 8.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.size(12.dp))
            Text("Log in with spotify")
        }
    }
}