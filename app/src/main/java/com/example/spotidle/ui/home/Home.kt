package com.example.spotidle.ui.home

import android.net.Uri
import android.view.View
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
import com.example.spotidle.R
import com.example.spotidle.ui.home.components.SpotifightTitle
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse


//val CLIENT_ID = "71cb703af64d40e889f5a274b3986da7"
//val AUTH_TOKEN_REQUEST_CODE = 0x10
//val REDIRECT_URI = "spotifight://"
//
//fun getAuthenticationRequest(type: AuthorizationResponse.Type): AuthorizationRequest {
//    return AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
//        .setShowDialog(false)
//        .setScopes(arrayOf<String>("user-read-email"))
//        .setCampaign("your-campaign-token")
//        .build()
//}
//
//fun getRedirectUri(): Uri {
//    return Uri.Builder()
//        .scheme("spotifight")
//        .authority("login")
//        .build()
//}
//
//fun onRequestTokenClicked(view: View?) {
//    val request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN)
//    AuthorizationClient.openLoginActivity(this, AUTH_TOKEN_REQUEST_CODE, request)
//}

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SpotifightTitle()
        Button(onClick = {}) {
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