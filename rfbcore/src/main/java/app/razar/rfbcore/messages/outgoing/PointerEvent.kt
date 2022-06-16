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
 *  PointerEvent
 *
 *  Indicates either pointer movement or a pointer button press or release. The pointer is now
 *  at (x-position, y-position), and the current state of buttons 1 to 8 are represented
 *  by bits 0 to 7 of button-mask respectively, 0 meaning up, 1 meaning down (pressed).
 *
 *  On a conventional mouse, buttons 1, 2 and 3 correspond to the left, middle and right buttons
 *  on the mouse. On a wheel mouse, each step of the wheel is represented by a press and release
 *  of a certain button. Button 4 means up, button 5 means down, button 6 means left and button 7 means right.
 *  No. of bytes 	Type 	[Value] 	Description
 *  1 	            U8 	    5 	        message-type
 *  1 	            U8 	  	            button-mask
 *  2 	            U16 	  	        x-position
 *  2 	            U16 	  	        y-position
 *
 *  The QEMU Pointer Motion Change Psuedo-encoding allows for the negotiation of an alternative
 *  interpretation for the x-position and y-position fields, as relative deltas.
 */
class PointerEvent(
    private val x: Int,
    private val y: Int,
    private val buttonMask: Short,
) : OutgoingMessage {
    override suspend fun sendMessage(outputStream: ByteWriteChannel) {
        val byteArray = ByteArray(1 + 1 + 2 + 2)

        // Set the message type
        byteArray[0] = this.messageId.toByte()
        byteArray[1] = buttonMask.toByte()

        val xB = x.toUInt16()
        val yB = y.toUInt16()

        System.arraycopy(xB, 0, byteArray, 2, 2)
        System.arraycopy(yB, 0, byteArray, 4, 2)
        outputStream.writeFully(byteArray)
    }

    override val messageId: Int = 5
}