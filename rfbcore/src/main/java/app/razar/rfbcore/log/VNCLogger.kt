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

package app.razar.rfbcore.log

import android.util.Log
import app.razar.rfbcore.other.DEFAULT_LOGGING_ENABLED
import app.razar.rfbcore.other.DEFAULT_TAG

open class VNCLogger(loggingEnabled: Boolean, loggingTag: String) : Logger {

        constructor() : this(DEFAULT_LOGGING_ENABLED, DEFAULT_TAG)

        /** Enable or disable logging.*/
        override var enabled: Boolean = loggingEnabled

        /** Sets the logging tag name. If the tag
         * name is more than 23 characters the default
         * tag name will be used as the tag.*/
        var tag: String = loggingTag

        private val loggingTag: String
            get() {
                return if (tag.length > 23) {
                    DEFAULT_TAG
                } else {
                    tag
                }
            }

        override fun d(message: String) {
            if (enabled) {
                Log.d(loggingTag, message)
            }
        }

        override fun d(message: String, throwable: Throwable) {
            if (enabled) {
                Log.d(loggingTag, message, throwable)
            }
        }

        override fun e(message: String) {
            if (enabled) {
                Log.e(loggingTag, message)
            }
        }

        override fun e(message: String, throwable: Throwable) {
            if (enabled) {
                Log.e(loggingTag, message, throwable)
            }
        }

    }