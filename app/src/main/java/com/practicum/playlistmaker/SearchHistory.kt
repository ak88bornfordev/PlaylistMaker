package com.practicum.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val sharedPreferences: SharedPreferences) {

    private val gson = Gson()

    fun getTracks(): List<Track> {
        val json = sharedPreferences.getString(HISTORY_KEY, null) ?: return emptyList()
        val type = object : TypeToken<List<Track>>() {}.type
        return runCatching { gson.fromJson<List<Track>>(json, type) }.getOrNull().orEmpty()
    }

    fun addTrack(track: Track) {
        val tracks = getTracks()
            .filterNot { it.trackId == track.trackId }
            .toMutableList()
            .apply { add(0, track) }

        sharedPreferences.edit()
            .putString(HISTORY_KEY, gson.toJson(tracks.take(MAX_HISTORY_SIZE)))
            .apply()
    }

    fun clear() {
        sharedPreferences.edit().remove(HISTORY_KEY).apply()
    }

    private companion object {
        const val HISTORY_KEY = "search_history"
        const val MAX_HISTORY_SIZE = 10
    }
}
