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

package app.razar.rfbcore.encodings

import app.razar.rfbcore.render.Render
import io.ktor.utils.io.*

/**
 * Encodings
 *
 * Number 	        Name
 * 0 	            Raw Encoding
 * 1 	            CopyRect Encoding
 * 2 	            RRE Encoding
 * 4 	            CoRRE Encoding
 * 5 	            Hextile Encoding
 * 6 	            zlib Encoding
 * 7 	            Tight Encoding
 * 8 	            zlibhex Encoding
 * 16 	            ZRLE Encoding
 * -23 to -32 	    JPEG Quality Level Pseudo-encoding
 * -223 	        DesktopSize Pseudo-encoding
 * -224 	        LastRect Pseudo-encoding
 * -239 	        Cursor Pseudo-encoding
 * -240 	        X Cursor Pseudo-encoding
 * -247 to -256 	Compression Level Pseudo-encoding
 * -257 	        QEMU Pointer Motion Change Psuedo-encoding
 * -258 	        QEMU Extended Key Event Psuedo-encoding
 * -259 	        QEMU Audio Psuedo-encoding
 * -305 	        gii Pseudo-encoding
 * -307 	        DesktopName Pseudo-encoding
 * -308 	        ExtendedDesktopSize Pseudo-encoding
 * -309 	        xvp Pseudo-encoding
 * -312 	        Fence Pseudo-encoding
 * -313 	        ContinuousUpdates Pseudo-encoding
 * -412 to -512 	JPEG Fine-Grained Quality Level Pseudo-encoding
 * -763 to -768 	JPEG Subsampling Level Pseudo-encoding
 */
interface Encoding {
    val encodingId: Int
    suspend fun decode(
        render: Render,
        dataInputStream: ByteReadChannel,
        width: Int,
        height: Int,
        xPosition: Int,
        yPosition: Int,
    )
}