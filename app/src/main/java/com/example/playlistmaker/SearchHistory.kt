package com.example.playlistmaker
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

const val SEARCH_HISTORY_KEY = "key_for_search_history"

class SearchHistory(private val sharedPreferences: SharedPreferences) {

    private val gson = Gson()

    val sizeLimit: Int = 10

    // Получить список треков из истории
    fun getHistory(): List<Track> {
        val json = sharedPreferences.getString(SEARCH_HISTORY_KEY, null) ?: return emptyList()
        val type = object : TypeToken<List<Track>>() {}.type
        return gson.fromJson(json, type)
    }

    // Добавить трек в историю
    fun addTrack(track: Track) {
        val history = getHistory().toMutableList()

        // Удаляем трек, если он уже есть (чтобы не было дублей)
        history.removeIf { it.trackId == track.trackId }

        // Добавляем в начало списка
        history.add(0, track)

        // Ограничиваем размер до 10
       if (history.size > sizeLimit) {
            history.removeAt(sizeLimit)
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
