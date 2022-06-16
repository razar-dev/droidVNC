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

import io.ktor.network.sockets.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicLong

class FPSCounter {
    private val ticker = AtomicLong(0)
    private val tickerCount = AtomicLong(0)

    private val _fps = MutableStateFlow(1)
    val fps = _fps.asStateFlow()

    //TODO
    fun startMeasure(
        coroutineScope: CoroutineScope,
        socket: Socket,
    ) = coroutineScope.launch {
        while (!socket.isClosed) {
            delay(500)
            try {
                val _tickerCount = tickerCount.get()
                val _ticker = ticker.get()
                val frameTime = (_ticker / _tickerCount / 1000)
                if ((frameTime / 1000) != 0L) {
                    val fps = (1000 / (frameTime / 1000)).toInt()
                    _fps.value = if (fps > 99) 99 else fps
                    tickerCount.set(0)
                    ticker.set(0)
                }
            } catch (e: Exception) {
            }
        }
    }

    fun measure(frameTime: Long) {
        tickerCount.incrementAndGet()
        ticker.addAndGet(frameTime)
    }

}