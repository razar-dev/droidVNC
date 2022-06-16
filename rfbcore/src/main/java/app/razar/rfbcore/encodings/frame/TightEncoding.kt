package app.razar.rfbcore.encodings.frame

import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import app.razar.rfbcore.encodings.Encoding
import app.razar.rfbcore.other.SharedBuffer
import app.razar.rfbcore.other.readCompactLength
import app.razar.rfbcore.other.readUnsignedByte
import app.razar.rfbcore.render.Render
import io.ktor.utils.io.*
import java.util.zip.Inflater


class TightEncoding : Encoding {

    private val tightInflaters = arrayOf(Inflater(), Inflater(), Inflater(), Inflater())
    private var zLibData = ByteArray(4096)
    private var inflaterBuffer = ByteArray(8192)
    private val tightFillColorBuffer = ByteArray(3)
    private val tightColorBuffer = ByteArray(768)
    private val uncompressedDataBuffer = ByteArray(tightMinimumCompressionSize * 3)
    private val tightColorPalette24 = IntArray(256)

    // reuse of var for minimal gc impact
    private var red: Int = 0
    private var green: Int = 0
    private var blue: Int = 0
    private var blueShift: Int = 0
    private var greenShift: Int = 0
    private var redShift: Int = 0
    private var rowSize: Int = 0
    private var bitsPerPixel: Int = 0
    private var bytesPerPixel: Int = 0
    private var pixelColorMask: Int = 0
    private var dataSize: Int = 0
    private var numberOfColors: Int = 0
    private var filterId: Int = 0
    private var pixelValue: Int = 0
    private var compressionControl: Int = 0
    private var jpegDataLength: Int = 0
    private var colorOffset: Int = 0
    private var positionInArray: Int = 0
    private var toUseStreamId: Int = 0
    private var currentX: Int = 0
    private var currentY: Int = 0
    private var xByteOffset: Int = 0
    private var yByteOffset: Int = 0
    private var currentByte: Byte = 0
    private var zLibDataLength: Int = 0
    private var rowBytes: Int = 0
    private var pixelCount: Int = 0
    private var n: Int = 0

