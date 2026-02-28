package com.example.playlistmaker


import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class PlayMedia : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.play_media)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.play_media)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        btnBack.setOnClickListener {
            finish()
        }

        val trackName = findViewById<TextView>(R.id.tv_track_name)
        val artistName = findViewById<TextView>(R.id.tv_artist)
        val albumImage = findViewById<ImageView>(R.id.img_album)
        val duration = findViewById<TextView>(R.id.time)
        val durationTotal = findViewById<TextView>(R.id.tv_duration_value)
        val collectionName = findViewById<TextView>(R.id.tv_collection_value)
        val releaseDate = findViewById<TextView>(R.id.tv_year_value)
        val genreName = findViewById<TextView>(R.id.genre_name)
        val countryName = findViewById<TextView>(R.id.country_name)

        // 2. Получаем объект трека
        val track = intent.getSerializableExtra("selected_track") as Track

// 3. Заполняем экран
        trackName.text = track.trackName
        artistName.text = track.artistName
        collectionName.text = track.collectionName
        releaseDate.text = track.releaseDate
        genreName.text = track.primaryGenreName
        countryName.text = track.country
// Для времени используй SimpleDateFormat ("mm:ss")
        duration.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        durationTotal.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)



// 4. Загрузка обложки (используй Glide)
        Glide.with(this)
            .load(track.artworkUrl100.replace("100x100bb", "512x512bb")) // делаем качество лучше
            .placeholder(R.drawable.placeholder_stab)
            .centerCrop()
            .transform(RoundedCorners(12))
            .into(albumImage)

    }
}