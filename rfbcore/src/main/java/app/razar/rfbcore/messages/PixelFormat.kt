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

package app.razar.rfbcore.messages

import io.ktor.utils.io.*

class PixelFormat(
    var bitsPerPixel: Int,
    var depth: Int,
    var bigEndianFlag: Int,
    var trueColorFlag: Int,
    var redMax: Int,
    var greenMax: Int,
    var blueMax: Int,
    var redShift: Int,
    var greenShift: Int,
    var blueShift: Int
) {
    /**
     * Generate PixelFormat from DataInputStream
     */
    companion object {
        suspend fun fromBuffer(extendedDataInputStream: ByteReadChannel): PixelFormat {

            // Read the 16 bytes needed
            val pixelFormatArray = ByteArray(16)
            extendedDataInputStream.readFully(pixelFormatArray)

            // Return a new PixelFormat object
            return PixelFormat(
                bitsPerPixel = 0xFF and pixelFormatArray[0].toInt(),
                depth = 0xFF and pixelFormatArray[1].toInt(),
                bigEndianFlag = 0xFF and pixelFormatArray[2].toInt(),
                trueColorFlag = 0xFF and pixelFormatArray[3].toInt(),
                redMax = 0xFF and (pixelFormatArray[4].toInt() shl 8) or (0xFF and pixelFormatArray[5].toInt()),
                greenMax = 0xFF and (pixelFormatArray[6].toInt() shl 8) or (0xFF and pixelFormatArray[7].toInt()),
                blueMax = 0xFF and (pixelFormatArray[8].toInt() shl 8) or (0xFF and pixelFormatArray[9].toInt()),
                redShift = 0xFF and pixelFormatArray[10].toInt(),
                greenShift = 0xFF and pixelFormatArray[11].toInt(),
                blueShift = 0xFF and pixelFormatArray[12].toInt()
            )
        }
    }

    fun toByteArray(): ByteArray {
        return byteArrayOf(
            bitsPerPixel.toByte(),
            depth.toByte(),
            bigEndianFlag.toByte(),
            trueColorFlag.toByte(),
            (redMax shr 8 and 0xFF).toByte(),
            (redMax and 0xFF).toByte(),
            (greenMax shr 8 and 0xFF).toByte(),
            (greenMax and 0xFF).toByte(),
            (blueMax shr 8 and 0xFF).toByte(),
            (blueMax and 0xFF).toByte(),
            redShift.toByte(),
            greenShift.toByte(),
            blueShift.toByte(), 0 ,0 ,0
        )
    }
}