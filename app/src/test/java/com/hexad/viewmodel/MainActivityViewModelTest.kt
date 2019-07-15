package com.hexad.viewmodel

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.ViewModel
import com.hexad.TrampolineSchedulerRule
import com.hexad.dispatcher.MainActivityDispatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mockito


class MainActivityViewModelTest : ViewModel() {
    // For Live Data
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()
    // For RX Threading
    @get:Rule
    var trampolineSchedulerRule: TestRule = TrampolineSchedulerRule()
    private var viewModel = MainActivityViewModel()
    private var dispatcher: MainActivityDispatcher? = null
    @Before
    fun init() {
        dispatcher = Mockito.mock(MainActivityDispatcher::class.java)
        viewModel.mainActivityDispatcher = dispatcher!!
    }

    @Test
    fun loadDataFirstTime() {
        viewModel.loadDataFirstTime()
    }

    @Test
    fun testGetMovieLiveData() {
        checkNotNull(viewModel.getMovieLiveData())
    }

    @Test
    fun getMovies() {
        viewModel.getMovies()
    }

    @Test
    fun testRateMovie() {
        viewModel.rateMovie(null, 1f)
    }

    @Test
    fun testStopRandomRankingIfStarted() {
        viewModel.stopRandomRankingIfStarted()
    }

}
