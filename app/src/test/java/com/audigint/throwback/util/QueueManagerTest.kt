package com.audigint.throwback.util

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.audigint.throwback.testUtils.fakes.FakeMetadataService
import com.audigint.throwback.testUtils.fakes.FakePreferencesStorage
import com.audigint.throwback.testUtils.fakes.FakeSongRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

class QueueManagerTest {
    private lateinit var queueManager: QueueManager

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        queueManager = QueueManager(
            createTestRepository(),
            createTestMetadataService(),
            mainCoroutineRule.testDispatcher,
            mainCoroutineRule.testDispatcher,
            createTestPreferencesStorage()
        )
    }

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun setQueueWithYear_ReturnsNotNull() {
        queueManager.setQueueWithYear(Constants.DEFAULT_YEAR)

        val value = LiveDataTestUtil.getValue(queueManager.queue)
        assertNotNull(value)
    }

    private fun createTestRepository() = FakeSongRepository()

    private fun createTestMetadataService() = FakeMetadataService()

    private fun createTestPreferencesStorage() = FakePreferencesStorage()
}