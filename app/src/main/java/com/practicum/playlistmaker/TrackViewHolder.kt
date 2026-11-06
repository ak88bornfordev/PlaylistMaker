package com.practicum.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
) {

    private val artwork: ImageView = itemView.findViewById(R.id.ivArtwork)
    private val trackName: TextView = itemView.findViewById(R.id.tvTrackName)
    private val artistName: TextView = itemView.findViewById(R.id.tvArtistName)
    private val trackTime: TextView = itemView.findViewById(R.id.tvTrackTime)

    private val cornerRadiusPx: Int =
        itemView.resources.getDimensionPixelSize(R.dimen.track_artwork_corner_radius)

    fun bind(model: Track) {
        trackName.text = model.trackName.orEmpty()
        artistName.text = model.artistName.orEmpty()
        trackTime.text = model.trackTimeMillis?.let { millis ->
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(millis)
        }.orEmpty()

        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.ic_track_placeholder)
            .error(R.drawable.ic_track_placeholder)
            .transform(CenterCrop(), RoundedCorners(cornerRadiusPx))
            .into(artwork)
    }
}
