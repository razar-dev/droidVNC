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

object SharedBuffer {
    private var buffer = ByteArray(5 * 1024 * 1024)
    private var pixels = intArrayOf(1920 * 1080)

    fun getBuffer(newSize: Int): ByteArray {
        if (newSize > buffer.size) buffer = ByteArray(newSize)
        return buffer
    }

    fun getPixels(newSize: Int): IntArray {
        if (newSize > pixels.size) pixels = IntArray(newSize)
        return pixels
    }
}