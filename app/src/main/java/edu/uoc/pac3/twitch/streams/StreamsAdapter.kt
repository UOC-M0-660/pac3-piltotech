package edu.uoc.pac3.twitch.streams

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import edu.uoc.pac3.R
import edu.uoc.pac3.data.streams.Stream

class StreamsAdapter(private var streams: List<Stream>, activity: Activity) : RecyclerView.Adapter<StreamsAdapter.ViewHolder>() {

    private val mActivity = activity

    private fun getStream(position: Int): Stream {
        return streams[position]
    }

    fun setStreams(streams: List<Stream>) {
        this.streams = streams
        // Reloads the RecyclerView with new adapter data
        notifyDataSetChanged()
    }

    // Creates View Holder for re-use
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.streams_list, parent, false)

        return ViewHolder(view)
    }

    // Binds re-usable View for a given position
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val stream = getStream(position)
        holder.StreamName.text = stream.title
        holder.UserName.text = stream.userName
        holder.Position.text = (position + 1).toString()

        var imageurl = stream.url
        if (imageurl != null) {
            imageurl = imageurl.replace("{width}", "600", false)
            imageurl = imageurl.replace("{height}", "400", false)
        }

        Glide.with(holder.mView.context)
                .load(imageurl)
                .into(holder.StreamView)

    }

    // Returns total items in Adapter
    override fun getItemCount(): Int {
        return streams.size
    }

    // Holds an instance to the view for re-use
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val StreamName: TextView = view.findViewById(R.id.id_stream_name)
        val UserName: TextView = view.findViewById(R.id.id_user_name)
        val Position: TextView = view.findViewById(R.id.id_stream_position)
        val StreamView: ImageView = view.findViewById(R.id.id_stream_imageView)
        var mView: View = view
    }

}