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

package app.razar.rfbcore

import android.content.Context
import android.graphics.Bitmap
import android.util.Size
import app.razar.rfbcore.encodings.Encoding
import app.razar.rfbcore.encodings.EncodingType
import app.razar.rfbcore.encodings.frame.*
import app.razar.rfbcore.encodings.pseudo.LastRectEncoding
import app.razar.rfbcore.encodings.pseudo.MouseType
import app.razar.rfbcore.handshake.auth.SecurityType
import app.razar.rfbcore.internal.Settings
import app.razar.rfbcore.internal.VNCSetting
import app.razar.rfbcore.log.Logger
import app.razar.rfbcore.messages.FrameBufferInfo
import app.razar.rfbcore.messages.PixelFormat
import app.razar.rfbcore.messages.incoming.FramebufferUpdate
import app.razar.rfbcore.messages.incoming.IncomingMessage
import app.razar.rfbcore.messages.incoming.ServerCutText
import app.razar.rfbcore.messages.incoming.SetColourMapEntries
import app.razar.rfbcore.messages.outgoing.FramebufferUpdateRequest
import app.razar.rfbcore.messages.outgoing.KeyEvent
import app.razar.rfbcore.messages.outgoing.OutgoingMessage
import app.razar.rfbcore.messages.outgoing.PointerEvent
import app.razar.rfbcore.other.*
import app.razar.rfbcore.other.exception.ProtocolException
import app.razar.rfbcore.other.perfomance.FPSCounter
import app.razar.rfbcore.other.perfomance.NetworkSpeed
import app.razar.rfbcore.other.perfomance.SpeedMeasure
import app.razar.rfbcore.render.AndroidRender
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.net.InetSocketAddress
import java.nio.charset.StandardCharsets

class VNCImpl(val context: Context, val logger: Logger) : VNC {
    lateinit var dataInputStream: ByteReadChannel
    lateinit var dataOutputStream: ByteWriteChannel

    private var socket: Socket? = null

    // todo move to builder, for support other render (android, win,...)
    internal val render = AndroidRender()

    private val sendChanel = Channel<OutgoingMessage>()
    private val messageTypes: HashMap<Int, IncomingMessage> = HashMap()

    internal val registeredEncodings = mutableMapOf<Int, Encoding>()

    private val _screenSize = MutableStateFlow(Size(1, 1))
    private val _serverState = MutableStateFlow(State.CONNECTION)
    private val _serverConfig = MutableStateFlow(ServerConfig.empty())
    private val _lastFrameEncoding = MutableStateFlow(EncodingType.UNDEFINED)

    private val speedMeasure = SpeedMeasure()
    private val fpsCounter = FPSCounter()

    private val settings = VNCSetting(sendChanel, logger, registeredEncodings)

