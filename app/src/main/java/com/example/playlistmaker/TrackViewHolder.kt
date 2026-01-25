package com.example.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackName: TextView = itemView.findViewById(R.id.trackName)
    private val artistName: TextView = itemView.findViewById(R.id.artistName)
    private val trackTime: TextView = itemView.findViewById(R.id.trackTime)
    private val trackCover: ImageView = itemView.findViewById(R.id.trackCover)

    fun bind(model: Track) {
        trackName.text = model.trackName
        artistName.text = model.artistName
        trackTime.text = model.trackTime

        Glide.with(itemView) // Используем itemView, как требует задание
            .load(model.artworkUrl100)
            .placeholder(R.drawable.stab_icon) // Замените на ваш ресурс заглушки
            .centerCrop() // Чтобы картинка заполнила квадрат
            .transform(RoundedCorners(2)) // Скругление (число в пикселях)
            .into(trackCover)
    }
}