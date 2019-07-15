package com.hexad.util

import com.google.gson.*
import java.lang.reflect.Type

class ParsingHelper {
    companion object {
        private var gson: Gson? = null
        fun getGson(): Gson? {
            if (gson == null) {
                val gsonBuilder = GsonBuilder()
                gsonBuilder.registerTypeAdapter(CharSequence::class.java, CharSequenceDeserializer())
                gson = gsonBuilder.create()
            }
            return gson
        }

        internal class CharSequenceDeserializer : JsonDeserializer<CharSequence> {
            @Throws(JsonParseException::class)
            override fun deserialize(
                element: JsonElement, type: Type,
                context: JsonDeserializationContext
            ): CharSequence {
                return element.asString
            }
        }
    }
}