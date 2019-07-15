package com.hexad

import android.app.Application
import android.content.Context
import com.hexad.model.SecureSharedPref

class HexadApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        SecureSharedPref.init(this)
        if(context==null){
            context=applicationContext
        }
    }
    companion object {
        private var context:Context?=null
        fun getAppContext(): Context? {
            return context
        }
    }


}