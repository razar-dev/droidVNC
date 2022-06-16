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

package app.razar.rfbcore

import android.content.Context
import android.graphics.Bitmap
import android.util.Size
import app.razar.rfbcore.encodings.EncodingType
import app.razar.rfbcore.handshake.auth.SecurityType
import app.razar.rfbcore.internal.Settings
import app.razar.rfbcore.log.VNCLogger
import app.razar.rfbcore.other.State
import app.razar.rfbcore.other.perfomance.NetworkSpeed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface VNC {

    /**
     * Connect to server
     */
    fun start(host: String, port: Int, authenticationInfo: SecurityType)

    /**
     * Disconnect from server, close socket.
     */
    fun stop()

    /**
     * Mouse move
     */
    fun onPointerEvent(x: Float, y: Float, leftButtonPressed: Boolean)

    /**
     * Send click
     */
    fun onLeftClick(x: Int, y: Int)

    /**
     * Scroll gesture up/down
     */
    fun onSwapDown(x: Int, y: Int)
    fun onSwapUp(x: Int, y: Int)

    /**
     * Mouse right click
     */
    fun onRightClick(x: Int, y: Int)

    fun onScreenUpdateA(bitmap: (Bitmap) -> Unit)

    /**
     * Todo, added custom encoding
     */
    fun addFrameEncoding()

    /**
     * Keyboard
     */
    fun onKeyTap(code: Int)

    /**
     * Returns a flow with new screen size
     */
    fun onResize(): StateFlow<Size>

    /**
     * Returns a flow with information about frame rate count
     */
    fun fpsCount(): StateFlow<Int>

    /**
     * Return a flow with server state
     * see [State], for more information
     */
    fun serverState(): StateFlow<State>

    fun lastFrameEncoding(): StateFlow<EncodingType>

    /**
     * Returns a flow with information about the connection speed to the server
     */
    fun networkSpeed(): Flow<NetworkSpeed>

    fun settings(): Settings

    class Builder(private val context: Context) {
        private var logger = VNCLogger()

        /**
         * Create VNC instance
         */
        fun build(): VNC =
            VNCImpl(context = context, logger = logger)
    }
}