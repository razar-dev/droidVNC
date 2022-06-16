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

package app.razar.rfbcore.other

/**
 * Specifies the desired compression level. Encoding number -247 implies high compression level,
 * -256 implies low compression level. Low compression level can be useful to get low latency
 * in medium to high bandwidth situations and high compression level can be useful in low bandwidth situations.
 *
 * The compression level concerns the general tradeoff between CPU time and bandwidth.
 * It is therefore probably difficult to define exact cut-off points for which compression levels
 * should be used for any given bandwidth. The compression level is just a hint for the server,
 * and there is no specification for what a specific compression level means.
 *
 * Most servers use this hint to control lossless compression algorithms as the tradeoff between
 * CPU time and bandwidth is obvious there. However it can also be used for other algorithms
 * where this tradeoff is relevant.
 */
enum class CompressionLevel constructor(val level: Int) {
    COMPRESSION_LEVEL_0(-256), //LOW
    COMPRESSION_LEVEL_1(-255),
    COMPRESSION_LEVEL_2(-254),
    COMPRESSION_LEVEL_3(-253),
    COMPRESSION_LEVEL_4(-252),
    COMPRESSION_LEVEL_5(-251),
    COMPRESSION_LEVEL_6(-250),
    COMPRESSION_LEVEL_7(-249),
    COMPRESSION_LEVEL_8(-248),
    COMPRESSION_LEVEL_9(-247); //HIGH

    companion object {
        fun valueOf(value: Int): CompressionLevel {
            return when (value) {
                -256 -> COMPRESSION_LEVEL_0
                -255 -> COMPRESSION_LEVEL_1
                -254 -> COMPRESSION_LEVEL_2
                -253 -> COMPRESSION_LEVEL_3
                -252 -> COMPRESSION_LEVEL_4
                -251 -> COMPRESSION_LEVEL_5
                -250 -> COMPRESSION_LEVEL_6
                -249 -> COMPRESSION_LEVEL_7
                -248 -> COMPRESSION_LEVEL_8
                -247 -> COMPRESSION_LEVEL_9
                else -> COMPRESSION_LEVEL_9
            }
        }
    }
}