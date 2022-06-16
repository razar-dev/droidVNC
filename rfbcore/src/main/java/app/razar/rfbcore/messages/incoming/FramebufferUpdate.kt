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

package app.razar.rfbcore.messages.incoming

import app.razar.rfbcore.VNCImpl
import app.razar.rfbcore.other.exception.ProtocolException
import app.razar.rfbcore.other.readUnsignedShort

/**
 * A framebuffer update consists of a sequence of rectangles of pixel data which the client should put into its framebuffer.
 * It is sent in response to a FramebufferUpdateRequest from the client. Note that there may be an indefinite period between
 * the FramebufferUpdateRequest and the FramebufferUpdate.
 *
 * No. of bytes 	Type 	[Value] 	Description
 * 1 	            U8 	    0 	        message-type
 * 1 	              	  	            padding
 * 2 	            U16 	  	        number-of-rectangles
 *
 * This is followed by number-of-rectangles rectangles of pixel data. Each rectangle consists of:
 * No. of bytes 	Type 	Description
 * 2 	            U16 	x-position
 * 2 	            U16 	y-position
 * 2 	            U16 	width
 * 2 	            U16 	height
 * 4 	            S32 	encoding-type
 *
 * followed by the pixel data in the specified encoding. See Encodings for the format of the data for each encoding and
 * Pseudo-encodings for the meaning of pseudo-encodings.
 *
 * Note that a framebuffer update marks a transition from one valid framebuffer state to another. That means that a single
 * update handles all received FramebufferUpdateRequest up to the point where the update is sent out.
 *
 * However, because there is no strong connection between a FramebufferUpdateRequest and a subsequent FramebufferUpdate,
 * a client that has more than one FramebufferUpdateRequest pending at any given time cannot be sure that it has
 * received all framebuffer updates.
 *
 * See the LastRect Pseudo-encoding for an extension to this message.
 */
class FramebufferUpdate : IncomingMessage {
    override suspend fun onMessageReceived(vnc: VNCImpl) {
        val dataInputStream = vnc.dataInputStream

        // Skip padding
        dataInputStream.discard(1)

        // Read the number of rectangles contained in the frame buffer update
        val numberOfRectangles = vnc.dataInputStream.readUnsignedShort()
        // Repeat numberOfRectangles times
        repeat(numberOfRectangles) {
            // Read the x and y position of the current rectangle
            val xPosition = dataInputStream.readUnsignedShort()
            val yPosition = dataInputStream.readUnsignedShort()

            // Read the width and height of the current rectangle
            val width = dataInputStream.readUnsignedShort()
            val height = dataInputStream.readUnsignedShort()

            val encodingId = dataInputStream.readInt()
            val frameEncoding = vnc.registeredEncodings[encodingId] ?: throw ProtocolException(
                "Received an unsupported encoding type ($encodingId)"
            )

            vnc.setLastFrameEncoding(frameEncoding.encodingId)

            frameEncoding.decode(
                render = vnc.render,
                dataInputStream = dataInputStream,
                width = width,
                height = height,
                xPosition = xPosition,
                yPosition = yPosition
            )

        }
    }

    override val messageId: Int = 0
}