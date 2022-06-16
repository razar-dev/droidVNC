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
 * Specifies the desired quality from the JPEG encoder. Encoding number
 * -23 implies high JPEG quality and -32 implies low JPEG quality.
 * Low quality can be useful in low bandwidth situations.
 * If the JPEG quality level is not specified, JpegCompression is not used in the Tight Encoding.
 *
 * The quality level concerns lossy compression and hence the setting is a tradeoff
 * between image quality and bandwidth. The specification defines neither what bandwidth
 * is required at a certain quality level nor what image quality you can expect.
 * The quality level is also just a hint to the server.
 */
enum class JPEGQualityLevel constructor(val level: Int) {
    JPEG_QUALITY_LEVEL_0(-32),
    JPEG_QUALITY_LEVEL_1(-31),
    JPEG_QUALITY_LEVEL_2(-30),
    JPEG_QUALITY_LEVEL_3(-29),
    JPEG_QUALITY_LEVEL_4(-28),
    JPEG_QUALITY_LEVEL_5(-27),
    JPEG_QUALITY_LEVEL_6(-26),
    JPEG_QUALITY_LEVEL_7(-25),
    JPEG_QUALITY_LEVEL_8(-24),
    JPEG_QUALITY_LEVEL_9(-23);

    companion object {
        fun valueOf(value: Int): JPEGQualityLevel {
            return when (value) {
                -32 -> JPEG_QUALITY_LEVEL_0
                -31 -> JPEG_QUALITY_LEVEL_1
                -30 -> JPEG_QUALITY_LEVEL_2
                -29 -> JPEG_QUALITY_LEVEL_3
                -28 -> JPEG_QUALITY_LEVEL_4
                -27 -> JPEG_QUALITY_LEVEL_5
                -26 -> JPEG_QUALITY_LEVEL_6
                -25 -> JPEG_QUALITY_LEVEL_7
                -24 -> JPEG_QUALITY_LEVEL_8
                -23 -> JPEG_QUALITY_LEVEL_9
                else -> JPEG_QUALITY_LEVEL_0
            }
        }
    }
}