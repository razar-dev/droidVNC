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

import app.razar.rfbcore.encodings.frame.*
import app.razar.rfbcore.encodings.pseudo.CursorPosEncoding

enum class EncodingType constructor(val id: Int, val clazz: Class<*>) {

    UNDEFINED(-1234567, NullPointerException::class.java),

    // Frame encoding
    RAW(0, RawEncoding::class.java),
    ZLIB(6, ZLibEncoding::class.java),
    ZRLE(16, ZRLEEncoding::class.java),
    TIGHT(7, TightEncoding::class.java),

    COPY_RECT(1, CopyRectEncoding::class.java),
    DESKTOP_RESIZE(-223, DesktopResizeEncoding::class.java),

    //Pseudo encoding
    CURSOR_POS(-232, CursorPosEncoding::class.java);

    companion object {
        fun MutableList<Int>.add(element: EncodingType) {
            this.add(element.id)
        }

        fun List<Int>.contains(element: EncodingType): Boolean {
            return this.contains(element.id)
        }

        fun valueOf(value: Int): EncodingType {
            return when (value) {
                0 -> RAW
                6 -> ZLIB
                16 -> ZRLE
                7 -> TIGHT
                1 -> COPY_RECT
                -223 -> DESKTOP_RESIZE
                -232 -> CURSOR_POS
                else -> UNDEFINED
            }
        }
    }
}