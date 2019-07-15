package com.hexad.viewmodel

import dagger.Component

import javax.inject.Singleton

@Singleton
@Component(modules = [ViewModelModule::class])
interface ViewModelComponent {
    fun inject(mainActivityViewModel: MainActivityViewModel)

}
