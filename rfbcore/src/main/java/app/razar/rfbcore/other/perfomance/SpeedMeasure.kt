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

package app.razar.rfbcore.other.perfomance

import android.content.Context
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SpeedMeasure {
    private var lastTotalByteRead = 0L
    private var lastTotalByteWrite = 0L

    private val _networkSpeed: MutableStateFlow<NetworkSpeed> = MutableStateFlow(NetworkSpeed.empty())

    val networkSpeed = _networkSpeed.asStateFlow()

    fun startMeasure(
        coroutineScope: CoroutineScope,
        context: Context,
        dataInputStream: ByteReadChannel,
        dataOutputStream: ByteWriteChannel,
        socket: Socket,
    ) = coroutineScope.launch {
        while (!socket.isClosed) {
            delay(1000)
            val currentRead = dataInputStream.totalBytesRead
            val currentWrite = dataOutputStream.totalBytesWritten
            val download = currentRead - lastTotalByteRead
            val upload = currentWrite - lastTotalByteWrite
            lastTotalByteRead = currentRead
            lastTotalByteWrite = currentWrite
            _networkSpeed.value = NetworkSpeed.create(context, download, upload)
        }
    }

}