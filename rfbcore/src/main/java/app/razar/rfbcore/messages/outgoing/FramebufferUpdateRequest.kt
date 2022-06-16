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

import app.razar.rfbcore.other.toUInt16
import io.ktor.utils.io.*

/**
 * FramebufferUpdateRequest
 *
 * Notifies the server that the client is interested in the area of the framebuffer
 * specified by x-position, y-position, width and height. The server usually responds
 * to a FramebufferUpdateRequest by sending a FramebufferUpdate. Note however that
 * a single FramebufferUpdate may be sent in reply to several FramebufferUpdateRequests.

 * The server assumes that the client keeps a copy of all parts of the framebuffer in
 * which it is interested. This means that normally the server only needs to send
 * incremental updates to the client.
 *
 * However, if for some reason the client has lost the contents of a particular area
 * which it needs, then the client sends a FramebufferUpdateRequest with incremental
 * set to zero (false). This requests that the server send the entire contents of the
 * specified area as soon as possible. The area will not be updated using the CopyRect
 * encoding.
 *
 * If the client has not lost any contents of the area in which it is interested,
 * then it sends a FramebufferUpdateRequest with incremental set to non-zero (true).
 * If and when there are changes to the specified area of the framebuffer, the server
 * will send a FramebufferUpdate. Note that there may be an indefinite period between
 * the FramebufferUpdateRequest and the FramebufferUpdate.
 *
 * In the case of a fast client, the client may want to regulate the rate at which
 * it sends incremental FramebufferUpdateRequests to avoid hogging the network.
 * No. of bytes 	Type 	[Value] 	Description
 * 1 	            U8 	    3 	        message-type
 * 1 	            U8 	  	            incremental
 * 2 	            U16 	  	        x-position
 * 2 	            U16 	  	        y-position
 * 2 	            U16 	  	        width
 * 2 	            U16 	  	        height
 *
 * A request for an area that partly falls outside the current framebuffer must be cropped
 * so that it fits within the framebuffer dimensions.
 *
 * Note that an empty area can still solicit a FramebufferUpdate even though that update
 * will only contain pseudo-encodings.
 */
class FramebufferUpdateRequest(
    private val incremental: Boolean,
    private val xPosition: Int,
    private val yPosition: Int,
    private val width: Int,
    private val height: Int
): OutgoingMessage {

    override val messageId: Int = 3

    override suspend fun sendMessage(outputStream: ByteWriteChannel) {
        val fbuEncodingMessage = ByteArray(1 + 1 + 2 + 2 + 2 + 2)

        fbuEncodingMessage[0] = this.messageId.toByte()

        when (incremental) {
            true -> fbuEncodingMessage[1] = 1
            false -> fbuEncodingMessage[1] = 0
        }

        val x = xPosition.toUInt16()
        val y = yPosition.toUInt16()
        val w = width.toUInt16()
        val h = height.toUInt16()

        System.arraycopy(x, 0, fbuEncodingMessage, 2, 2)
        System.arraycopy(y, 0, fbuEncodingMessage, 4, 2)
        System.arraycopy(w, 0, fbuEncodingMessage, 6, 2)
        System.arraycopy(h, 0, fbuEncodingMessage, 8, 2)

        outputStream.writeFully(fbuEncodingMessage)
    }
}