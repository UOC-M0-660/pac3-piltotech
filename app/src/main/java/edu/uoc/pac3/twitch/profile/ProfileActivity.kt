package edu.uoc.pac3.twitch.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import edu.uoc.pac3.R
import edu.uoc.pac3.data.SessionManager
import edu.uoc.pac3.data.TwitchApiService
import edu.uoc.pac3.data.network.Network
import edu.uoc.pac3.data.user.User
import edu.uoc.pac3.oauth.LoginActivity
import kotlinx.coroutines.*


class ProfileActivity : AppCompatActivity() {

    private val TAG = "ProfileActivity"

    private val job = Job()
    private val scopeMainThread = CoroutineScope(job + Dispatchers.Main)
    private val scopeIO = CoroutineScope(job + Dispatchers.IO)

    private var user: User? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val httpClient = Network.createHttpClient(this.applicationContext)
        val service = TwitchApiService(httpClient)

        scopeMainThread.launch {
            scopeIO.async {
                user = service.getUser()

            }.await()
            Log.d(TAG, "User response: ${user?.userName}")


            val userName = findViewById<TextView>(R.id.userNameTextView)
            val userDescription = findViewById<TextInputEditText>(R.id.userDescriptionEditText)
            val userImage = findViewById<ImageView>(R.id.imageView)

            user?.userName?.let { userName.setText(it) }
            user?.description?.let { userDescription.setText(it) }

            var imageurl = user?.imageUrl
            if (imageurl != null) {
                imageurl = imageurl.replace("{width}", "200", false)
                imageurl = imageurl.replace("{height}", "200", false)
                Glide.with(applicationContext)
                    .load(imageurl)
                    .into(userImage)
            }

            val updateButton = findViewById<MaterialButton>(R.id.updateDescriptionButton)
            updateButton.setOnClickListener(){
                scopeIO.async {
                    Log.d(
                        TAG,
                        "Udate user description: ${userDescription.getText().toString()}"
                    )
                    service.updateUserDescription(userDescription.getText().toString())
                }
            }

            val logoutButton = findViewById<MaterialButton>(R.id.logoutButton)
            logoutButton.setOnClickListener(){
                SessionManager(this@ProfileActivity).clearAccessToken()
                SessionManager(this@ProfileActivity).clearRefreshToken()
                val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }

        }
    }
}