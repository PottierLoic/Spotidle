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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spotidle.R
import com.example.spotidle.ui.home.components.SpotifightTitle


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    spotifyLogin: () -> Unit,
    isSpotifyConnected: Boolean,
    username: String
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SpotifightTitle()
        Spacer(modifier = Modifier.size(140.dp))
        if (!isSpotifyConnected) {
            Text(text = "Log in with spotify to start guessing !", color = Color.White)
            Button(
                onClick = { spotifyLogin() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1ED760),
                    contentColor = Color.White
                ),
                modifier = Modifier.padding(8.dp)
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
        } else {
            Text(
                text = buildAnnotatedString {
                    append("Hello, ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.White)) {
                        append(username)
                    }
                    append(" !")
                },
                color = Color.White,
                fontSize = 24.sp,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}