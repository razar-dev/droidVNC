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

import app.razar.rfbcore.messages.Messages
import io.ktor.utils.io.*

/**
 * Client to Server Messages
 *
 * The client to server message types that all servers must support are:
 * Number 	Name
 * 0 	    [SetPixelFormat]
 * 2 	    [SetEncodings]
 * 3 	    [FramebufferUpdateRequest]
 * 4 	    [KeyEvent]
 * 5 	    [PointerEvent]
 * 6 	    [ClientCutText]
 *
 * Optional:
 * Number 	Name
 * 7 	    FileTransfer
 * 8 	    SetScale
 * 9 	    SetServerInput
 * 10 	    SetSW
 * 11 	    TextChat
 * 12 	    KeyFrameRequest
 * 13 	    KeepAlive
 * 14 	    Possibly used in UltraVNC
 * 15 	    SetScaleFactor
 * 16-19 	Possibly used in UltraVNC
 * 20 	    RequestSession
 * 21 	    SetSession
 * 80 	    NotifyPluginStreaming
 * 127  	VMWare
 * 128 	    Car Connectivity
 * 150 	    EnableContinuousUpdates
 * 248 	    [ClientFence]
 * 249 	    OLIVE Call Control
 * 250 	    xvp Client Message
 * 251 	    SetDesktopSize
 * 252 	    tight
 * 253 	    gii Client Message
 * 254 	    VMWare
 * 255 	    QEMU Client Message
 */
interface OutgoingMessage: Messages {
    suspend fun sendMessage(outputStream: ByteWriteChannel)
}