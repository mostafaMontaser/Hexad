package com.hexad.repo

import android.content.SharedPreferences
import com.hexad.BaseTest
import com.hexad.model.Movies
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito


class OfflineRepoTest: BaseTest() {
    var offlineRepo: OfflineRepo? = null
    var secureEditor: SharedPreferences.Editor? = null
    var secureSharedPref: SharedPreferences? = null
    var assetFileManager: AssetFileManager? = null

    @Before
    fun init() {
        secureEditor = Mockito.mock(SharedPreferences.Editor::class.java)
        Mockito.`when`(secureEditor!!.putString(Mockito.anyString(), Mockito.any())).thenReturn(secureEditor)
        Mockito.`when`(secureEditor!!.commit()).thenReturn(true)
        secureSharedPref = Mockito.mock(SharedPreferences::class.java)

        assetFileManager = Mockito.mock(AssetFileManager::class.java)
        offlineRepo = OfflineRepo(secureEditor!!, secureSharedPref!!, assetFileManager!!)
    }

    @Test
    fun testPutEntry() {
        offlineRepo?.putEntry("", Movies(ArrayList()))
    }

    @Test
    fun testGetEntry() {
        Mockito.`when`(secureSharedPref?.getString(Mockito.anyString(), Mockito.any())).thenReturn("{}")
        checkNotNull(offlineRepo?.getEntry<Movies>("", Movies::class.java))
    }

    @Test
    fun testGetEntryNull() {
        Mockito.`when`(secureSharedPref?.getString(Mockito.anyString(), Mockito.any())).thenReturn(null)
        check(offlineRepo?.getEntry<Movies>("", Movies::class.java) == null)
    }

    @Test
    fun testGetObjectFromAsset() {
        check((offlineRepo?.getObjectFromAsset<Movies>("",Movies::class.java))==null)
    }

}