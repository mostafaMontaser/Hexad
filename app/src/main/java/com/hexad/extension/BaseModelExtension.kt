package com.tsse.vfuk.model.extensions

import com.hexad.util.ParsingHelper

/**
 * Created by ehab on 3/28/18.
 */

fun Any.convertToJsonString(): String {
    return ParsingHelper.getGson()!!.toJson(this)
}