package com.tsse.vfuk.model.extensions

import com.hexad.util.ParsingHelper
import java.lang.reflect.Type

/**
 * Created by ehab on 3/28/18.
 */

fun <T> String.convertToModel(type: Type): T {
    return ParsingHelper.getGson()!!.fromJson<T>(this, type)
}