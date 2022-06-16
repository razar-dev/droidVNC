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
import app.razar.rfbcore.other.readUnsignedShort

/**
 * SetColourMapEntries

 * When the pixel format uses a “colour map”, this message tells the client
 * that the specified pixel values should be mapped to the given RGB intensities.
 * No. of bytes 	Type 	[Value] 	Description
 * 1 	            U8 	    1 	        message-type
 * 1 	  	  	                        padding
 * 2 	            U16 	  	        first-colour
 * 2 	            U16 	  	        number-of-colours
 *
 * followed by number-of-colours repetitions of the following:
 * No. of bytes 	Type 	Description
 * 2 	            U16 	red
 * 2 	            U16 	green
 * 2 	            U16 	blue
 */
class SetColourMapEntries: IncomingMessage {
    override suspend fun onMessageReceived(vnc: VNCImpl) {
        vnc.dataInputStream.discard(1) //padding
        val firstColor = vnc.dataInputStream.readUnsignedShort()
        val colorNum = vnc.dataInputStream.readUnsignedShort()
        repeat (colorNum) {
            vnc.dataInputStream.readUnsignedShort() // R
            vnc.dataInputStream.readUnsignedShort() // G
            vnc.dataInputStream.readUnsignedShort() // B
        }
    }

    override val messageId: Int = 1
}