    override suspend fun decode(
        render: Render,
        dataInputStream: ByteReadChannel,
        width: Int,
        height: Int,
        xPosition: Int,
        yPosition: Int,
    ) {
        pixelCount = width * height
        val frameBufferInfo = render.getFrameBufferInfo()

        val pixels = SharedBuffer.getPixels(pixelCount)

        // Initialize the general info provided by the FrameBufferInfo object
        bitsPerPixel = frameBufferInfo.pixelFormat.bitsPerPixel
        bytesPerPixel = bitsPerPixel / 8
        blueShift = frameBufferInfo.pixelFormat.blueShift
        greenShift = frameBufferInfo.pixelFormat.greenShift
        redShift = frameBufferInfo.pixelFormat.redShift

        // Reset the variables
        rowSize = width
        numberOfColors = 0

        // Reset pixelColorMask
        pixelColorMask = 0

        // Set the pixel mask
        repeat(bitsPerPixel / 4) {
            pixelColorMask = pixelColorMask or (1 shl it)
        }

        // Read the compression control byte from the input stream
        compressionControl = dataInputStream.readUnsignedByte()
        // Iterate through the first 4 bits and flush the zlib streams if told to do so
        repeat(4) {
            if ((compressionControl and 1) != 0) {
                tightInflaters[it].reset()
            }
            // Shift one bit
            compressionControl = compressionControl shr 1
        }

        when (compressionControl) {
            fillCompression -> {
                // Read the 3 bytes
                // If bytesPerPixel is not one the pixel data comes in 3 bytes: red, green, blue
                dataInputStream.readFully(tightFillColorBuffer, 0, 3)

                // Add the values to pixel data
                pixelValue = tightFillColorBuffer[0].toInt() and 0xFF shl (0 * 8)
                pixelValue = pixelValue or (tightFillColorBuffer[1].toInt() and 0xFF shl (1 * 8))
                pixelValue = pixelValue or (tightFillColorBuffer[2].toInt() and 0xFF shl (2 * 8))

                // Extract the color values from pixel data
                red = (pixelValue shr blueShift) and pixelColorMask
                green = (pixelValue shr greenShift) and pixelColorMask
                blue = (pixelValue shr redShift) and pixelColorMask

                // Color fill of all pixels
                val color = Color.rgb(red, green, blue)
                repeat(pixelCount) {
                    pixels[it] = color
                }

                // Set pixels
                render.setPixels(pixels, xPosition, yPosition, width, height)
            }
            jpegCompression -> {
                jpegDataLength = dataInputStream.readCompactLength()

                if (inflaterBuffer.size < jpegDataLength * 2) inflaterBuffer = ByteArray(2 * jpegDataLength + 1)
                dataInputStream.readFully(inflaterBuffer, 0, jpegDataLength)
                BitmapFactory.decodeByteArray(inflaterBuffer, 0, jpegDataLength)
                    .getPixels(pixels, 0, width, 0, 0, width, height)
                render.setPixels(pixels, xPosition, yPosition, width, height)
            }
            // If date not compressed with fillCompression or jpegCompression
            else -> {
                // Read filter id and parameters
                if ((compressionControl and tightExplicitFilter) != 0) {

                    // Read the filter id
                    filterId = dataInputStream.readUnsignedByte()

                    // Handle the different filter
                    when (filterId) {
                        // Handle the palette filter
                        tightFilterPalette -> {
                            // Read the number of colors
                            numberOfColors = dataInputStream.readUnsignedByte() + 1

                            // Read the colorBuffer
                            dataInputStream.readFully(tightColorBuffer, 0, numberOfColors * 3)

                            // Reset value
                            pixelValue = 0
                            colorOffset = 0
                            positionInArray = 0

                            repeat(numberOfColors * 3) {
                                // Add the new byte to the existing pixel
                                pixelValue =
                                    pixelValue or (tightColorBuffer[it].toInt() and 0xFF shl (colorOffset * 8))

                                // If we read the needed 3 bytes (RGB)
                                // shift them by the shift value and apply the color mask
                                if (++colorOffset == 3) {
                                    red = (pixelValue shr blueShift) and pixelColorMask
                                    green = (pixelValue shr greenShift) and pixelColorMask
                                    blue = (pixelValue shr redShift) and pixelColorMask

                                    tightColorPalette24[positionInArray] = Color.rgb(red, green, blue)

                                    positionInArray++
                                    // Reset the pixel data and color offset
                                    pixelValue = 0
                                    colorOffset = 0
                                }
                            }


                            if (numberOfColors == 2) {
                                rowSize = (width + 7) / 8
                            }

                        }
                        // Handle the gradient filter
                        tightFilterGradient -> {
                            //TODO see RFC 6143
                            Log.e("te", "te")
                        }
                    }
                }

                if (numberOfColors == 0 && bytesPerPixel == 4) rowSize *= 3

                // Calculate date size
                dataSize = height * rowSize

                // Data is to small to be compressed
                if (dataSize < tightMinimumCompressionSize) {
                    dataInputStream.readFully(uncompressedDataBuffer, 0, dataSize)

                    decodeBuffer(render, xPosition, yPosition, width, height, uncompressedDataBuffer, pixels)

                } else {
                    // Data was compressed with zlib and has to be decompressed
                    zLibDataLength = dataInputStream.readCompactLength()

                    // Resize buffer
                    if (zLibDataLength > zLibData.size) zLibData = ByteArray(zLibDataLength * 2)

                    dataInputStream.readFully(zLibData, 0, zLibDataLength)

                    toUseStreamId = compressionControl and 0x03

                    val inflater = tightInflaters[toUseStreamId]

                    inflater.setInput(zLibData, 0, zLibDataLength)

                    // Resize buffer
                    if (dataSize > inflaterBuffer.size) inflaterBuffer = ByteArray(dataSize * 2)

                    // inflate
                    inflater.inflate(inflaterBuffer, 0, dataSize)

                    decodeBuffer(render, xPosition, yPosition, width, height, inflaterBuffer, pixels)
                }
            }
        }

    }

