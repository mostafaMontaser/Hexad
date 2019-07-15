package com.hexad.repo

import android.content.Context
import android.content.res.AssetManager
import com.hexad.model.Movie
import com.hexad.model.Movies
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.mockito.BDDMockito
import org.mockito.Mockito
import java.io.IOException
import java.io.InputStream

class AssetFileManagerTest {

    @get:Rule
    var expectedEx = ExpectedException.none()

    @Before
    fun init() {


    }

    @Test
    fun testEmptyGetJsonObjectFromFile() {
        val context: Context = Mockito.mock(Context::class.java)
        val inputStream: InputStream = Mockito.mock(InputStream::class.java)
        val assetManager: AssetManager = Mockito.mock(AssetManager::class.java)
        val assetFileManager = AssetFileManager(context)
        Mockito.`when`(context.assets).thenReturn(assetManager)
        Mockito.`when`(assetManager.open("test.json")).thenReturn(inputStream)
        check(assetFileManager.getJsonObjectFromFile<Movies>("test", Movie::class.java) == null)
    }

    @Test
    fun testIOExceptionGetJsonObjectFromFile() {
        val context: Context = Mockito.mock(Context::class.java)
        val assetManager: AssetManager = Mockito.mock(AssetManager::class.java)
        Mockito.`when`(context.assets).thenReturn(assetManager)
        BDDMockito.willThrow( IOException()).given(assetManager).open("test.json");
        val assetFileManager: AssetFileManager? = AssetFileManager(context)
        assetFileManager?.getJsonObjectFromFile<Movies>("test", Movie::class.java)
    }
    @Test
    fun testExceptionGetJsonObjectFromFile() {
        val context: Context = Mockito.mock(Context::class.java)
        val assetManager: AssetManager = Mockito.mock(AssetManager::class.java)
        Mockito.`when`(context.assets).thenReturn(assetManager)
        BDDMockito.willThrow( NullPointerException()).given(assetManager).open("test.json");
        val assetFileManager: AssetFileManager? = AssetFileManager(context)
        assetFileManager?.getJsonObjectFromFile<Movies>("test", Movie::class.java)
    }

}
