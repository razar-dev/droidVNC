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
import app.razar.rfbcore.render.Render
import io.ktor.utils.io.*

/**
 * A client which requests the LastRect pseudo-encoding is declaring
 * that it does not need the exact number of rectangles in a FramebufferUpdate message.
 * Instead, it will stop parsing when it reaches a LastRect rectangle.
 * A server may thus start transmitting the FramebufferUpdate message
 * before it knows exactly how many rectangles it is going to transmit,
 * and the server typically advertises this situation by saying that
 * it is going to send 65535 rectangles, but it then stops with a LastRect
 * instead of sending all of them. There is no further data associated
 * with the pseudo-rectangle.
 */
class LastRectEncoding: Encoding {
    override val encodingId: Int
        get() = -224

    override suspend fun decode(
        render: Render,
        dataInputStream: ByteReadChannel,
        width: Int,
        height: Int,
        xPosition: Int,
        yPosition: Int
    ) {
        //no payload
    }
}