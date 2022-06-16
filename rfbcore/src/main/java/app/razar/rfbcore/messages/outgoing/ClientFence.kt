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

import io.ktor.utils.io.*

/**
 * A client supporting the Fence extension sends this to request a synchronisation of the data stream.
 * No. of bytes 	Type 	[Value] 	Description
 * 1 	            U8 	    248 	    message-type
 * 3 	  	  	                        padding
 * 4 	            U32 	  	        flags
 * 1 	            U8 	  	            length
 * length 	        U8 array 	  	    payload
 *
 * The flags byte informs the server if this is a new request, or a response to a server request sent earlier,
 * as well as what kind of synchronisation that is desired. The server should not delay the response
 * more than necessary, even if the synchronisation requirements would allow it.
 * Bit 	    Description
 * 0 	        BlockBefore
 * 1 	        BlockAfter
 * 2 	        SyncNext
 * 3-30 	    Currently unused
 * 31 	        Request
 *
 * The server should respond with a ServerFence with the Request bit cleared, as well as clearing any
 * bits it does not understand. The remaining bits should remain set in the response. This allows the
 * client to determine which flags the server supports when new ones are defined in the future.
 *
 * BlockBefore
 * All messages preceding this one must have finished processing and taken effect before the response is sent.
 * Messages following this one are unaffected and may be processed in any order the protocol permits,
 * even before the response is sent.
 *
 * BlockAfter
 * All messages following this one must not start processing until the response is sent.
 * Messages preceding this one are unaffected and may be processed in any order the protocol permits,
 * even being delayed until after the response is sent.
 *
 * SyncNext
 * The message following this one must be executed in an atomic manner so that anything preceding the
 * fence response must not be affected by the message, and anything following the fence response must
 * be affected by the message.
 *
 * Anything unaffected by the following message can be sent at any time the protocol permits.
 *
 * The primary purpose of this synchronisation is to allow safe usage of stream altering commands such
 * as SetPixelFormat, which would impose strict ordering on FramebufferUpdate messages even with
 * asynchrounous extensions such as the ContinuousUpdates Pseudo-encoding.
 *
 * If BlockAfter is also set then the interaction between the two flags can be ambiguous.
 * In this case we relax the requirement for BlockAfter and allow the following message
 * (the one made atomic by SyncNext) to be processed before a response is sent.
 * All messages after that first one are still subjected to the semantics of BlockAfter however.
 * The behaviour will be similar to the following series of messages:
 *
 * ClientFence with SyncNext
 * message made atomic
 * ClientFence with BlockAfter
 *
 * Request
 * Indicates that this is a new request and that a response is expected.
 * If this bit is cleared then this message is a response to an earlier request.
 *
 * The client can also include a chunk of data to differentiate between responses and to avoid keeping state.
 * This data is specified using length and payload. The size of this data is limited to 64 bytes in order
 * to minimise the disturbance to highly parallel clients and servers.
 */
class ClientFence(
    private val flags: Long,
    private val length: Int,
    private val payload: ByteArray,
) : OutgoingMessage {
    override suspend fun sendMessage(outputStream: ByteWriteChannel) {
        outputStream.writeByte(this.messageId)
        outputStream.writeFully(byteArrayOf(0, 0, 0))
        outputStream.writeInt(this.flags.toInt())
        outputStream.writeByte(this.length)
        outputStream.writeFully(payload)
    }

    override val messageId: Int
        get() = 248
}