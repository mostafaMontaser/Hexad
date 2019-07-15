package com.hexad.repo

import android.content.Context
import android.text.TextUtils
import com.hexad.util.FileUtils
import com.hexad.util.ParsingHelper
import java.io.IOException
import java.lang.reflect.Type
import java.nio.charset.Charset
import javax.inject.Inject

open class AssetFileManager @Inject constructor(private var context: Context?) {

    fun <T> getJsonObjectFromFile(fileName: String, classOfT: Type?): T? {
        try {
            val jsonString = loadJSONFromFile(fileName)
            if (!TextUtils.isEmpty(jsonString)) {
                val gson = ParsingHelper.getGson()
                return gson?.fromJson(jsonString, classOfT)
            }
        } catch (ex: Exception) {
            return null
        }
        return null
    }

    fun loadJSONFromFile(fileName: String): String? {
        var json: String? = null
        try {
            val inputStream = context?.assets?.open(String.format("%s.json", fileName))
            if(inputStream!=null) {
                val feed = FileUtils.readInputStreamAsByteArray(inputStream, inputStream.available(), true)
                inputStream.close()
                json = String(feed, Charset.forName("UTF-8"))
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return json
    }
}
