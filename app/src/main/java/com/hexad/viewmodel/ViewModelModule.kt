package com.hexad.viewmodel

import android.content.Context
import android.content.SharedPreferences
import com.hexad.HexadApplication
import com.hexad.model.SecureSharedModule
import dagger.Module
import dagger.Provides

@Module
class ViewModelModule{
    @Provides
    fun provideSecurePref(): SharedPreferences {
        return SecureSharedModule(HexadApplication.getAppContext()).getSecureSharedPreferences()
    }
    @Provides
    fun provideSecurePrefEditor(): SharedPreferences.Editor {
        return SecureSharedModule(HexadApplication.getAppContext()).getSecureSharedPreferencesEditor()
    }
    @Provides
    fun provideContext(): Context? {
        return HexadApplication.getAppContext()
    }
}
