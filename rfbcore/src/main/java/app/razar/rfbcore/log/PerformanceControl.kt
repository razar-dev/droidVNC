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

package app.razar.rfbcore.log

import android.util.Log

class PerformanceControl {
    var startTime: Long = 0
    val list = mutableMapOf<String, Long>()

    fun start() {
        startTime = System.nanoTime()
    }

    fun checkPoint(name: String) {
        list[name] = System.nanoTime()
    }

    fun finish() {
        for (item in list){
        Log.d("PERF_${item.key}", ((item.value-startTime)/1000).toString())}
    }
}