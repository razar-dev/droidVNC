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

package app.razar.rfbcore.render

import android.graphics.Bitmap
import app.razar.rfbcore.messages.FrameBufferInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AndroidRender : Render {
    private var bitmap: Bitmap? = null
    //private var pixelArray: IntArray? = null
    private var frameBufferInfo: FrameBufferInfo? = null

    private var width: Int = 0
    private var height: Int = 0

    private var pixels = IntArray(1)

    var onUpdate: ((Bitmap) -> Unit)? = null

    override fun updateFrameBuffer(frameBufferInfo: FrameBufferInfo) {
        this.frameBufferInfo = frameBufferInfo
        this.width = frameBufferInfo.frameBufferWidth
        this.height = frameBufferInfo.frameBufferHeight
        if (width * height > pixels.size) pixels = IntArray(width * height)
        bitmap = Bitmap.createBitmap(
            frameBufferInfo.frameBufferWidth,
            frameBufferInfo.frameBufferHeight,
            Bitmap.Config.ARGB_8888
        )
        //pixelArray = IntArray(frameBufferInfo.frameBufferWidth * frameBufferInfo.frameBufferHeight)
    }

    override fun getFrameBufferInfo() = frameBufferInfo!!

    //fun getBitmap(): Bitmap = bitmap!!

    //fun screen(): IntArray = pixelArray!!

    /**
     * Change screen size
     */
    override fun changeSize(width: Int, height: Int) {
        if ((this.width != width) or (this.height != height)) {
            this.width = width
            this.height = height
            bitmap = Bitmap.createBitmap(
                width,
                height,
                Bitmap.Config.ARGB_8888
            )
            //pixelArray = IntArray(width * height)
        }
    }

    override fun getPixels(pixels: IntArray, x: Int, y: Int, width: Int, height: Int) =
        bitmap!!.getPixels(pixels, 0, width, x, y, width, height)

    override suspend fun setPixels(pixels: IntArray, x: Int, y: Int, width: Int, height: Int) {
        bitmap!!.setPixels(pixels, 0, width, x, y, width, height)
        CoroutineScope(Dispatchers.Main).launch {
            onUpdate?.invoke(bitmap!!)
        }
    }

}