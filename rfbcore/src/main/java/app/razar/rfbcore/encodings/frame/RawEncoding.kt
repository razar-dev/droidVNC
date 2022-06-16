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

/**
 * The simplest encoding type is raw pixel data. In this case the data consists of width * height
 * pixel values (where width and height are the width and height of the rectangle).
 * The values simply represent each pixel in left-to-right scanline order.
 * All RFB clients must be able to cope with pixel data in this raw encoding,
 * and RFB servers should only produce raw encoding unless
 * the client specifically asks for some other encoding type.
 */
class RawEncoding : Encoding {
    override val encodingId: Int
        get() = 0

    private var frameSize: Int = 0
    private var bitsPerPixel: Int = 0
    private var bytesPerPixel: Int = 0
    private var pixelValue: Int = 0
    private var totalBytes: Int = 0
    private var colorOffset: Int = 0
    private var currentPixelIndex: Int = 0
    private var blue: Int = 0
    private var green: Int = 0
    private var red: Int = 0
    private var blueShift: Int = 0
    private var greenShift: Int = 0
    private var redShift: Int = 0

    override suspend fun decode(
        render: Render,
        dataInputStream: ByteReadChannel,
        width: Int,
        height: Int,
        xPosition: Int,
        yPosition: Int,
    ) {
        // Get frame buffer information
        val frameBufferInfo = render.getFrameBufferInfo()

        // Calculate frame size
        frameSize = width * height

        // Get pixel array
        val pixels = SharedBuffer.getPixels(frameSize)

        // Bits per pixel from the PixelFormat information
        bitsPerPixel = frameBufferInfo.pixelFormat.bitsPerPixel

        // Bits per pixel divided by 8 to get bytes
        bytesPerPixel = bitsPerPixel / 8

        // Reset values
        pixelValue = 0
        colorOffset = 0
        currentPixelIndex = 0

        // Get the blue, green and red shift from PixelData
        blueShift = frameBufferInfo.pixelFormat.blueShift
        greenShift = frameBufferInfo.pixelFormat.greenShift
        redShift = frameBufferInfo.pixelFormat.redShift

        // Calculate the total bytes to read
        totalBytes = frameSize * bytesPerPixel

        // Set buffer size
        val buffer = SharedBuffer.getBuffer(totalBytes)

        // Read all bytes
        dataInputStream.readFully(buffer, 0, totalBytes)

        // Read byte from buffer
        repeat(totalBytes) {
            // Add the new byte to the existing pixel data
            pixelValue = pixelValue or ((buffer[it].toInt() and 0xFF) shl (colorOffset * 8))

            if (bytesPerPixel == ++colorOffset) {
                blue = pixelValue shr blueShift
                green = pixelValue shr greenShift
                red = pixelValue shr redShift

                pixels[currentPixelIndex++] = Color.rgb(red, green, blue)

                // Reset values
                pixelValue = 0
                colorOffset = 0
            }
        }

        // Send pixels to screen
        render.setPixels(pixels, xPosition, yPosition, width, height)
    }
}