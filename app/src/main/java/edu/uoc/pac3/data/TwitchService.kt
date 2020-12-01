package edu.uoc.pac3.data

import android.util.Log
import edu.uoc.pac3.data.network.Endpoints
import edu.uoc.pac3.data.oauth.OAuthConstants
import edu.uoc.pac3.data.oauth.OAuthTokensResponse
import edu.uoc.pac3.data.oauth.UnauthorizedException
import edu.uoc.pac3.data.streams.StreamsResponse
import edu.uoc.pac3.data.user.User
import edu.uoc.pac3.data.user.UserResponse
import io.ktor.client.*
import io.ktor.client.request.*

/**
 * Created by alex on 24/10/2020.
 */

class TwitchApiService(private val httpClient: HttpClient) {
    private val TAG = "TwitchApiService"

    /// Gets Access and Refresh Tokens on Twitch
    suspend fun getTokens(authorizationCode: String): OAuthTokensResponse? {

        try {
            val response = httpClient.post<OAuthTokensResponse>(Endpoints.oauthTokenUrl) {
            parameter("client_id", OAuthConstants.oauthClientId)
            parameter("client_secret", OAuthConstants.oauthClientSecret)
            parameter("code", authorizationCode)
            parameter("grant_type", "authorization_code")
            parameter("redirect_uri", Endpoints.redirectUri)
        }
        Log.d(TAG, "Access Token: ${response.accessToken}. Refresh Token: ${response.refreshToken}")

        return OAuthTokensResponse(response.accessToken, response.refreshToken)
        } catch (t: Throwable) {
            Log.d(TAG, t.toString())
        }
        return null

    }

    /// Gets Streams on Twitch
    @Throws(UnauthorizedException::class)
    suspend fun getStreams(cursor: String? = null): StreamsResponse? {

        try {
            val response = httpClient.get<StreamsResponse>(Endpoints.twitchStreamsUrl) {
                header("Client-Id", OAuthConstants.oauthClientId)
                parameter("after", cursor)
            }
            Log.d(TAG, "Streams response: ${response.data}.")

            return response
        } catch (t: Throwable) {
            Log.d(TAG, t.toString())
        }
        return null

    }

    /// Gets Current Authorized User on Twitch
    @Throws(UnauthorizedException::class)
    suspend fun getUser(): User? {

        try {
            val response = httpClient.get<UserResponse>(Endpoints.twitchUsersUrl) {
                header("Client-Id", OAuthConstants.oauthClientId)
                parameter("login", "mrkobreti")
            }
            Log.d(TAG, "User response: ${response.data?.get(0)?.userName} ${response.data?.get(0)?.description} ${response.data?.get(0)?.imageUrl}.")

            return response.data?.get(0)
        } catch (t: Throwable) {
            Log.d(TAG, t.toString())
        }
        return null

    }

    /// Gets Current Authorized User on Twitch
    @Throws(UnauthorizedException::class)
    suspend fun updateUserDescription(description: String): User? {

        try {
            val response = httpClient.put<UserResponse>(Endpoints.twitchUsersUrl) {
                header("Client-Id", OAuthConstants.oauthClientId)
                parameter("description", description)
            }
            Log.d(TAG, "Update response: ${response.data?.get(0)?.userName} ${response.data?.get(0)?.description}.")

            return response.data?.get(0)
        } catch (t: Throwable) {
            Log.d(TAG, t.toString())
        }
        return null

    }
}