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

package app.razar.rfbcore.encodings.frame

import android.graphics.Color
import app.razar.rfbcore.encodings.Encoding
import app.razar.rfbcore.other.SharedBuffer
import app.razar.rfbcore.render.Render
import io.ktor.utils.io.*
import java.util.zip.Inflater

class ZLibEncoding : Encoding {
    override val encodingId: Int
        get() = 6

    private val inflater = Inflater()
    private var zLibDataLength: Int = 0
    private var zLibData: ByteArray = ByteArray(1)
    private var inflaterBuffer = ByteArray(1)
    private var maxUncompressedLength: Int = 0
    private var frameSize = 0
    private var pixelData: Int = 0
    private var blue: Int = 0
    private var green: Int = 0
    private var red: Int = 0
    private var blueShift: Int = 0
    private var greenShift: Int = 0
    private var redShift: Int = 0
    private var bitsPerPixel: Int = 0
    private var bytesPerPixel: Int = 0
    private var colorOffset: Int = 0
    private var currentPixelIndex = 0

    override suspend fun decode(
        render: Render,
        dataInputStream: ByteReadChannel,
        width: Int,
        height: Int,
        xPosition: Int,
        yPosition: Int
    ) {
        frameSize = width * height

        // Get frame buffer information
        val frameBufferInfo = render.getFrameBufferInfo()

        // Read the zlib data length
        zLibDataLength = dataInputStream.readInt()

        // If the length is larger than the array, initialize a new array
        if (zLibDataLength > zLibData.size) zLibData = ByteArray(zLibDataLength)

        // Read the zlib data from the input stream
        dataInputStream.readFully(zLibData, 0, zLibDataLength)
        ///test.checkPoint("READ")

        // Set input of inflater
        inflater.setInput(zLibData, 0, zLibDataLength)

        // Get the blue, green and red shift from PixelData
        blueShift = frameBufferInfo.pixelFormat.blueShift
        greenShift = frameBufferInfo.pixelFormat.greenShift
        redShift = frameBufferInfo.pixelFormat.redShift

        // Bits per pixel from the PixelFormat information
        bitsPerPixel = frameBufferInfo.pixelFormat.bitsPerPixel
        bytesPerPixel = bitsPerPixel / 8

        // Calculate the maximum size of the uncompressed data
        maxUncompressedLength = frameSize * render.getFrameBufferInfo().pixelFormat.bitsPerPixel / 8

        // If the buffer size is to small set it to the max uncompressed size
        if (inflaterBuffer.size < maxUncompressedLength) {
            //vnc.logger.d("Buffer size miss")
            inflaterBuffer = ByteArray(maxUncompressedLength)
        }

        // Inflate the zlib data
        inflater.inflate(inflaterBuffer)

        // Reset values
        pixelData = 0
        colorOffset = 0
        currentPixelIndex = 0

        // Get pixel array
        val pixels = SharedBuffer.getPixels(frameSize)

        repeat(maxUncompressedLength) {
            pixelData = pixelData or ((inflaterBuffer[it].toInt() and 0xFF) shl (colorOffset * 8))

            if (bytesPerPixel == ++colorOffset) {
                blue = pixelData shr blueShift
                green = pixelData shr greenShift
                red = pixelData shr redShift

                pixels[currentPixelIndex++] = Color.rgb(red, green, blue)

                pixelData = 0
                colorOffset = 0
            }
        }


        render.setPixels(pixels, xPosition, yPosition, width, height)
    }
}