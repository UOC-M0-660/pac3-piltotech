package edu.uoc.pac3.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import edu.uoc.pac3.R
import edu.uoc.pac3.oauth.ErrorInterceptor

/**
 * Created by alex on 06/09/2020.
 */

class SessionManager(val context: Context) {

    val PREFS_NAME = "edu.uoc.pac3.sharedpreferences"
    val sharedPref: SharedPreferences = context.getSharedPreferences(PREFS_NAME, 0)

    fun isUserAvailable(): Boolean {
        // TODO: Implement
        return false
    }

    fun getAccessToken(): String? {
        //Log.d("Session", "Get AccessToken: ${sharedPref.getString(context.getString(R.string.preference_acces_token), null)}.")
        return sharedPref.getString(context.getString(R.string.preference_acces_token), null)
    }

    fun saveAccessToken(accessToken: String) {
        with (sharedPref.edit()) {
            putString(context.getString(R.string.preference_acces_token), accessToken)
            commit()
        }
    }

    fun clearAccessToken() {
        with (sharedPref.edit()) {
            putString(context.getString(R.string.preference_acces_token), null)
            commit()
        }
    }

    fun getRefreshToken(): String? {
        return sharedPref.getString(context.getString(R.string.preference_refresh_token), null)
    }

    fun saveRefreshToken(refreshToken: String) {
        with (sharedPref.edit()) {
            putString(context.getString(R.string.preference_refresh_token), refreshToken)
            commit()
        }
    }

    fun clearRefreshToken() {
        with (sharedPref.edit()) {
            putString(context.getString(R.string.preference_refresh_token), null)
            commit()
        }
    }

}