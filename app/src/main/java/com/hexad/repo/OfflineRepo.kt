package com.hexad.repo

import android.content.SharedPreferences
import com.tsse.vfuk.model.extensions.convertToJsonString
import com.tsse.vfuk.model.extensions.convertToModel
import java.lang.reflect.Type
import javax.inject.Inject

open class OfflineRepo @Inject constructor (private var secureEditor: SharedPreferences.Editor?,private var secureSharedPref: SharedPreferences?,private var assetFileManager: AssetFileManager?){


    fun  putEntry(key: String, data: Any) {
        secureEditor?.putString(key, data.convertToJsonString())?.commit()
    }

    fun <T> getEntry(key: String, type: Type?): T? {
        val serializedCacheEntry: String? = secureSharedPref?.getString(key, null)
        if (serializedCacheEntry != null) {
            val cacheEntry: T = serializedCacheEntry.convertToModel(type!!)
            return cacheEntry
        }
        return null
    }

    fun  <T> getObjectFromAsset(fileName:String, tClass:Type):T?{
        return assetFileManager?.getJsonObjectFromFile<T>(fileName, tClass)
    }
}