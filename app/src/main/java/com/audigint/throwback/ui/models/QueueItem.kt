package com.audigint.throwback.ui.models

import com.audigint.throwback.data.Song

data class QueueItem(
    var id: String?,
    var title: String?,
    var artist: String?,
    var artworkUrl: String?,
    var song: Song?
)