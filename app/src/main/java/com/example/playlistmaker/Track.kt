package com.example.playlistmaker

import java.sql.RowId

data class Track (
    val trackId: String,
    val trackName: String,
    val artistName: String,
    val trackTime: Long,
    val  artworkUrl100: String
)



