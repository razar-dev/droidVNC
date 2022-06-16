/*
 * This file is part of the droidVNC distribution (https://github.com/razar-dev/VNC-android).
 * Copyright © 2022 Sachindra Man Maskey.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package app.razar.rfbcore.other

import android.content.Context
import app.razar.rfbcore.R
import app.razar.rfbcore.encodings.Encoding
import app.razar.rfbcore.messages.incoming.IncomingMessage
import io.ktor.utils.io.*
import java.io.DataInputStream

suspend fun ByteReadChannel.readFullyUnsignedBytes(unsignedBytes: IntArray) {
    val signedBytes = ByteArray(unsignedBytes.size)
    this.readFully(signedBytes)
    for (i in signedBytes.indices) {
        unsignedBytes[i] = 0xFF and signedBytes[i].toInt()
    }
}

suspend fun ByteReadChannel.readUnsignedByte(): Int {
    return readByte().toInt() and 0xFF//readByte().and(0xFF.toByte()).toInt()
}
suspend fun ByteReadChannel.readUnsignedShort(): Int {
    return readShort().toInt()
}

fun Int.toUInt16(): ByteArray {
    return byteArrayOf((this shr 8 and 0xFF).toByte(), (this and 0xFF).toByte())
}

fun MutableMap<Int, Encoding>.addEncoding(encoding: Encoding) {
    this[encoding.encodingId] = encoding
}

fun MutableMap<Int, IncomingMessage>.addMessage(message: IncomingMessage) {
    this[message.messageId] = message
}


fun DataInputStream.readUInt32(): Long {
    return 0xFFFFFFFFL and readInt().toLong()
}

suspend fun ByteReadChannel.readUInt32(): Long {
    return 0xFFFFFFFFL and readInt().toLong()
}

fun Int.toSInt32Array(): ByteArray {
    return byteArrayOf((this shr 24).toByte(), (this shr 16).toByte(), (this shr 8).toByte(), this.toByte())
}

suspend fun ByteReadChannel.readCompactLength(): Int {
    val portion = IntArray(3)
    portion[0] = readUnsignedByte()
    var len = portion[0] and 0x7F
    if (portion[0] and 0x80 != 0) {
        portion[1] = readUnsignedByte()
        len = len or (portion[1] and 0x7F shl 7)
        if (portion[1] and 0x80 != 0) {
            portion[2] = readUnsignedByte()
            len = len or (portion[2] and 0xFF shl 14)
        }
    }
    return len
}

fun DataInputStream.readCompactLength(): Int {
    val portion = IntArray(3)
    portion[0] = readUnsignedByte()
    var len = portion[0] and 0x7F
    if (portion[0] and 0x80 != 0) {
        portion[1] = readUnsignedByte()
        len = len or (portion[1] and 0x7F shl 7)
        if (portion[1] and 0x80 != 0) {
            portion[2] = readUnsignedByte()
            len = len or (portion[2] and 0xFF shl 14)
        }
    }
    return len
}

fun Context.getSpeedText(int: Long): String {
    val kb = 1024
    val mb = kb * kb
    return when (int) {
        in 0 until kb -> {
            getString(R.string.download_speed_bytes).format(int)
        }
        in kb until mb -> {
            getString(R.string.download_speed_kb).format(int / kb.toFloat())
        }
        else -> {
            getString(R.string.download_speed_mb).format(int / mb.toFloat())
        }
    }
}