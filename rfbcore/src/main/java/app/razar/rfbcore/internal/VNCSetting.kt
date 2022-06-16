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

import app.razar.rfbcore.encodings.Encoding
import app.razar.rfbcore.encodings.EncodingType
import app.razar.rfbcore.encodings.EncodingType.Companion.add
import app.razar.rfbcore.encodings.EncodingType.Companion.contains
import app.razar.rfbcore.encodings.pseudo.MouseType
import app.razar.rfbcore.log.Logger
import app.razar.rfbcore.messages.outgoing.OutgoingMessage
import app.razar.rfbcore.messages.outgoing.SetEncodings
import app.razar.rfbcore.other.CompressionLevel
import app.razar.rfbcore.other.JPEGQualityLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

internal class VNCSetting(
    private val sendChanel: Channel<OutgoingMessage>,
    private val logger: Logger,
    private val registeredEncodings: Map<Int, Encoding>,
) : Settings {

    override var allowCopyRect: Boolean = true
        private set

    override var forcePreferredEncoding: Boolean = false
        private set

    override var preferredEncoding: EncodingType = EncodingType.TIGHT
        private set

    override var mouseMode: MouseType = MouseType.CLIENT_SIDE
        private set

    override var compressionLevel: CompressionLevel = CompressionLevel.COMPRESSION_LEVEL_9
        private set

    override var jpegQualityLevel: JPEGQualityLevel = JPEGQualityLevel.JPEG_QUALITY_LEVEL_9
        private set

    override val enabledEncodings = mutableListOf<Int>()

    override fun set(
        allowCopyRect: Boolean,
        forcePreferredEncoding: Boolean,
        preferredEncoding: EncodingType,
        mouseMode: MouseType,
        compressionLevel: CompressionLevel,
        jpegQualityLevel: JPEGQualityLevel,
    ) {
        CoroutineScope(Dispatchers.Default).launch {
            initEncoding(allowCopyRect,
                forcePreferredEncoding,
                preferredEncoding,
                mouseMode,
                compressionLevel,
                jpegQualityLevel)
        }
    }

    //TODO
    override fun set(enabledEncodings: List<Int>) {
        if (enabledEncodings.size == 1) {
            forcePreferredEncoding = true
            preferredEncoding = EncodingType.valueOf(enabledEncodings[0].toString())
            return
        }

        if (enabledEncodings.contains(EncodingType.COPY_RECT)) allowCopyRect = true
    }

    suspend fun initEncoding(
        allowCopyRect: Boolean,
        forcePreferredEncoding: Boolean,
        preferredEncoding: EncodingType,
        mouseMode: MouseType,
        compressionLevel: CompressionLevel,
        jpegQualityLevel: JPEGQualityLevel,
    ) {
        this.allowCopyRect = allowCopyRect
        this.forcePreferredEncoding = forcePreferredEncoding
        this.preferredEncoding = preferredEncoding
        this.mouseMode = mouseMode
        this.compressionLevel = compressionLevel
        this.jpegQualityLevel = jpegQualityLevel


        val idList = mutableListOf<Int>()

        idList.add(preferredEncoding)

        if (allowCopyRect) idList.add(EncodingType.COPY_RECT)

        when (mouseMode) {
            MouseType.DISABLE -> idList.add(EncodingType.CURSOR_POS)
            MouseType.SERVER_SIDE -> {}//skip
            MouseType.CLIENT_SIDE -> idList.add(EncodingType.CURSOR_POS)
        }

        if (!forcePreferredEncoding) {
            registeredEncodings.keys.forEach {
                if (!idList.contains(it)) idList.add(it)
            }
            idList.add(compressionLevel.level)
            idList.add(jpegQualityLevel.level)
        } else if (preferredEncoding == EncodingType.TIGHT) {
            idList.add(compressionLevel.level)
            idList.add(jpegQualityLevel.level)
        }

        enabledEncodings.clear()
        enabledEncodings.addAll(idList)

        sendChanel.send(SetEncodings(idList))
    }

}