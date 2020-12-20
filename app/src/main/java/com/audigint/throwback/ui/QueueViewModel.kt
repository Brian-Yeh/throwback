package com.audigint.throwback.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.audigint.throwback.data.Song
import com.audigint.throwback.utill.QueueManager

class QueueViewModel @ViewModelInject constructor(
    queueManager: QueueManager
) : ViewModel() {
    val currentQueue: LiveData<List<Song>> = queueManager.queue
}