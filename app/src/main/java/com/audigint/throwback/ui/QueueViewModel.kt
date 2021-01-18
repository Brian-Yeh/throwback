package com.audigint.throwback.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.audigint.throwback.di.MainDispatcher
import com.audigint.throwback.ui.models.QueueItem
import com.audigint.throwback.util.QueueManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

class QueueViewModel @ViewModelInject constructor(
    private val queueManager: QueueManager,
    @MainDispatcher private val dispatcher: CoroutineDispatcher
) : ViewModel() {
    private val _currentQueue = MutableLiveData<List<QueueItem>>()
    val currentQueue: LiveData<List<QueueItem>> = _currentQueue

    init {
        viewModelScope.launch(dispatcher) {
            queueManager.queue.value?.let { queueItems ->
                _currentQueue.value = queueItems
            }
        }
    }
}