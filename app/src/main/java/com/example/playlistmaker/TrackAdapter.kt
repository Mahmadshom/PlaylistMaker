package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter(private val tracks: List<Track>) : RecyclerView.Adapter<TrackViewHolder>() {
    var onItemClickListener: ((Track) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        // Inflate выполняется в конструкторе ViewHolder через передачу View
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
       val track = tracks[position]
        holder.bind(track)

        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(track)

        }
    }

    override fun getItemCount(): Int = tracks.size
}