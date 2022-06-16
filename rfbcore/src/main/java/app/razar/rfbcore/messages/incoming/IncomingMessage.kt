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
import app.razar.rfbcore.messages.Messages

/**
 *  Server to Client Messages
 *  The server to client message types that all clients must support are:
 *  Number 	Name
 *  0 	    FramebufferUpdate
 *  1 	    SetColourMapEntries
 *  2 	    Bell
 *  3 	    ServerCutText
 *
 *  Optional:
 *  Number 	Name
 *  4 	    ResizeFrameBuffer
 *  5 	    KeyFrameUpdate
 *  6 	    Possibly used in UltraVNC
 *  7 	    FileTransfer
 *  8-10 	Possibly used in UltraVNC
 *  11 	    TextChat
 *  12 	    Possibly used in UltraVNC
 *  13 	    KeepAlive
 *  14 	    Possibly used in UltraVNC
 *  15 	    ResizeFrameBuffer
 *  127 	VMWare
 *  128 	Car Connectivity
 *  150 	EndOfContinuousUpdates
 *  173 	ServerState
 *  248 	ServerFence
 *  249 	OLIVE Call Control
 *  250 	xvp Server Message
 *  252 	tight
 *  253 	gii Server Message
 *  254 	VMWare
 *  255 	QEMU Server Message
 */
interface IncomingMessage: Messages {
    suspend fun onMessageReceived(vnc: VNCImpl)
}