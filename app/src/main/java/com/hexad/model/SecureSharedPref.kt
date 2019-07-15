package com.hexad.model

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import com.facebook.android.crypto.keychain.AndroidConceal
import com.facebook.android.crypto.keychain.SharedPrefsBackedKeyChain
import com.facebook.crypto.Crypto
import com.facebook.crypto.CryptoConfig
import com.facebook.crypto.Entity
import com.facebook.crypto.exception.CryptoInitializationException
import com.facebook.crypto.exception.KeyChainException
import com.facebook.crypto.keychain.KeyChain
import com.facebook.soloader.SoLoader
import java.io.IOException
import java.util.*

class SecureSharedPref private constructor(context: Context,
                                           keyChain: KeyChain,
                                           private val entity: Entity,
                                           sharedPrefFilename: String?) : SharedPreferences {
    private val sharedPreferences: SharedPreferences?
    private val crypto: Crypto

    class Builder(private val context: Context) {
        var password: String? = null
            private set
        var sharedPrefFileName: String? = null
            private set

        fun password(password: String): Builder {
            this.password = password
            return this
        }

        fun fileName(sharedPrefFileName: String): Builder {
            this.sharedPrefFileName = sharedPrefFileName
            return this
        }

        fun build(): SharedPreferences {
            if (!isInit) {
                Log.w(TAG, "You need call 'SecurePreferences.init()' in onCreate() from your application class.")
            }
            val keyChain = SharedPrefsBackedKeyChain(context, CryptoConfig.KEY_256)
            val entity = Entity.create(
                    if (TextUtils.isEmpty(password)) javaClass.`package`.name else password
            )
            return SecureSharedPref(
                    context,
                    keyChain,
                    entity,
                    sharedPrefFileName
            )
        }
    }

    init {
        this.sharedPreferences = getSharedPreferenceFile(context, sharedPrefFilename)
        this.crypto = AndroidConceal.get().createCrypto256Bits(keyChain)
    }

    private fun encrypt(plainText: String?): String? {
        if (TextUtils.isEmpty(plainText)) {
            return plainText
        }

        var cipherText: ByteArray? = null

        if (!crypto.isAvailable) {
            log(Log.WARN, "encrypt: crypto not available")
            return null
        }

        try {
            cipherText = crypto.encrypt(plainText!!.toByteArray(), entity)
        } catch (e: KeyChainException) {
            log(Log.ERROR, "encrypt: $e")
        } catch (e: CryptoInitializationException) {
            log(Log.ERROR, "encrypt: $e")
        } catch (e: IOException) {
            log(Log.ERROR, "encrypt: $e")
        }

        return if (cipherText != null) Base64.encodeToString(cipherText, Base64.DEFAULT) else null
    }

    private fun decrypt(encryptedText: String): String? {
        if (TextUtils.isEmpty(encryptedText)) {
            return encryptedText
        }

        var plainText: ByteArray? = null

        if (!crypto.isAvailable) {
            log(Log.WARN, "decrypt: crypto not available")
            return null
        }

        try {
            plainText = crypto.decrypt(Base64.decode(encryptedText, Base64.DEFAULT), entity)
        } catch (e: KeyChainException) {
            log(Log.ERROR, "decrypt: $e")
        } catch (e: CryptoInitializationException) {
            log(Log.ERROR, "decrypt: $e")
        } catch (e: IOException) {
            log(Log.ERROR, "decrypt: $e")
        }

        return if (plainText != null)
            String(plainText)
        else
            null
    }

    private fun getSharedPreferenceFile(context: Context, sharedPrefFilename: String?): SharedPreferences? {
        return if (TextUtils.isEmpty(sharedPrefFilename)) {
            PreferenceManager
                    .getDefaultSharedPreferences(context)
        } else {
            context.getSharedPreferences(sharedPrefFilename, Context.MODE_PRIVATE)
        }
    }

    override fun getAll(): Map<String, *>? {
        return if (sharedPreferences != null) {
            val encryptedMap = sharedPreferences.all
            val decryptedMap = HashMap<String, String?>(encryptedMap.size)
            if (encryptedMap != null) {
                for ((key, encryptedValue) in encryptedMap) {
                    if (encryptedValue != null) {
                        decryptedMap[key] = decrypt(encryptedValue.toString())
                    }
                }
            }
            decryptedMap
        } else {
            null
        }
    }

    override fun getString(key: String, defValue: String?): String? {
        val encryptedValue = sharedPreferences?.getString(key, defValue)
        return if (encryptedValue != null && encryptedValue != defValue) decrypt(encryptedValue) else defValue
    }

    override fun getStringSet(key: String, defValues: Set<String>?): Set<String>? {
        val encryptedValues = sharedPreferences?.getStringSet(key, null) ?: return defValues
        val decryptedValues = HashSet<String>(encryptedValues.size)
        for (encryptedValue in encryptedValues) {
            decryptedValues.add(encryptedValue)
        }
        return decryptedValues
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        val encryptedValue = sharedPreferences?.getString(key, null) ?: return defaultValue
        try {
            return Integer.parseInt(decrypt(encryptedValue))
        } catch (e: NumberFormatException) {
            throw ClassCastException(e.message)
        }

    }

    override fun getLong(key: String, defaultValue: Long): Long {
        val encryptedValue = sharedPreferences?.getString(key, null) ?: return defaultValue
        try {
            return java.lang.Long.parseLong(decrypt(encryptedValue))
        } catch (e: NumberFormatException) {
            throw ClassCastException(e.message)
        }

    }

    override fun getFloat(key: String, defaultValue: Float): Float {
        val encryptedValue = sharedPreferences?.getString(key, null) ?: return defaultValue
        try {
            return java.lang.Float.parseFloat(decrypt(encryptedValue))
        } catch (e: NumberFormatException) {
            throw ClassCastException(e.message)
        }

    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        val encryptedValue = sharedPreferences?.getString(key, null) ?: return defaultValue
        try {
            return java.lang.Boolean.parseBoolean(decrypt(encryptedValue))
        } catch (e: NumberFormatException) {
            throw ClassCastException(e.message)
        }

    }

    override fun contains(key: String): Boolean {
        return sharedPreferences?.contains(key) ?: false
    }

    override fun edit(): Editor {
        return Editor()
    }

    inner class Editor @SuppressLint("CommitPrefEdits")
    constructor() : SharedPreferences.Editor {

        private val mEditor: SharedPreferences.Editor? = sharedPreferences?.edit()

        override fun putString(key: String, value: String?): SharedPreferences.Editor {
            mEditor?.putString(key, encrypt(value))
            return this
        }

        override fun putStringSet(key: String,
                                  values: Set<String>?): SharedPreferences.Editor {
            val encryptedValues = HashSet<String?>(
                    values!!.size)
            for (value in values) {
                encryptedValues.add(encrypt(value))
            }
            mEditor?.putStringSet(key, encryptedValues)
            return this
        }

        override fun putInt(key: String, value: Int): SharedPreferences.Editor {
            mEditor?.putString(key, encrypt(Integer.toString(value)))
            return this
        }

        override fun putLong(key: String, value: Long): SharedPreferences.Editor {
            mEditor?.putString(key, encrypt(java.lang.Long.toString(value)))
            return this
        }

        override fun putFloat(key: String, value: Float): SharedPreferences.Editor {
            mEditor?.putString(key, encrypt(java.lang.Float.toString(value)))
            return this
        }

        override fun putBoolean(key: String, value: Boolean): SharedPreferences.Editor {
            mEditor?.putString(key, encrypt(java.lang.Boolean.toString(value)))
            return this
        }

        override fun remove(key: String): SharedPreferences.Editor {
            mEditor?.remove(key)
            return this
        }

        override fun clear(): SharedPreferences.Editor {
            mEditor?.clear()
            return this
        }

        override fun commit(): Boolean {
            return mEditor?.commit() ?: false
        }


        override fun apply() {
            mEditor?.apply()
        }
    }


    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPreferences?.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPreferences?.unregisterOnSharedPreferenceChangeListener(listener)
    }

    companion object {
        const val SHARED_PREF_NAME = "HEXAD_STORAGE"
        const val PASSWORD = "hexad"
        private val TAG = SecureSharedPref::class.java.name
        private var isInit = false

        fun init(pContext: Context) {
            SoLoader.init(pContext, false)
            isInit = true
        }

        fun log(type: Int, str: String) {
                when (type) {
                    Log.WARN -> Log.w(TAG, str)
                    Log.ERROR -> Log.e(TAG, str)
                    Log.DEBUG -> Log.d(TAG, str)
                }
            }
        }
}
