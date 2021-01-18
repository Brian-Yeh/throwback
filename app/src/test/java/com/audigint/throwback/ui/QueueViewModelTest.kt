package com.audigint.throwback.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.audigint.throwback.testUtils.fakes.FakeMetadataService
import com.audigint.throwback.testUtils.fakes.FakePreferencesStorage
import com.audigint.throwback.testUtils.fakes.FakeSongRepository
import com.audigint.throwback.util.MainCoroutineRule
import com.audigint.throwback.util.QueueManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class QueueViewModelTest {
    private lateinit var queueViewModel: QueueViewModel

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        val queueManager = createTestQueueManager()
        queueViewModel = QueueViewModel(queueManager, mainCoroutineRule.testDispatcher)
    }

    @Test
    fun currentQueueNotNull_AfterInit() {
        assertNotNull(LiveDataTestUtil.getValue(queueViewModel.currentQueue))
    }

    @Test
    fun currentQueuePopulated_AfterInit() {
        val queueItems = LiveDataTestUtil.getValue(queueViewModel.currentQueue)
        assertTrue(queueItems!!.isNotEmpty())
    }

    private fun createTestQueueManager() = QueueManager(
        FakeSongRepository(),
        FakeMetadataService(),
        mainCoroutineRule.testDispatcher,
        mainCoroutineRule.testDispatcher,
        FakePreferencesStorage()
    )
}