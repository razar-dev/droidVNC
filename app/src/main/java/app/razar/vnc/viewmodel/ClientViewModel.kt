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

package app.razar.vnc.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.util.Size
import android.view.View
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.razar.rfbcore.VNC
import app.razar.rfbcore.encodings.EncodingType
import app.razar.rfbcore.handshake.auth.SecurityTypeNone
import app.razar.rfbcore.handshake.auth.VncAuthentication
import app.razar.rfbcore.other.CompressionLevel
import app.razar.rfbcore.other.JPEGQualityLevel
import app.razar.rfbcore.other.State
import app.razar.rfbcore.other.perfomance.NetworkSpeed
import app.razar.vnc.other.SwapState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject


@HiltViewModel
class ClientViewModel @Inject constructor(@ApplicationContext context: Context) : ViewModel() {
    private val vnc = VNC.Builder(context).build()

    val fps: StateFlow<Int> = vnc.fpsCount().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 1,
    )

    val networkSpeed: StateFlow<NetworkSpeed> = vnc.networkSpeed().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NetworkSpeed.empty(),
    )

    val size: StateFlow<Size> = vnc.onResize().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Size(1, 1),
    )

    val state: StateFlow<State> = vnc.serverState().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = State.CONNECTION,
    )

    val currentEncoding: StateFlow<EncodingType> = vnc.lastFrameEncoding().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = EncodingType.UNDEFINED,
    )



    val offsetX = MutableStateFlow(0f)
    val offsetY = MutableStateFlow(0f)

    private val swapState = MutableSharedFlow<SwapState>()
    private val _currentQuality = MutableStateFlow(0)
    private val _currentCompression = MutableStateFlow(0)
    val currentQuality: StateFlow<Int> = _currentQuality
    val currentCompression: StateFlow<Int> = _currentCompression

    private var sizeScreen: Size = Size(1, 1)

    private var cursorX = 0F
    private var cursorY = 0F

    private var saved = false
    private var id = -1L
    private var id1 = -1L

    private val path = context.filesDir

    var onUpdate: ((Bitmap) -> Unit)? = null

    init {
        vnc.onScreenUpdateA {
            //data0.value = it
            onUpdate?.invoke(it)
            if ((!saved) and (id != -1L) and (id1 > 250)) {
                saved = true
                saveBitmap(it)
            } else if (id1 < 251) id1++
        }
        viewModelScope.launch {
            swapState.debounce(8).collect {
                when (it) {
                    SwapState.NONE -> {}
                    SwapState.UP -> {
                        vnc.onSwapUp(((size.value.width * cursorX) / sizeScreen.width).toInt(),
                            ((size.value.height * cursorY) / sizeScreen.height).toInt())
                    }
                    SwapState.DOWN -> {
                        vnc.onSwapDown(((size.value.width * cursorX) / sizeScreen.width).toInt(),
                            ((size.value.height * cursorY) / sizeScreen.height).toInt())
                    }
                }
            }
        }
    }

    private fun saveBitmap(bitmap: Bitmap) = CoroutineScope(Dispatchers.IO).launch {
        try {//viewModel.frameViewRef.get()?.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            File("$path/preview").mkdir()
            FileOutputStream("$path/preview/$id").use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun start(id: Long, host: String, port: Int, secType: Int, name: String, pass: String) {
        this.id = id
        vnc.start(host = host, port, if (secType == 0) SecurityTypeNone() else VncAuthentication(pass))
    }

    fun setScreenSize(onGloballyPositioned: LayoutCoordinates) {
        if (
            (sizeScreen.width != onGloballyPositioned.size.width)
            or
            (sizeScreen.height != onGloballyPositioned.size.height)
        )
            sizeScreen = Size(onGloballyPositioned.size.width, onGloballyPositioned.size.height)
    }

    private fun onMouseMove(x: Float, y: Float, sizeScreen: Size, pressed: Boolean) {
        cursorX = x
        cursorY = y
        //(2560 * offsetX) / 2400, (1080 * (offsetY)) / screenHeight
        vnc.onPointerEvent((size.value.width * x) / sizeScreen.width,
            (size.value.height * y) / sizeScreen.height,
            pressed)
    }

    fun onClick() {
        vnc.onLeftClick(((size.value.width * cursorX) / sizeScreen.width).toInt(),
            ((size.value.height * cursorY) / sizeScreen.height).toInt())
    }

    fun onDrag(dragAmount: Offset, pressed: Boolean) {
        when {
            offsetX.value < 0 -> offsetX.value = 0f
            offsetX.value + dragAmount.x > sizeScreen.width -> offsetX.value =
                sizeScreen.width.toFloat()
            else -> offsetX.value += dragAmount.x
        }
        when {
            offsetY.value < 0 -> offsetY.value = 0f
            offsetY.value + dragAmount.y > sizeScreen.height -> offsetY.value =
                sizeScreen.height.toFloat()
            else -> offsetY.value += dragAmount.y
        }
        onMouseMove(offsetX.value, offsetY.value, sizeScreen, pressed)
    }

    fun onSwap(state: SwapState) {
        viewModelScope.launch {
            swapState.emit(state)
        }

    }

    fun onKeyEvent(code: Int) {
        vnc.onKeyTap(code)
    }

    fun changeEncoding(index: Int) {
        val encodingType = when (index) {
            0 -> EncodingType.RAW
            1 -> EncodingType.ZLIB
            2 -> EncodingType.ZRLE
            3 -> EncodingType.TIGHT
            else -> EncodingType.TIGHT
        }
        vnc.settings().set(forcePreferredEncoding = true, preferredEncoding = encodingType)
    }

    fun changeQuality(index: Int) {
        _currentQuality.value = index
        val jpegQualityLevel = when (index) {
            0 -> JPEGQualityLevel.JPEG_QUALITY_LEVEL_9
            1 -> JPEGQualityLevel.JPEG_QUALITY_LEVEL_4
            2 -> JPEGQualityLevel.JPEG_QUALITY_LEVEL_0
            else -> JPEGQualityLevel.JPEG_QUALITY_LEVEL_9
        }
        vnc.settings().set(jpegQualityLevel = jpegQualityLevel)
    }

    fun changeCompressLevel(index: Int) {
        _currentCompression.value = index
        val compressionLevel = when (index) {
            0 -> CompressionLevel.COMPRESSION_LEVEL_9
            1 -> CompressionLevel.COMPRESSION_LEVEL_4
            2 -> CompressionLevel.COMPRESSION_LEVEL_0
            else -> CompressionLevel.COMPRESSION_LEVEL_9
        }
        vnc.settings().set(compressionLevel = compressionLevel)
    }

    fun View.clicks(): Flow<Unit> = callbackFlow {
        setOnClickListener {
            trySend(Unit).isSuccess
        }
        awaitClose { setOnClickListener(null) }
    }

    fun <T> Flow<T>.throttleFirst(windowDuration: Long): Flow<T> = flow {
        var lastEmissionTime = 0L
        collect { upstream ->
            val currentTime = System.currentTimeMillis()
            val mayEmit = currentTime - lastEmissionTime > windowDuration
            if (mayEmit) {
                lastEmissionTime = currentTime
                emit(upstream)
            }
        }
    }

    fun onLongPress() {
        vnc.onRightClick(((size.value.width * cursorX) / sizeScreen.width).toInt(),
            ((size.value.height * cursorY) / sizeScreen.height).toInt())
    }

    /**
     * Disconnect dialog
     */
    private var _disconnectDialogShow = MutableStateFlow(false)
    val disconnectDialogShow: StateFlow<Boolean> = _disconnectDialogShow
    fun closeDialogDismiss() {
        _disconnectDialogShow.value = false
    }

    fun closeDialogConfirm() {
        vnc.stop()
    }

    fun disconnect() {
        _disconnectDialogShow.value = true
    }
}