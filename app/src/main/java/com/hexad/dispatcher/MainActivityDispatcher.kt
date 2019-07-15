package com.hexad.dispatcher

import com.hexad.model.Movies
import com.hexad.repo.OfflineRepo
import com.hexad.util.Keys
import javax.inject.Inject

open class MainActivityDispatcher @Inject
constructor(private val offlineRepo: OfflineRepo?) {

    fun <M> loadObjectFromAssetAndSaveToCache(fileName: String, key: String, tClass: Class<M>?) {
        var model: M? = offlineRepo?.getEntry<M>(key, Movies::class.java)
        if (model == null) {
            model = offlineRepo?.getObjectFromAsset(fileName, tClass!!)
            if (model != null) {
                offlineRepo?.putEntry(key, model)
            }
        }
    }

    fun getMovies(): Movies? {
        return offlineRepo?.getEntry<Movies>(Keys.MOVIES, Movies::class.java)
    }

    fun setMovies(key: String, model: Movies) {
        offlineRepo?.putEntry(key, model)
    }
}