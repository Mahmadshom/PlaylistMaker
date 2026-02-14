package com.example.playlistmaker
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

const val SEARCH_HISTORY_KEY = "key_for_search_history"

class SearchHistory(private val sharedPreferences: SharedPreferences) {

    private val gson = Gson()

    // Получить список треков из истории
    fun getHistory(): MutableList<Track> {
        val json = sharedPreferences.getString(SEARCH_HISTORY_KEY, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<Track>>() {}.type
        return gson.fromJson(json, type)
    }

    // Добавить трек в историю
    fun addTrack(track: Track) {
        val history = getHistory()

        // Удаляем трек, если он уже есть (чтобы не было дублей)
        history.removeIf { it.trackId == track.trackId }

        // Добавляем в начало списка
        history.add(0, track)

        // Ограничиваем размер до 10
        if (history.size > 10) {
            history.removeAt(10)
        }

        saveHistory(history)
    }

    // Очистить историю
    fun clearHistory() {
        sharedPreferences.edit().remove(SEARCH_HISTORY_KEY).apply()
    }

    private fun saveHistory(history: List<Track>) {
        val json = gson.toJson(history)
        sharedPreferences.edit()
            .putString(SEARCH_HISTORY_KEY, json)
            .apply()
    }
}
