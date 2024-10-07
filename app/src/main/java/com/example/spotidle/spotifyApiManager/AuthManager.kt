package com.example.spotidle.spotifyApiManager

import androidx.compose.runtime.MutableState
import android.app.Activity
import android.content.Intent
import android.util.Log
import com.example.spotidle.MainActivity
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse

class AuthManager(private val activity: Activity) {
    fun connectSpotify(onSuccess: (SpotifyAppRemote) -> Unit, onFailure: (Throwable) -> Unit) {
        val builder = AuthorizationRequest.Builder(MainActivity.CLIENT_ID, AuthorizationResponse.Type.TOKEN, MainActivity.REDIRECT_URI)
        builder.setScopes(arrayOf("user-library-read", "user-read-playback-state", "user-modify-playback-state", "user-read-private", "user-read-email"))
        val request = builder.build()

        AuthorizationClient.openLoginActivity(activity, MainActivity.REQUEST_CODE, request)

        val connectionParams = ConnectionParams.Builder(MainActivity.CLIENT_ID)
            .setRedirectUri(MainActivity.REDIRECT_URI)
            .showAuthView(true)
            .build()

        SpotifyAppRemote.connect(activity, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(spotifyAppRemote: SpotifyAppRemote) {
                Log.d("Spotify", "Connected to Spotify App Remote")
                onSuccess(spotifyAppRemote)
            }

            override fun onFailure(throwable: Throwable) {
                Log.e("Spotify", "Failed to connect to Spotify App Remote: ${throwable.message}")
                onFailure(throwable)
            }
        })
    }

    fun disconnectSpotify(spotifyAppRemote: SpotifyAppRemote?, isSpotifyConnected: MutableState<Boolean>) {
        spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
            Log.d("Spotify", "Disconnected from Spotify App Remote")
            isSpotifyConnected.value = false
        }
        isSpotifyConnected.value = false
    }

    fun handleAuthorizationResult(requestCode: Int, resultCode: Int, intent: Intent?, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        if (requestCode == MainActivity.REQUEST_CODE) {
            val response = AuthorizationClient.getResponse(resultCode, intent)
            when (response.type) {
                AuthorizationResponse.Type.TOKEN -> {
                    Log.d("Spotify", "Authorization successful, token: ${response.accessToken}")
                    onSuccess(response.accessToken)
                }
                AuthorizationResponse.Type.ERROR -> {
                    Log.e("Spotify", "Authorization error: ${response.error}")
                    onError(response.error)
                }
                else -> {
                    Log.d("Spotify", "Authorization flow was cancelled or not completed.")
                }
            }
        }
    }

}
