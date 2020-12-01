package edu.uoc.pac3.oauth

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import edu.uoc.pac3.R
import edu.uoc.pac3.data.SessionManager
import edu.uoc.pac3.data.TwitchApiService
import edu.uoc.pac3.data.network.Endpoints
import edu.uoc.pac3.data.network.Network
import edu.uoc.pac3.data.oauth.OAuthConstants
import edu.uoc.pac3.twitch.streams.StreamsActivity
import kotlinx.android.synthetic.main.activity_oauth.*
import kotlinx.coroutines.*
import java.util.*

class OAuthActivity : AppCompatActivity() {

    private val TAG = "OAuthActivity"
    private lateinit var uniqueState:String

    private val job = Job()
    private val scopeMainThread = CoroutineScope(job + Dispatchers.Main)
    private val scopeIO = CoroutineScope(job + Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_oauth)
        launchOAuthAuthorization()

    }

    fun buildOAuthUri(): Uri {
        // Create URI
        val scopes=listOf("user:read:email","user:edit")

        uniqueState = UUID.randomUUID().toString()

        val uri = Uri.parse(Endpoints.oauthAuthorizeUrl)
                .buildUpon()
                .appendQueryParameter("client_id", OAuthConstants.oauthClientId)
                .appendQueryParameter("redirect_uri", Endpoints.redirectUri)
                .appendQueryParameter("response_type", "code")
                .appendQueryParameter("scope", scopes.joinToString(separator = " "))
                .appendQueryParameter("state", uniqueState)
                .build()

        return uri
    }

    private fun launchOAuthAuthorization() {
        //  Create URI
        val uri = buildOAuthUri()

        // Set webView Redirect Listener
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                request?.let {
                    // Check if this url is our OAuth redirect, otherwise ignore it
                    if (request.url.toString().startsWith(Endpoints.redirectUri)) {
                        // To prevent CSRF attacks, check that we got the same state value we sent, otherwise ignore it
                        val responseState = request.url.getQueryParameter("state")
                        if (responseState == uniqueState) {
                            // This is our request, obtain the code!
                            request.url.getQueryParameter("code")?.let { code ->
                                // Got it!
                                Log.d(TAG, "Authorization code: $code")
                                onAuthorizationCodeRetrieved(code)
                                return true

                            } ?: run {
                                // User cancelled the login flow
                            }
                        }
                    }
                }
                return super.shouldOverrideUrlLoading(view, request)
            }
        }


        // Load OAuth Uri
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(uri.toString())
    }

    // Call this method after obtaining the authorization code
    // on the WebView to obtain the tokens
    private fun onAuthorizationCodeRetrieved(authorizationCode: String) {

        // Show Loading Indicator
        progressBar.visibility = View.VISIBLE

        // Create Twitch Service
        val httpClient = Network.createHttpClient(this.applicationContext)
        val service = TwitchApiService(httpClient)

        // Get Tokens from Twitch
        scopeMainThread.launch {
            scopeIO.async {
                Log.d(TAG, "Getting tokens with authorization code: $authorizationCode")
                val tokenResponse = service.getTokens(authorizationCode)
                Log.d(TAG, "TOKEN RESPONSE ${tokenResponse?.accessToken}. Refresh Token: ${tokenResponse?.refreshToken}")
                // Save access token and refresh token using the SessionManager class
                tokenResponse?.accessToken?.let { SessionManager(this@OAuthActivity).saveAccessToken(it) }
                tokenResponse?.refreshToken?.let { SessionManager(this@OAuthActivity).saveRefreshToken(it) }
            }.await()
            Log.d(TAG, "TOKEN RESPONSE2")
            startActivity(Intent(this@OAuthActivity, StreamsActivity::class.java))
        }


    }
}