    /**
     * Connect to server
     */
    override fun start(host: String, port: Int, authenticationInfo: SecurityType) {
        _serverState.value = State.CONNECTION
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
            logger.d("Exception: $exception")
            _serverState.value = if (exception is ClosedReceiveChannelException) State.DISCONNECTED else
                State.ERROR
            exception.printStackTrace()
            socket?.close()
        }
        CoroutineScope(Dispatchers.Default).launch(coroutineExceptionHandler) {
            logger.d("Init")
            // Open connection
            socket = aSocket(ActorSelectorManager(Dispatchers.IO))
                .tcp()
                .connect(InetSocketAddress(host, port))
            _serverState.value = State.INIT
            dataInputStream = socket?.openReadChannel() ?: throw Exception()
            dataOutputStream = socket?.openWriteChannel(autoFlush = true) ?: throw Exception()
            //Calculate network speed
            speedMeasure.startMeasure(
                this,
                context = context,
                dataInputStream = dataInputStream,
                dataOutputStream = dataOutputStream,
                socket = socket ?: throw Exception("socket must not be null")
            )

            logger.d("Connection SUCCESSFUL")

            // Read the Protocol Version from the socket
            val protocolVersion = getProtocolVersion(byteReadChannel = dataInputStream)
            logger.d("Received protocol version ${protocolVersion.versionString} from server")
            // Reply the server that we accept the version we got
            dataOutputStream.writeFully("${protocolVersion.versionString}\n".toByteArray(StandardCharsets.UTF_8))

            // Get the available security types from the server
            val availableSecurityTypes = getAvailableSecurityTypes(dataInputStream, protocolVersion)
            logger.d("Received the following security types from server: ${availableSecurityTypes.contentToString()}")

            //IF protocol version > 3.3 => auth
            if (protocolVersion != ProtocolVersion.RFB_3_3) {
                logger.d("Send the server the specified security type")
                authenticationInfo.authenticate(dataInputStream, dataOutputStream)
            }

            // Check authentication result
            checkSecurityResult(protocolVersion, dataInputStream)

            //Send init message 1 - disconnect other connected client, 0 - skip
            logger.d("Sending client init message to the server")
            dataOutputStream.writeByte(0)
            logger.d("Receiving FrameBufferInfo from the server")
            // Read frameBufferInfo from server
            val frameBufferInfo = readServerInitMessage(dataInputStream)
            render.updateFrameBuffer(frameBufferInfo)
            logger.d("Init complete")
            logger.d("Received frameBufferInfo ${frameBufferInfo.pixelFormat.bitsPerPixel}")
            _screenSize.value = Size(frameBufferInfo.frameBufferWidth, frameBufferInfo.frameBufferHeight)

            logger.d("Create Bitmap ${frameBufferInfo.frameBufferWidth}x${frameBufferInfo.frameBufferHeight}")

            _serverConfig.value = ServerConfig(protocolVersion, 0, 0,
                Size(frameBufferInfo.frameBufferWidth, frameBufferInfo.frameBufferHeight))

            initInternalEncodings()

            fpsCounter.startMeasure(this, socket!!)

            launch {
                for (message in sendChanel) {
                    message.sendMessage(dataOutputStream)
                }
            }
            /*
            // 8 colours (1 bit per component)
            static const PixelFormat verylowColourPF(8, 3,false, true,
                                                     1, 1, 1, 2, 1, 0);
            // 64 colours (2 bits per component)
            static const PixelFormat lowColourPF(8, 6, false, true,
                                                 3, 3, 3, 4, 2, 0);
            // 256 colours (2-3 bits per component)
            static const PixelFormat mediumColourPF(8, 8, false, true,
                                        7, 7, 3, 5, 2, 0);
             */
            //sendMessage(SetPixelFormat(PixelFormat(32,8,0,1,255,255,255,16,8,0)))
            //frameBufferInfo.pixelFormat = PixelFormat(8,8,0,0,7,7,7,5,2,0)
            //render.updateFrameBuffer(frameBufferInfo)
            //sendMessage(SetPixelFormat(PixelFormat(8,8,0,0,7,7,7,5,2,0)))
            // Set default settings
            settings.initEncoding(
                allowCopyRect = true,
                forcePreferredEncoding = false,
                preferredEncoding = EncodingType.TIGHT,
                mouseMode = MouseType.CLIENT_SIDE,
                compressionLevel = CompressionLevel.COMPRESSION_LEVEL_9,
                jpegQualityLevel = JPEGQualityLevel.JPEG_QUALITY_LEVEL_9
            )

            // request first frame
            sendMessage(FramebufferUpdateRequest(
                false,
                0,
                0,
                frameBufferInfo.frameBufferWidth,
                frameBufferInfo.frameBufferHeight
            ))

            // send FrameBufferUpdateRequest every 25 millisecond
            launch {
                val message = FramebufferUpdateRequest(
                    true,
                    0,
                    0,
                    frameBufferInfo.frameBufferWidth,
                    frameBufferInfo.frameBufferHeight
                )
                while (true) {
                    delay(25)
                    sendMessage(message)
                }
            }

            _serverState.value = State.CONNECTED

            // Waiting first frame
            launch {
                delay(500)
                _serverState.value = State.READY
            }

            // Receive message from server
            while (socket?.isClosed == false) {
                val messageId = dataInputStream.readUnsignedByte()
                val messageType = messageTypes[messageId]
                    ?: throw ProtocolException("Received a message type that is not supported, aborting! ID-$messageId")
                val time = System.nanoTime()
                messageType.onMessageReceived(this@VNCImpl)
                fpsCounter.measure(System.nanoTime() - time)
            }
            _serverState.value = State.DISCONNECTED
        }
    }

    /**
     * Send [OutgoingMessage] to vnc server
     */
    internal suspend fun sendMessage(outgoingMessage: OutgoingMessage) =
        sendChanel.send(outgoingMessage)

    /**
     * Register all supported messages and encodings
     */
    private fun initInternalEncodings() {
        registeredEncodings.addEncoding(TightEncoding())
        registeredEncodings.addEncoding(ZRLEEncoding())
        registeredEncodings.addEncoding(ZLibEncoding())
        registeredEncodings.addEncoding(RawEncoding())
        registeredEncodings.addEncoding(LastRectEncoding())
        registeredEncodings.addEncoding(CopyRectEncoding())

        messageTypes.addMessage(FramebufferUpdate())
        messageTypes.addMessage(ServerCutText())
        messageTypes.addMessage(SetColourMapEntries())
    }

    /**
     * Disconnect from server
     */
    override fun stop() {
        socket?.close()
    }

    /**
     * Send message with mouse position
     */
    override fun onPointerEvent(x: Float, y: Float, leftButtonPressed: Boolean) {
        CoroutineScope(Dispatchers.Default).launch {
            sendMessage(PointerEvent(x.toInt(), y.toInt(), if (leftButtonPressed) 1 else 0))
        }
    }

    /**
     * Send mouse left click to server down/up
     */
    override fun onLeftClick(x: Int, y: Int) {
        CoroutineScope(Dispatchers.Default).launch {
            sendMessage(PointerEvent(x, y, 1))//Down
            sendMessage(PointerEvent(x, y, 0))//UP
        }
    }

    override fun onSwapDown(x: Int, y: Int) {
        CoroutineScope(Dispatchers.Default).launch {
            sendMessage(PointerEvent(x, y, 16))//Down
            sendMessage(PointerEvent(x, y, 0))//UP
        }
    }

    override fun onSwapUp(x: Int, y: Int) {
        CoroutineScope(Dispatchers.Default).launch {
            sendMessage(PointerEvent(x, y, 8))//Down
            sendMessage(PointerEvent(x, y, 0))//UP
        }
    }

    /**
     * Send mouse right click to server
     */
    override fun onRightClick(x: Int, y: Int) {
        CoroutineScope(Dispatchers.Default).launch {
            sendMessage(PointerEvent(x, y, 4))//Down
            sendMessage(PointerEvent(x, y, 0))//UP
        }
    }

    /**
     * Receive screen update
     */
    override fun onScreenUpdateA(bitmap: (Bitmap) -> Unit) {
        render.onUpdate = bitmap
    }


    override fun addFrameEncoding() {
        TODO("Not yet implemented")
    }

    /**
     * Send keystroke to server down/up
     */
    override fun onKeyTap(code: Int) {
        CoroutineScope(Dispatchers.Default).launch {
            sendMessage(KeyEvent(code, true))//Down
            sendMessage(KeyEvent(code, false))//UP
        }
    }

    override fun onResize(): StateFlow<Size> = _screenSize
    override fun fpsCount(): StateFlow<Int> = fpsCounter.fps
    override fun serverState(): StateFlow<State> = _serverState
    override fun lastFrameEncoding(): StateFlow<EncodingType> = _lastFrameEncoding

    override fun networkSpeed(): Flow<NetworkSpeed> = speedMeasure.networkSpeed
    override fun settings(): Settings = settings

    internal fun setLastFrameEncoding(id: Int) {
        _lastFrameEncoding.value = EncodingType.valueOf(id)
    }

    /**
     * Receives the server init message and parses it to the correct objects
     */
    private suspend fun readServerInitMessage(byteReadChannel: ByteReadChannel): FrameBufferInfo {
        // Read the width and height from the input stream
        val frameBufferWidth = byteReadChannel.readUnsignedShort()
        val frameBufferHeight = byteReadChannel.readUnsignedShort()

        // Read the pixel format from the input stream
        val pixelFormat = PixelFormat.fromBuffer(byteReadChannel)

        // Read the desktop name length
        val desktopNameLength = byteReadChannel.readInt()
        // If the length is less than 0 the desktop name is too short
        if (desktopNameLength < 0) throw ProtocolException("Desktop name is too short")

        // Read the ByteArray containing the desktop name from the input stream
        val desktopNameByteArray = ByteArray(desktopNameLength)
        byteReadChannel.readFully(desktopNameByteArray)

        // Decode it into a String using UTF-8
        val desktopName = String(desktopNameByteArray, StandardCharsets.UTF_8)
        // Return a FrameBufferInfo Object with all the info from the server init message
        return FrameBufferInfo(frameBufferWidth, frameBufferHeight, pixelFormat, desktopName)
    }

    /**
     * Reads the security result from the input stream and throws an exception if it was not successful
     */
    private suspend fun checkSecurityResult(protocolVersion: ProtocolVersion, byteReadChannel: ByteReadChannel) {
        val result = byteReadChannel.readInt()

        // If the result is not 0 there was a problem authenticating
        if (result != 0) {
            // If the protocol version is 3.8 the server sends a reason
            if (protocolVersion == ProtocolVersion.RFB_3_8) {
                throw ProtocolException("Failed to authenticate: ${getReasonMessage(byteReadChannel)}")
            }

            // If the protocol version is less than 3.8 we get no reason
            throw ProtocolException("Failed to authenticate")
        }
    }

    /**
     * Reads the Protocol Version from the Input Stream and returns a ProtocolVersion Object
     */
    private suspend fun getProtocolVersion(byteReadChannel: ByteReadChannel): ProtocolVersion {
        val protocolVersionBuffer = ByteArray(12)
        byteReadChannel.readFully(protocolVersionBuffer)

        // Convert the ByteArray to a String
        val protocolVersion = String(protocolVersionBuffer, StandardCharsets.UTF_8).replace("\n", "")

        // Returns a protocol version from a version string if known, otherwise throw error ProtocolException
        return ProtocolVersion.getVersionFromString(protocolVersion)
            ?: throw ProtocolException("Unsupported protocol version")
    }

    /**
     * Reads a reason string from the input stream if the server responds with one
     */
    private suspend fun getReasonMessage(byteReadChannel: ByteReadChannel): String {
        val reasonLength = byteReadChannel.readInt()

        if (reasonLength < 0) {
            throw ProtocolException("The reason length exceeded the maximum integer value")
        }

        // Read the reasonBytes from the InputStream
        val reasonBytes = ByteArray(reasonLength)
        byteReadChannel.readFully(reasonBytes)

        // Decode string
        return String(reasonBytes, StandardCharsets.US_ASCII)
    }

    /**
     * Returns an integer array of the available security types
     */
    private suspend fun getAvailableSecurityTypes(
        byteReadChannel: ByteReadChannel,
        protocolVersion: ProtocolVersion,
    ): IntArray {
        // Protocol version 3.3 uses only None and VncAuthentication as Authentication
        if (protocolVersion == ProtocolVersion.RFB_3_3) {
            // Read the securityType from the input stream
            val securityType = byteReadChannel.readInt()

            // If the securityType is 0 there was a problem getting it
            if (securityType == 0) {
                throw ProtocolException("Cannot get security type")
            }

            // Return the available security type
            return intArrayOf(securityType)
        }

        // Get the number of security types available
        val securityTypeCount = byteReadChannel.readUnsignedByte()

        // If there are no securityTypes available, read the reason and throw an exception
        if (securityTypeCount == 0) {
            throw ProtocolException(
                "Connect get security types from server Reason: ${
                    getReasonMessage(
                        byteReadChannel
                    )
                }"
            )
        }

        // Read securityTypes
        val securityTypes = IntArray(securityTypeCount)
        byteReadChannel.readFullyUnsignedBytes(securityTypes)

        return securityTypes
    }

}