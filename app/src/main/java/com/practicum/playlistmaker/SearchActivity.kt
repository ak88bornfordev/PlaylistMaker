package com.practicum.playlistmaker

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
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private var searchText: String = SEARCH_TEXT_DEF
    private var lastQuery: String = SEARCH_TEXT_DEF
    private var currentCall: Call<TracksResponse>? = null

    private lateinit var searchEditText: EditText
    private lateinit var tracksRecyclerView: RecyclerView
    private lateinit var placeholderLayout: LinearLayout
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderText: TextView
    private lateinit var retryButton: Button

    private val trackAdapter = TrackAdapter()
    private val iTunesApi: ITunesApi by lazy {
        Retrofit.Builder()
            .baseUrl(ITUNES_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApi::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        findViewById<View>(R.id.root).applySystemBarsPadding()

        searchEditText = findViewById(R.id.searchEditText)
        tracksRecyclerView = findViewById(R.id.tracksRecyclerView)
        placeholderLayout = findViewById(R.id.placeholderLayout)
        placeholderImage = findViewById(R.id.placeholderImage)
        placeholderText = findViewById(R.id.placeholderText)
        retryButton = findViewById(R.id.retryButton)
        val clearButton = findViewById<ImageView>(R.id.clearButton)

        tracksRecyclerView.adapter = trackAdapter

        findViewById<ImageButton>(R.id.back_button).setOnClickListener { finish() }

        clearButton.setOnClickListener {
            searchEditText.setText("")
            hideKeyboard(searchEditText)
            searchEditText.clearFocus()
        }

        retryButton.setOnClickListener {
            searchTracks(lastQuery)
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard(searchEditText)
                searchTracks(searchEditText.text.toString())
                true
            } else {
                false
            }
        }

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                searchText = s?.toString() ?: SEARCH_TEXT_DEF
                if (s.isNullOrEmpty()) {
                    clearSearchResults()
                }
            }

            override fun afterTextChanged(s: Editable?) = Unit
        }
        searchEditText.addTextChangedListener(searchTextWatcher)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(SEARCH_TEXT_KEY, SEARCH_TEXT_DEF)
        searchEditText.setText(searchText)
        searchEditText.setSelection(searchText.length)
    }

    override fun onDestroy() {
        cancelCurrentCall()
        super.onDestroy()
    }

    private fun searchTracks(query: String) {
        val normalizedQuery = query.trim()
        if (normalizedQuery.isEmpty()) {
            clearSearchResults()
            return
        }

        lastQuery = normalizedQuery
        cancelCurrentCall()
        trackAdapter.updateTracks(emptyList())
        tracksRecyclerView.visibility = View.GONE
        placeholderLayout.visibility = View.GONE

        val call = iTunesApi.search(normalizedQuery)
        currentCall = call
        call.enqueue(object : Callback<TracksResponse> {
            override fun onResponse(
                call: Call<TracksResponse>,
                response: Response<TracksResponse>,
            ) {
                if (call !== currentCall) return
                currentCall = null

                if (response.isSuccessful) {
                    val tracks = response.body()?.results.orEmpty()
                    if (tracks.isEmpty()) {
                        showNoResults()
                    } else {
                        showTracks(tracks)
                    }
                } else {
                    showError()
                }
            }

            override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                if (call !== currentCall) return
                currentCall = null
                showError()
            }
        })
    }

    private fun showTracks(tracks: List<Track>) {
        trackAdapter.updateTracks(tracks)
        placeholderLayout.visibility = View.GONE
        tracksRecyclerView.visibility = View.VISIBLE
    }

    private fun showNoResults() {
        showPlaceholder(
            imageRes = R.drawable.ic_no_results,
            messageRes = R.string.nothing_found,
            showRetry = false,
        )
    }

    private fun showError() {
        showPlaceholder(
            imageRes = R.drawable.ic_search_error,
            messageRes = R.string.connection_error,
            showRetry = true,
        )
    }

    private fun showPlaceholder(imageRes: Int, messageRes: Int, showRetry: Boolean) {
        trackAdapter.updateTracks(emptyList())
        tracksRecyclerView.visibility = View.GONE
        placeholderImage.setImageResource(imageRes)
        placeholderText.setText(messageRes)
        retryButton.visibility = if (showRetry) View.VISIBLE else View.GONE
        placeholderLayout.visibility = View.VISIBLE
    }

    private fun clearSearchResults() {
        cancelCurrentCall()
        trackAdapter.updateTracks(emptyList())
        tracksRecyclerView.visibility = View.GONE
        placeholderLayout.visibility = View.GONE
    }

    private fun cancelCurrentCall() {
        val call = currentCall
        currentCall = null
        call?.cancel()
    }

    private fun clearButtonVisibility(s: CharSequence?): Int =
        if (s.isNullOrEmpty()) View.GONE else View.VISIBLE

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    companion object {
        private const val SEARCH_TEXT_KEY = "SEARCH_TEXT"
        private const val SEARCH_TEXT_DEF = ""
        private const val ITUNES_BASE_URL = "https://itunes.apple.com"
    }
}
