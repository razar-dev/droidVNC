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

package app.razar.rfbcore.internal

import app.razar.rfbcore.encodings.EncodingType
import app.razar.rfbcore.encodings.pseudo.MouseType
import app.razar.rfbcore.other.CompressionLevel
import app.razar.rfbcore.other.JPEGQualityLevel

interface Settings {
    val allowCopyRect: Boolean

    val forcePreferredEncoding: Boolean

    val preferredEncoding: EncodingType

    /**
     * [MouseType.DISABLE]      -    only view mode
     * [MouseType.SERVER_SIDE]  -    mouse draw on server
     * [MouseType.CLIENT_SIDE]  -    draw cursor locally on client
     */
    val mouseMode: MouseType

    /**
     * If empty then all registered encodings are enabled
     */
    val enabledEncodings: List<Int>

    val compressionLevel: CompressionLevel

    val jpegQualityLevel: JPEGQualityLevel


    /**
     * Configuration guide
     *
     * @param allowCopyRect             Allow pseudo-encoding CopyRectEncoding
     *                                  [app.razar.rfbcore.encodings.frame.CopyRectEncoding]
     *                                  Useful when moving windows in the remote session.
     *                                  Saves bandwidth and drawing time when
     *                                  parts of the remote screen are moving around.
     *
     * @param forcePreferredEncoding    Use only the priority encoding.
     *                                  If the server does not support this encoding,
     *                                  the connection may not be established.
     *
     * @param preferredEncoding         Install the priority encoding
     *                                  if the server does not support this encoding will be used different.
     *
     * @param mouseMode                 Set mouse draw mode
     * @see [MouseType]
     *
     */
    fun set(
        allowCopyRect: Boolean = this.allowCopyRect,
        forcePreferredEncoding: Boolean = this.forcePreferredEncoding,
        preferredEncoding: EncodingType = this.preferredEncoding,
        mouseMode: MouseType = this.mouseMode,
        compressionLevel: CompressionLevel = this.compressionLevel,
        jpegQualityLevel: JPEGQualityLevel = this.jpegQualityLevel
    )

    /*
     * @param enabledEncodings          If the list is not empty, then
     *                                  [forcePreferredEncoding] and [preferredEncoding], [allowCopyRect],
     *                                  [mouseMode] values are ignored.
     *                                  The list is in order of priority from greater to lesser.
     */
    fun set(
        enabledEncodings: List<Int>
    )

}