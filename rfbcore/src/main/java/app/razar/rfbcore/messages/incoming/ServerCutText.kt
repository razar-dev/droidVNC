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
import app.razar.rfbcore.other.readUInt32
import io.ktor.utils.io.*
import java.nio.charset.StandardCharsets

/**
 * The server has new ISO 8859-1 (Latin-1) text in its cut buffer. Ends of lines are represented by the
 * linefeed / newline character (value 10) alone. No carriage-return (value 13) is needed. There is
 * currently no way to transfer text outside the Latin-1 character set.
 * No. of bytes 	Type 	[Value] 	Description
 * 1 	            U8 	    3 	        message-type
 * 3 	  	  	                        padding
 * 4 	            U32 	  	        length
 * length 	        U8 array 	  	    text
 */
class ServerCutText: IncomingMessage {
    override suspend fun onMessageReceived(vnc: VNCImpl) {
        vnc.dataInputStream.discard(3)
        val length = vnc.dataInputStream.readUInt32().toInt()
        val payload = ByteArray(length)

        vnc.dataInputStream.readFully(payload)
        val str = String(payload, StandardCharsets.UTF_8)
        vnc.logger.d("ServerCutText: $str")
    }

    override val messageId: Int = 3
}