    private suspend fun decodeBuffer(
        render: Render,
        xPosition: Int,
        yPosition: Int,
        width: Int,
        height: Int,
        src: ByteArray,
        pixels: IntArray,
    ) {
        if (numberOfColors != 0) {
            // The palette filter was applied
            if (numberOfColors == 2) {
                // Data is encoded in 1 bit per pixel (0 or 1 because we have only 2 colors in the palette)
                decodeData(
                    render = render,
                    xPosition = xPosition,
                    yPosition = yPosition,
                    width = width,
                    height = height,
                    src = src,
                    palette = tightColorPalette24,
                    pixels = pixels
                )
            } else {
                // Data is encoded in 8 bit (1 byte) because
                // the palette is larger than two an can contain up to 256 colors
                // Set pixels in bitmap

                currentX = xPosition
                currentY = yPosition

                repeat(dataSize) {

                    pixels[width * (currentY - yPosition) + currentX - xPosition] =
                        tightColorPalette24[src[it].toInt() and 0xFF]

                    currentX++


                    if (currentX == xPosition + width) {
                        currentX = xPosition
                        currentY++
                    }
                }
                render.setPixels(
                    pixels = pixels,
                    x = xPosition,
                    y = yPosition,
                    width = width,
                    height = height
                )
            }
        } else {
            // Initialize the pixel data
            pixelValue = 0
            colorOffset = 0

            currentX = xPosition
            currentY = yPosition

            repeat(dataSize) {
                // Add the new byte to the existing pixel
                pixelValue =
                    pixelValue or (src[it].toInt() and 0xFF shl (colorOffset * 8))

                // If we read the needed 3 bytes (RGB) shift them by the shift value and apply the color mask
                if (++colorOffset == 3) {
                    red = (pixelValue shr blueShift) and pixelColorMask
                    green = (pixelValue shr greenShift) and pixelColorMask
                    blue = (pixelValue shr redShift) and pixelColorMask

                    pixels[width * (currentY - yPosition) + currentX - xPosition] = Color.rgb(red, green, blue)

                    currentX++

                    // Reset the pixel data and color offset
                    pixelValue = 0
                    colorOffset = 0
                }

                if (currentX == xPosition + width) {
                    currentX = xPosition
                    currentY++
                }
            }
            render.setPixels(pixels, xPosition, yPosition, width, height)
        }
    }

    private suspend fun decodeData(
        render: Render,
        xPosition: Int,
        yPosition: Int,
        width: Int,
        height: Int,
        src: ByteArray,
        palette: IntArray,
        pixels: IntArray,
    ) {
        // Calculate the bytes contained in one row of pixels
        rowBytes = (width + 7) / 8

        // Assign the current x and y values
        currentX = xPosition
        currentY = yPosition

        yByteOffset = 0
        while (yByteOffset < height) {
            // Since the last byte gets filled with zeros firstly iterate over the first bytes
            xByteOffset = 0
            while (xByteOffset < width / 8) {
                // Get the current byte from the given array
                currentByte = src[yByteOffset * rowBytes + xByteOffset]

                // Iterate over the bits in the current byte
                n = 7
                while (n >= 0) {
                    // Update the pixel at the current Position with the color at index 0 or 1 in palette
                    // Increment the current pixel value and decrement the current bit shift value
                    pixels[width * (currentY - yPosition) + currentX++ - xPosition] =
                        palette[currentByte.toInt() shr n-- and 1]
                }

                xByteOffset++
            }

            // Iterate over the last byte which is filled with zeros (if there are a few pixels missing in the row
            n = 7
            while (n >= 8 - width % 8) {
                pixels[width * (currentY - yPosition) + currentX++ - xPosition] =
                    palette[src[yByteOffset * rowBytes + xByteOffset].toInt() shr n-- and 1]
            }

            currentY++
            currentX = xPosition
            yByteOffset++
        }
        render.setPixels(
            pixels = pixels,
            x = xPosition,
            y = yPosition,
            width = width,
            height = height
        )
    }

    override val encodingId: Int
        get() = 7

    companion object {
        private const val fillCompression = 0x08
        private const val jpegCompression = 0x09
        private const val tightExplicitFilter = 0x04
        private const val tightFilterPalette = 0x01
        private const val tightFilterGradient = 0x02
        private const val tightMinimumCompressionSize = 12
    }

}