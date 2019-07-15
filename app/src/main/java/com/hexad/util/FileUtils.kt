package com.hexad.util

import java.io.EOFException
import java.io.IOException
import java.io.InputStream
import java.util.*

object FileUtils {
    private val TAG = FileUtils::class.java.simpleName
    @Throws(IOException::class)
    fun readInputStreamAsByteArray(inputStream: InputStream?, length: Int?, readAll: Boolean?): ByteArray {
        var length = length
        var output = byteArrayOf()
        if (length == -1) length = Integer.MAX_VALUE
        var pos = 0
        while (pos < length!!) {
            val bytesToRead: Int
            if (pos >= output.size) { // Only expand when there's no room
                bytesToRead = Math.min(length - pos, output.size + 1024)
                if (output.size < pos + bytesToRead) {
                    output = Arrays.copyOf(output, pos + bytesToRead)
                }
            } else {
                bytesToRead = output.size - pos
            }
            val cc = inputStream?.read(output, pos, bytesToRead)
            if (cc != null) {
                if (cc < 0) {
                    if (readAll!! && length != Integer.MAX_VALUE) {
                        throw EOFException("$TAG Detect premature EOF")
                    } else {
                        if (output.size != pos) {
                            output = Arrays.copyOf(output, pos)
                        }
                        break
                    }
                }
            }
            pos += cc!!
        }
        return output
    }

}
