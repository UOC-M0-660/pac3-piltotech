package edu.uoc.pac3.oauth

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import edu.uoc.pac3.data.SessionManager
import edu.uoc.pac3.data.TwitchApiService
import edu.uoc.pac3.data.network.Network
import edu.uoc.pac3.twitch.streams.StreamsActivity
import kotlinx.coroutines.*
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class ErrorInterceptor(val context: Context) : Interceptor {

    private val TAG = "ErrorInterceptor"
    private val job = Job()
    private val scopeMainThread = CoroutineScope(job + Dispatchers.Main)
    private val scopeIO = CoroutineScope(job + Dispatchers.IO)

    override fun intercept(chain: Interceptor.Chain): Response {

        val request: Request = chain.request()
        val response = chain.proceed(request)

        if (response.code == 401) {
            //unauthorized
            SessionManager(context).clearAccessToken()

            val httpClient = Network.createHttpClient(context)
            val service = TwitchApiService(httpClient)

            // Get Tokens from Twitch
            scopeMainThread.launch {
                scopeIO.async {
                    val refreshToken = SessionManager(context).getRefreshToken()
                    Log.d(TAG, "Refreshing tokens with refresh code ${refreshToken}}")
                    val tokenResponse = refreshToken?.let { service.refreshTokens(it) }
                    Log.d(TAG, "REFRESH TOKEN RESPONSE ${tokenResponse?.accessToken}. Refresh Token: ${tokenResponse?.refreshToken}")
                    // Save access token and refresh token using the SessionManager class
                    tokenResponse?.accessToken?.let { SessionManager(context).saveAccessToken(it) }
                    tokenResponse?.refreshToken?.let { SessionManager(context).saveRefreshToken(it) }
                }.await()
                Log.d(TAG, "REFRESH TOKEN RESPONSE2")
            }

        } else {
            Log.d(TAG, "Error ${response.code}!!!!")
        }

        return response
    }
}