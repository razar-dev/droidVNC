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

enum class ProtocolVersion constructor(val versionString: String) {
    RFB_3_3("RFB 003.003"),
    RFB_3_7("RFB 003.007"),
    RFB_3_8("RFB 003.008");

    /**
     * Returns a protocol version from a version string if known, otherwise returns null
     */
    companion object {
        fun getVersionFromString(versionString: String): ProtocolVersion? {
            values().forEach {
                if (it.versionString == versionString) return it
            }
            return null
        }
    }
}
