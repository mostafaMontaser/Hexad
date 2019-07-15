package com.hexad.dispatcher

import com.hexad.model.Movies
import com.hexad.repo.OfflineRepo
import org.junit.Test
import org.mockito.Mockito

class MainActivityDispatcherTest {

    @Test
    fun testGetMovies() {
        val offlineRepo = Mockito.mock(OfflineRepo::class.java)
        val dispatcher = MainActivityDispatcher(offlineRepo!!)
        dispatcher.getMovies()
    }

    @Test
    fun testSetMovies() {
        val offlineRepo = Mockito.mock(OfflineRepo::class.java)
        val dispatcher = MainActivityDispatcher(offlineRepo!!)
        dispatcher.setMovies("", Movies(ArrayList()))
    }

    @Test
    fun testLoadObjectFromAssetAndSaveToCache() {
        val offlineRepo = Mockito.mock(OfflineRepo::class.java)
        val dispatcher = MainActivityDispatcher(offlineRepo!!)
        dispatcher.loadObjectFromAssetAndSaveToCache<Movies>("", "", Movies::class.java)
    }

}