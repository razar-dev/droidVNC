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

package app.razar.rfbcore.handshake.auth

import io.ktor.utils.io.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class VncAuthentication(pass: String) : SecurityType {
    private val passwordBytes = ByteArray(8)

    init {
        // write byte-inverted pass to array
        for (i in 0..if (pass.length > 7) 7 else pass.length-1) {
            passwordBytes[i] = reverseBitsByte(pass[i].toByte())
        }

    }

    override suspend fun authenticate(dataInputStream: ByteReadChannel, dataOutputStream: ByteWriteChannel): Boolean {
        dataOutputStream.writeByte(2)
        // Read the server challenge
        val serverChallenge = ByteArray(16)
        dataInputStream.readFully(serverChallenge, 0, 16)

        // Encrypt the challenge with DES
        val encryptedChallenge = encrypt(serverChallenge, passwordBytes)

        // Send encrypted challenge
        dataOutputStream.writeAvailable(encryptedChallenge)
        return true
    }

    fun reverseBitsByte(b: Byte): Byte {
        var x = b
        val intSize = 8
        var y: Byte = 0
        for (position in intSize - 1 downTo 1) {
            y = (y+ (x.toInt() and 1 shl position)).toByte()
            x = (x.toInt() shr 1).toByte()
        }
        return y
    }

    /**
     * Encrypt challenge
     */
    private fun encrypt(serverChallenge: ByteArray, passwordBytes: ByteArray): ByteArray {
        val key = SecretKeySpec(passwordBytes, "DES")
        val cipher = Cipher.getInstance("DES/ECB/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        return cipher.doFinal(serverChallenge)
    }
}