package com.audigint.throwback.util

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.audigint.throwback.testUtils.fakes.FakeMetadataService
import com.audigint.throwback.testUtils.fakes.FakePreferencesStorage
import com.audigint.throwback.testUtils.fakes.FakeSongRepository
import com.audigint.throwback.ui.models.QueueItem
import getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

class QueueManagerTest {
    lateinit var queueManager: QueueManager

    @ExperimentalCoroutinesApi
    val testDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        queueManager = QueueManager(createTestRepository(), createTestMetadataService(), createTestPreferencesStorage())
    }

    @ExperimentalCoroutinesApi
    @Before
    fun setupDispatcher() {
        Dispatchers.setMain(testDispatcher)
    }

    @ExperimentalCoroutinesApi
    @After
    fun tearDownDispatcher() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun setQueueWithYear_ReturnsNotNull() {
        val observer = Observer<List<QueueItem>> {}
        try {
            queueManager.queue.observeForever(observer)

            queueManager.setQueueWithYear(Constants.DEFAULT_YEAR)

            val value = queueManager.queue.getOrAwaitValue()
            assertNotNull(value)
        } finally {
            queueManager.queue.removeObserver(observer)
        }
    }

    @Test
    fun setQueueWithYear() {
    }

    fun createTestRepository() = FakeSongRepository()

    fun createTestMetadataService() = FakeMetadataService()

    fun createTestPreferencesStorage() = FakePreferencesStorage()
}