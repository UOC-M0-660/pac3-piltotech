package edu.uoc.pac3.twitch.streams

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.uoc.pac3.R
import edu.uoc.pac3.data.SessionManager
import edu.uoc.pac3.data.TwitchApiService
import edu.uoc.pac3.data.network.Network
import edu.uoc.pac3.data.streams.Stream
import edu.uoc.pac3.data.streams.StreamsResponse
import edu.uoc.pac3.twitch.profile.ProfileActivity
import io.ktor.client.*
import kotlinx.coroutines.*
import kotlin.collections.ArrayList


class StreamsActivity : AppCompatActivity() {

    private val TAG = "StreamsActivity"

    private val job = Job()
    private val scopeMainThread = CoroutineScope(job + Dispatchers.Main)
    private val scopeIO = CoroutineScope(job + Dispatchers.IO)

    private lateinit var adapter: StreamsAdapter
    private var streamsResponse: StreamsResponse? = null
    private var paginationCursor: String =""

    private val streamList: MutableList<Stream> = ArrayList()

    private lateinit var httpClient: HttpClient
    private lateinit var service: TwitchApiService





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_streams)
        // Init RecyclerView
        initRecyclerView()

        //  Get Streams
        httpClient =
                Network.createHttpClient(this@StreamsActivity.applicationContext)
        service = TwitchApiService(httpClient)

        scopeMainThread.launch {
            scopeIO.async {
                Log.d(
                        TAG,
                    "Getting streams. Token:" + SessionManager(this@StreamsActivity).getAccessToken() + " Cursor" + paginationCursor
                )
                streamsResponse = service.getStreams(
                    null
                )

            }.await()
            Log.d(TAG, "Streams response:")
            streamsResponse?.data?.let { streamList.addAll(it) }
            streamsResponse?.pagination?.cursor?.let { paginationCursor = it}

            adapter.setStreams(streamList)
        }

    }

    private fun initRecyclerView() {
        // Implement
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        // Set Layout Manager
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        // Init Adapter
        adapter = StreamsAdapter(streamList, this)
        recyclerView.adapter = adapter

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {

                    Toast.makeText(this@StreamsActivity, "Loading next streams", Toast.LENGTH_LONG).show()

                    scopeMainThread.launch {
                        scopeIO.async {
                            Log.d(
                                    TAG,
                                "Getting streams. Token:" + SessionManager(this@StreamsActivity).getAccessToken() + " Cursor" + paginationCursor
                            )
                            streamsResponse = service.getStreams(
                                paginationCursor
                            )

                        }.await()
                        Log.d(TAG, "Streams response:")
                        streamsResponse?.data?.let { streamList.addAll(it) }

                        adapter.setStreams(streamList)
                    }
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.user_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.profileTitle) {
            startActivity(Intent(this, ProfileActivity::class.java))
            }

        return super.onOptionsItemSelected(item)
    }

}