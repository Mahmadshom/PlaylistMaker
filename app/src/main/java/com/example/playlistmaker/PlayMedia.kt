package com.example.playlistmaker


import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Looper
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.postDelayed
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.logging.Handler

class PlayMedia : AppCompatActivity() {
    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    private lateinit var btnPlay: ImageButton

    private val handler = android.os.Handler(Looper.getMainLooper())


    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

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
        btnPlay = findViewById(R.id.btn_play)
        btnPlay.isEnabled = false
        // 2. Получаем объект трека
        val track = intent.getSerializableExtra("selected_track") as Track

// 3. Заполняем экран
        trackName.text = track.trackName
        artistName.text = track.artistName
        collectionName.text = track.collectionName
        genreName.text = track.primaryGenreName
        countryName.text = track.country
// Для времени используй SimpleDateFormat ("mm:ss")
        duration.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        durationTotal.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        if (track.releaseDate != null && track.releaseDate.length >= 4) {
            releaseDate.text = track.releaseDate.substring(0, 4)
        } else {
            releaseDate.text = ""
        }

// 4. Загрузка обложки (используй Glide)
        Glide.with(this)
            .load(track.artworkUrl100.replace("100x100bb", "512x512bb")) // делаем качество лучше
            .placeholder(R.drawable.placeholder_stab)
            .centerCrop()
            .transform(RoundedCorners(12))
            .into(albumImage)

            preparePlayer(track.previewUrl)

             btnPlay.setOnClickListener {
            playbackControl()
        }
    }
    private val updateTimerRunnable = object : Runnable {
        override fun run() {
            if (playerState == STATE_PLAYING) {
                val currentTime = mediaPlayer.currentPosition
                // Обновляем текстовое поле
                findViewById<TextView>(R.id.time).text =
                    SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentTime)

                // Планируем следующее обновление через 300 мс
                handler.postDelayed(this, 300L)
            }
        }
    }
      private  fun preparePlayer(url: String?) {
          if (url.isNullOrEmpty()){ return}
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                btnPlay.isEnabled = true
                playerState = STATE_PREPARED
            }
            mediaPlayer.setOnCompletionListener {
                playerState = STATE_PREPARED
                // Когда трек доиграл — возвращаем иконку Play
                findViewById<ImageButton>(R.id.btn_play).setImageResource(R.drawable.ic_play_circle)

                handler.removeCallbacks(updateTimerRunnable)
                findViewById<TextView>(R.id.time).text = "00:00"

            }
        }

        private fun playbackControl() {
            when (playerState) {
                STATE_PLAYING -> pausePlayer()
                STATE_PREPARED, STATE_PAUSED -> startPlayer()

            }
        }

        private fun startPlayer() {
            mediaPlayer.start()
            btnPlay.setImageResource(R.drawable.ic_pause_circle)
            playerState = STATE_PLAYING
            handler.post(updateTimerRunnable)
        }

        private fun pausePlayer() {
            mediaPlayer.pause()
            btnPlay.setImageResource(R.drawable.ic_play_circle)
            playerState = STATE_PAUSED
            // Останавливаем цикл обновлений
            handler.removeCallbacks(updateTimerRunnable)
        }
    // 3. Важно освободить ресурсы при выходе
    override fun onPause() {
        super.onPause()
        if (playerState == STATE_PLAYING) {
            pausePlayer()
        }
        handler.removeCallbacks(updateTimerRunnable)

    }
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateTimerRunnable)
        mediaPlayer.release()
    }


}
