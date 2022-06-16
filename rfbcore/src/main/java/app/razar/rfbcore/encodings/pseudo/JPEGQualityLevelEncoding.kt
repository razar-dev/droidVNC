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

package app.razar.rfbcore.encodings.pseudo

import app.razar.rfbcore.encodings.Encoding
import app.razar.rfbcore.other.JPEGQualityLevel
import app.razar.rfbcore.render.Render
import io.ktor.utils.io.*

/**
 * Specifies the desired quality from the JPEG encoder. Encoding number -23 implies high JPEG quality
 * and -32 implies low JPEG quality. Low quality can be useful in low bandwidth situations.
 * If the JPEG quality level is not specified, JpegCompression is not used in the Tight Encoding.
 *
 * The quality level concerns lossy compression and hence the setting is a tradeoff between image
 * quality and bandwidth. The specification defines neither what bandwidth is required at a certain
 * quality level nor what image quality you can expect. The quality level is also just a hint to the server.
 */
class JPEGQualityLevelEncoding(quality: JPEGQualityLevel): Encoding {
    override val encodingId: Int = quality.level

    override suspend fun decode(
        render: Render,
        dataInputStream: ByteReadChannel,
        width: Int,
        height: Int,
        xPosition: Int,
        yPosition: Int
    ) {
        //empty
    }
}