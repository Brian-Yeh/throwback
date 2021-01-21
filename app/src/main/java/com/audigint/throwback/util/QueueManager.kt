package com.audigint.throwback.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.audigint.throwback.data.*
import com.audigint.throwback.di.IoDispatcher
import com.audigint.throwback.di.MainDispatcher
import com.audigint.throwback.ui.models.QueueItem
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
class QueueManager @Inject constructor(
    private val repository: SongRepository,
    private val metadataService: MetadataService,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    sharedPreferences: PreferencesStorage
) : CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = ioDispatcher + job

    private val _queue = MutableLiveData<List<QueueItem>>()
    val queue: LiveData<List<QueueItem>>
        get() = _queue
    var year: Int = sharedPreferences.year
        set(value) {
            setQueueWithYear(value)
        }

    init {
        setQueueWithYear(sharedPreferences.year)
    }

    private suspend fun getQueueFromRepository(year: Int) {
        repository.setAndGetCurrentQueue(year).collect {
            val songList = it.shuffled()
            val queueItems = fetchMetadataForSongs(songList)

            withContext(mainDispatcher) {
                _queue.value = queueItems
            }
        }
    }

    fun setQueueWithYear(year: Int) {
        launch {
            getQueueFromRepository(year)
        }
    }

    private suspend fun fetchMetadataForSongs(songList: List<Song>): List<QueueItem> {
        metadataService.fetchMetadata(songList)
        return songList.mapNotNull { song ->
            metadataService.getMetadataForId(song.id)?.let { metadata ->
                QueueItem(song.id, metadata.title, metadata.artist, metadata.artworkUrl, song)
            }
        }
    }
}