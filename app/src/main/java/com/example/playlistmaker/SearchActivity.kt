package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.jvm.java
import com.google.android.material.appbar.MaterialToolbar


class SearchActivity : AppCompatActivity() {

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }
    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }
    private val handler = android.os.Handler(android.os.Looper.getMainLooper())
    private val searchRunnable = Runnable { search() }

    companion object {
        private const val ITUNES_BASE_URL = "https://itunes.apple.com"
        private const val SEARCH_TEXT_KEY = "SEARCH_TEXT"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L // Задержка 2 секунды
        private const val CLICK_DEBOUNCE_DELAY = 1000L // Задержка между кликами
    }
    private var isClickAllowed = true
    private lateinit var historyLayout: View
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var clearHistoryButton: Button
    private lateinit var searchHistory: SearchHistory
    private val historyTracks = mutableListOf<Track>()
    private val historyAdapter = TrackAdapter(historyTracks)

    private lateinit var placeholderLayout: View
    private lateinit var progressBar: View


    private val retrofit = Retrofit.Builder()
        .baseUrl(ITUNES_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val itunesService = retrofit.create(ITunesApi::class.java)

    private val tracks = mutableListOf<Track>()
    private val trackAdapter = TrackAdapter(tracks)
    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageButton
    private lateinit var recyclerView: RecyclerView

    // Элементы плейсхолдера
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderImage: ImageView
    private lateinit var refreshButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_seach)

        val sharedPrefs = getSharedPreferences("playlist_maker_prefs", MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPrefs)


        progressBar = findViewById(R.id.progressBarId)
        historyLayout = findViewById(R.id.history_layout)
        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)
        placeholderLayout = findViewById(R.id.placeholder_layout)
        searchEditText = findViewById(R.id.search_edit_textId)
        clearButton = findViewById(R.id.button_clear_id)
        recyclerView = findViewById(R.id.recyclerView)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        placeholderImage = findViewById(R.id.placeholderImage)
        refreshButton = findViewById(R.id.refreshButton)
        val toolbar =findViewById<MaterialToolbar>(R.id.search_button_back)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = trackAdapter

        toolbar.setNavigationOnClickListener { finish() }




        clearButton.setOnClickListener {
            searchEditText.setText("")
            tracks.clear()
            trackAdapter.notifyDataSetChanged()
            hidePlaceholder()
            hideKeyboard()
            showHistory()
        }

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            showHistory()
        }



        val simpleTextWatcher = object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                showHistory()
                if (s?.isEmpty() == true) {
                    // Если текст стерли: отменяем поиск и показываем историю
                    handler.removeCallbacks(searchRunnable)
                    showHistory()
                } else {
                    // Если текст вводится: скрываем историю и запускаем debounce
                    historyLayout.visibility = View.GONE
                    searchDebounce()
                }
            }



            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        }
        searchEditText.addTextChangedListener(simpleTextWatcher)

        // Кнопка "Обновить" при ошибке
        refreshButton.setOnClickListener {
            search()
        }

        historyRecyclerView.adapter = historyAdapter


        historyAdapter.onItemClickListener = { track ->
            if (clickDebounce()) {
                // При нажатии на элемент истории просто переходим в плеер
                val intent = Intent(this, PlayMedia::class.java)
                intent.putExtra("selected_track", track)
                startActivity(intent)
            }
        }

        trackAdapter.onItemClickListener = { track ->
            if (clickDebounce()) {
                searchHistory.addTrack(track)
                // Здесь позже добавим переход на экран плеера
                val intent = Intent(this, PlayMedia::class.java)
                intent.putExtra("selected_track", track)
                startActivity(intent)
            }
        }
        clearHistoryButton.setOnClickListener {
            searchHistory.clearHistory()
            historyTracks.clear()
            historyAdapter.notifyDataSetChanged()
            historyLayout.visibility = View.GONE
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
                true
            } else {
                false
            }
        }
        refreshButton.setOnClickListener { search() }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search_activity_id)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }

    fun showHistory() {
        val history = searchHistory.getHistory()
        if (searchEditText.hasFocus() && searchEditText.text.isEmpty() && history.isNotEmpty()) {
            historyTracks.clear()
            historyTracks.addAll(history)
            historyAdapter.notifyDataSetChanged()
            historyLayout.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            placeholderLayout.visibility = View.GONE
        } else {
            historyLayout.visibility = View.GONE
        }
    }

    private fun search() {
        val query = searchEditText.text.toString()
        if (query.isNotEmpty()) {

            progressBar.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE

            hidePlaceholder()

            itunesService.search(query).enqueue(object : Callback<TrackResponse> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<TrackResponse>,
                    response: Response<TrackResponse>
                ) {
                    progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        tracks.clear()
                        val results = response.body()?.results
                        if (!results.isNullOrEmpty()) {
                            tracks.addAll(results)
                            trackAdapter.notifyDataSetChanged()
                            recyclerView.visibility = View.VISIBLE
                        } else {
                            // Пустой результат
                            showPlaceholder(
                                getString(R.string.nothing_found),
                                R.drawable.nothing_found_img,
                                false
                            )
                        }
                    } else {
                        // Ошибка сервера
                        showPlaceholder(
                            getString(R.string.server_error),
                            R.drawable.server_error_img,
                            true
                        )
                    }
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    // Ошибка сети
                    progressBar.visibility = View.GONE
                    showPlaceholder(
                        getString(R.string.server_error),
                        R.drawable.server_error_img,
                        true
                    )
                }
            })
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showPlaceholder(text: String, imageRes: Int, showRefresh: Boolean) {
        tracks.clear()
        trackAdapter.notifyDataSetChanged()

        recyclerView.visibility = View.GONE
        placeholderLayout.visibility = View.VISIBLE


        placeholderMessage.text = text
        placeholderImage.setImageResource(imageRes)

        refreshButton.visibility = if (showRefresh) View.VISIBLE else View.GONE
    }

    private fun hidePlaceholder() {
        placeholderLayout.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE // Возвращаем список
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, searchEditText.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val saveText = savedInstanceState.getString(SEARCH_TEXT_KEY, "")
        searchEditText.setText(saveText)
    }


}