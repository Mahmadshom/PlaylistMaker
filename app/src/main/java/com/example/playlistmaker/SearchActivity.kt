package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
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

class SearchActivity : AppCompatActivity() {

    private lateinit var historyLayout: View
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var clearHistoryButton: Button
    private lateinit var searchHistory: SearchHistory
    private val historyTracks = mutableListOf<Track>()
    private val historyAdapter = TrackAdapter(historyTracks)

    private lateinit var placeholderLayout: View

    companion object {
        private const val ITUNES_BASE_URL = "https://itunes.apple.com"
        private const val SEARCH_TEXT_KEY = "SEARCH_TEXT"
    }
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

        historyLayout = findViewById(R.id.history_layout)
        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)

        // Инициализация View
        placeholderLayout = findViewById(R.id.placeholder_layout)
        searchEditText = findViewById(R.id.search_edit_textId)
        clearButton = findViewById(R.id.button_clear_id)
        recyclerView = findViewById(R.id.recyclerView)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        placeholderImage = findViewById(R.id.placeholderImage)
        refreshButton = findViewById(R.id.refreshButton)








        val toolbar =
            findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.search_button_back)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = trackAdapter

        toolbar.setNavigationOnClickListener { finish() }

        clearButton.setOnClickListener {
            searchEditText.setText("")
            tracks.clear()
            trackAdapter.notifyDataSetChanged()
            hidePlaceholder()
            hideKeyboard()

            val history = searchHistory.getHistory()
            if (history.isNotEmpty()) {
                historyTracks.clear()
                historyTracks.addAll(history)
                historyAdapter.notifyDataSetChanged()
                historyLayout.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }
        }
        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            val history = searchHistory.getHistory()
            historyLayout.visibility =
                if (hasFocus && searchEditText.text.isEmpty() && history.isNotEmpty()) {
                    historyTracks.clear()
                    historyTracks.addAll(history)
                    historyAdapter.notifyDataSetChanged()
                    historyLayout.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }


        val simpleTextWatcher = object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE

                val history = searchHistory.getHistory()
                if (s.isNullOrEmpty() && searchEditText.hasFocus() && history.isNotEmpty()) {
                    historyLayout.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    placeholderLayout.visibility = View.GONE
                } else {
                    historyLayout.visibility = View.GONE
                    // recyclerView покажем, когда придут результаты поиска
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

        searchEditText.addTextChangedListener(simpleTextWatcher)
        trackAdapter.onItemClickListener = { track ->
            searchHistory.addTrack(track)
            // Здесь позже добавишь переход на экран плеера
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
            }}
refreshButton.setOnClickListener { search() }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search_activity_id)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }

    private fun search() {
        val query = searchEditText.text.toString()
        if (query.isNotEmpty()) {
            hidePlaceholder()

            itunesService.search(query).enqueue(object : Callback<TrackResponse> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                    if (response.isSuccessful) {
                        tracks.clear()
                        val results = response.body()?.results
                        if (!results.isNullOrEmpty()) {
                            tracks.addAll(results)
                            trackAdapter.notifyDataSetChanged()
                            recyclerView.visibility = View.VISIBLE
                        } else {
                            // Пустой результат
                            showPlaceholder(getString(R.string.nothing_found), R.drawable.nothing_found_img, false)
                        }
                    } else {
                        // Ошибка сервера
                        showPlaceholder(getString(R.string.server_error), R.drawable.server_error_img, true)
                    }
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    // Ошибка сети
                    showPlaceholder(getString(R.string.server_error), R.drawable.server_error_img, true)
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
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, searchEditText.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val saveText = savedInstanceState.getString(SEARCH_TEXT_KEY,"")
        searchEditText.setText(saveText)
    }
}