package com.hexad.model

import android.content.Context
import android.content.SharedPreferences

open class SecureSharedModule constructor(context: Context?) {

    private var secureSharedPref: SecureSharedPref = SecureSharedPref.Builder(context!!)
        .fileName(SecureSharedPref.SHARED_PREF_NAME)
        .password(SecureSharedPref.PASSWORD)
        .build() as SecureSharedPref
    private var secureEditor: SecureSharedPref.Editor = secureSharedPref.edit()

    open fun getSecureSharedPreferences(): SharedPreferences {
        return secureSharedPref
    }

    open fun getSecureSharedPreferencesEditor(): SharedPreferences.Editor {
        return secureEditor
    }
}