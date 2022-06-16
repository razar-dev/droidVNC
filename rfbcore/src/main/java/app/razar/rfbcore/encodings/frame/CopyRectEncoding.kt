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

import app.razar.rfbcore.encodings.Encoding
import app.razar.rfbcore.other.readUnsignedShort
import app.razar.rfbcore.render.Render
import io.ktor.utils.io.*


class CopyRectEncoding : Encoding {
    override val encodingId: Int
        get() = 1

    private var intPixel = IntArray(1)

    override suspend fun decode(
        render: Render,
        dataInputStream: ByteReadChannel,
        width: Int,
        height: Int,
        xPosition: Int,
        yPosition: Int
    ) {
        if (intPixel.size < width * height) intPixel = IntArray(width * height + 1)
        val srcX = dataInputStream.readUnsignedShort()
        val srcY = dataInputStream.readUnsignedShort()

        render.getPixels(intPixel, srcX, srcY, width, height)
        render.setPixels(intPixel, xPosition, yPosition, width, height)
    }
}