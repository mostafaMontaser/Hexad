package com.hexad

import org.mockito.Mockito

open class BaseTest {

    /**
     * use this always because there is a problem in kotlin with mockito cuz of null safety of kotlin
     * the Mockito.any() --> returns null when called and thus there is illegal state exception thrown because of the method
     * expect that its params must not be null ! its not optional we use this instead of modifying source code to make params optional
     */
    protected fun <T> any(): T {
        Mockito.any<T>()
        return uninitialized()
    }
    private fun <T> uninitialized(): T = null as T
}