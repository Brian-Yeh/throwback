package com.audigint.throwback.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.audigint.throwback.data.Song
import com.audigint.throwback.data.SongRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
class QueueManager @Inject constructor(repository: SongRepository) : CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    private val _queue = MutableLiveData<List<Song>>()
    val queue: LiveData<List<Song>>
        get() = _queue

    init {
        launch {
            repository.currentQueue.collect {
                withContext(Dispatchers.Main) {
                    _queue.value = it.shuffled()
                }
            }
        }
    }
}