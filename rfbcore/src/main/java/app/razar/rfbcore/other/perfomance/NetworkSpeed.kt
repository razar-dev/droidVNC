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
import app.razar.rfbcore.other.getSpeedText

class NetworkSpeed(
    val upStreamSpeed: Long,
    val downStreamSpeed: Long,
    val download: String,
    val upload: String,
) {
    companion object {
        fun empty() = NetworkSpeed(0, 0, "", "")

        fun create(context: Context, download: Long, upload: Long): NetworkSpeed =
            NetworkSpeed(upload, download, context.getSpeedText(download), context.getSpeedText(upload))

    }
}