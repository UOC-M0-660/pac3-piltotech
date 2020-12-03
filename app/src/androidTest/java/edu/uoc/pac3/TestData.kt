package edu.uoc.pac3

import android.content.Context
import edu.uoc.pac3.data.SessionManager
import edu.uoc.pac3.data.TwitchApiService
import edu.uoc.pac3.data.network.Endpoints
import edu.uoc.pac3.data.network.Network
import edu.uoc.pac3.data.oauth.OAuthConstants
import edu.uoc.pac3.data.oauth.OAuthTokensResponse
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.delay


/**
 * Created by alex on 04/10/2020.
 */
object TestData {
    const val networkWaitingMillis = 5000L
    const val sharedPrefsWaitingMillis = 500L

    // Network
    fun provideHttpClient(context: Context): HttpClient = Network.createHttpClient(context)
    fun provideTwitchService(context: Context): TwitchApiService =
        TwitchApiService(provideHttpClient(context))

    // Tokens
    const val dummyAccessToken = "access_12345"
    const val dummyRefreshToken = "refresh_12345"
    const val refreshToken = "urlzpdmpy1vd46uiyiibl8z4shxnzulpgn3cy23idtr3rp7zys"

    // User
    const val userName = "mrkobreti"
    const val userDescription = "Stream de mrkobreti"
    const val updatedUserDescription = userDescription.plus("!")

    // Token Refresh
    suspend fun setAccessToken(context: Context) {
        val scopes=listOf("user:read:email","user:edit")
        val response =
            provideHttpClient(context).post<OAuthTokensResponse>("https://id.twitch.tv/oauth2/token") {
                parameter("client_id", OAuthConstants.oauthClientId)
                parameter("client_secret", OAuthConstants.oauthClientSecret)
                parameter("refresh_token", refreshToken)
                parameter("grant_type", "refresh_token")
                parameter("scope", scopes.joinToString(separator = " "))
            }
        // Save new access token
        SessionManager(context).saveAccessToken(response.accessToken)
        delay(sharedPrefsWaitingMillis)
    }
}