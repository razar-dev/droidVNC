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
import app.razar.rfbcore.messages.outgoing.ClientFence
import app.razar.rfbcore.other.readUInt32
import app.razar.rfbcore.other.readUnsignedByte

/**
 * A server supporting the Fence extension sends this to request a synchronisation of the data stream.
 * No. of bytes 	Type 	[Value] 	Description
 * 1 	            U8 	    248 	    message-type
 * 3 	              	  	            padding
 * 4 	            U32 	  	        flags
 * 1 	            U8 	  	            length
 * length 	        U8 array 	  	    payload
 *
 * The format and semantics is identical to ClientFence, but with the roles of the client and server reversed.
 */
class ServerFence: IncomingMessage {
    override suspend fun onMessageReceived(vnc: VNCImpl) {
        val skipBytes = ByteArray(3)
        val inputStream = vnc.dataInputStream
        inputStream.readFully(skipBytes, 0, skipBytes.size)

        val flags = inputStream.readUInt32()
        val length = inputStream.readUnsignedByte()

        val payload = ByteArray(length)
        inputStream.readFully(payload, 0 ,payload.size)

        val clearedRequestFlag = flags and 0b00000000_00000000_00000000_00000111

        val clientFence = ClientFence(
            flags = clearedRequestFlag,
            length = length,
            payload = payload
        )
        vnc.sendMessage(clientFence)
    }

    override val messageId: Int = 248
}