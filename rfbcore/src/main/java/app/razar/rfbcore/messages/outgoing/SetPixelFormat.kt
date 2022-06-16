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

package app.razar.rfbcore.messages.outgoing

import app.razar.rfbcore.messages.PixelFormat
import io.ktor.utils.io.*

/**
 * SetPixelFormat
 *
 * Sets the format in which pixel values should be sent in FramebufferUpdate messages.
 * If the client does not send a SetPixelFormat message then the server sends pixel
 * values in its natural format as specified in the ServerInit message (ServerInit).

 * If true-colour-flag is zero (false) then this indicates that a “colour map” is to
 * be used. The server can set any of the entries in the colour map using the
 * SetColourMapEntries message (SetColourMapEntries). Immediately after the client
 * has sent this message the colour map is empty, even if entries had previously
 * been set by the server.
 *
 * Note that a client must not have an outstanding FramebufferUpdateRequest when it
 * sends SetPixelFormat as it would be impossible to determine if the next
 * FramebufferUpdate is using the new or the previous pixel format.
 * No. of bytes 	Type 	        [Value] 	Description
 * 1 	            U8 	            0 	        message-type
 * 3 	              	  	                    padding
 * 16 	            PIXEL_FORMAT 	  	        pixel-format
 *
 * where PIXEL_FORMAT is as described in ServerInit:
 * No. of bytes 	Type 	Description
 * 1 	            U8 	    bits-per-pixel
 * 1 	            U8 	    depth
 * 1 	            U8 	    big-endian-flag
 * 1 	            U8 	    true-colour-flag
 * 2 	            U16 	red-max
 * 2 	            U16 	green-max
 * 2 	            U16 	blue-max
 * 1 	            U8 	    red-shift
 * 1 	            U8 	    green-shift
 * 1 	            U8 	    blue-shift
 * 3 	              	    padding
 */
class SetPixelFormat(private val pixelFormat: PixelFormat): OutgoingMessage {
    override suspend fun sendMessage(outputStream: ByteWriteChannel) {
        val pixelFormatByte = pixelFormat.toByteArray()
        val pixelFormatMessage = ByteArray(1 + 3 + 16)
        pixelFormatMessage[0] = this.messageId.toByte()
        pixelFormatMessage[1] = 0
        pixelFormatMessage[2] = 0
        pixelFormatMessage[3] = 0
        System.arraycopy(
            pixelFormatByte,
            0,
            pixelFormatMessage,
            4,
            16
        )
        outputStream.writeFully(pixelFormatMessage)
    }

    override val messageId: Int = 0
}