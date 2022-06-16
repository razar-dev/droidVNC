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

import app.razar.rfbcore.other.toSInt32Array
import app.razar.rfbcore.other.toUInt16
import io.ktor.utils.io.*

/**
 * SetEncodings
 *
 * Sets the encoding types in which pixel data can be sent by the server.
 * The order of the encoding types given in this message is a hint by the client
 * as to its preference (the first encoding specified being most preferred).
 * The server may or may not choose to make use of this hint. Pixel data may
 * always be sent in raw encoding even if not specified explicitly here.

 * In addition to genuine encodings, a client can request “pseudo-encodings” to
 * declare to the server that it supports certain extensions to the protocol.
 * A server which does not support the extension will simply ignore the pseudo-encoding.
 * Note that this means the client must assume that the server does not support the
 * extension until it gets some extension-specific confirmation from the server.
 *
 * See Encodings for a description of each encoding and Pseudo-encodings for the
 * meaning of pseudo-encodings.
 *
 * No. of bytes 	Type 	[Value] 	Description
 * 1 	            U8 	    2 	        message-type
 * 1 	              	      	        padding
 * 2 	            U16 	  	        number-of-encodings
 *
 * followed by number-of-encodings repetitions of the following:
 * No. of bytes 	Type 	Description
 * 4 	            S32 	encoding-type
 */
class SetEncodings(private val encodings: List<Int>): OutgoingMessage {
    override suspend fun sendMessage(outputStream: ByteWriteChannel) {
        // Initialize the ByteArray to send the Encodings
        // 1B MessageType + 1B Padding + 2B Number of Encodings + 4B  for each Encoding
        val encodingMessage = ByteArray(1 + 1 + 2 + (4 * encodings.size))

        // Set the message type
        encodingMessage[0] = messageId.toByte()

        // Set the number of encodings
        val encodingCount = encodings.size.toUInt16()
        System.arraycopy(encodingCount, 0, encodingMessage, 2, 2)

        encodings.forEachIndexed { index, it ->
            System.arraycopy(
                it.toSInt32Array(),
                0,
                encodingMessage,
                4 + (4 * index),
                4
            )
        }
        outputStream.writeFully(encodingMessage)
    }

    override val messageId: Int = 2
}