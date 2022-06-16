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

package app.razar.rfbcore.messages

/**
 * There are three stages to the protocol.  First is the
 * handshaking phase, the purpose of which is to agree upon the protocol
 * version and the type of security to be used.  The second stage is an
 * initialization phase where the client and server exchange ClientInit
 * and ServerInit messages.  The final stage is the normal protocol
 * interaction.  The client can send whichever messages it wants, and
 * may receive messages from the server as a result.  All these messages
 * begin with a message-type byte, followed by message-specific data.
 *
 * RFC 6413 Section 7
 * https://datatracker.ietf.org/doc/html/rfc6143#section-7
 */
interface Messages {
    val messageId: Int
}