package com.example.spotidle.ui.connectPage

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.spotidle.R
import com.example.spotidle.ui.home.components.SpotifightTitle
import com.example.spotidle.ui.home.components.SpotifightTitleWhite


@Composable
fun ConnectPage(
    modifier: Modifier = Modifier,
    spotifyLogin: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.size(200.dp))
        SpotifightTitleWhite()
        Spacer(modifier = Modifier.size(140.dp))
        Button(
            onClick = { spotifyLogin() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1DB954),
                contentColor = Color.White
            ),
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.spotify_logo_white),
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