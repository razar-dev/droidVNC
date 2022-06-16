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

/**
 * Used to create custom loggers for VNC
 * */
interface Logger {

    /** Enable or disable logging.*/
    var enabled: Boolean

    /** Log debug information.
     * @param message message
     * */
    fun d(message: String)

    /** Log debug information with throwable.
     * @param message message
     * @param throwable throwable
     * */
    fun d(message: String, throwable: Throwable)

    /** Log error information.
     * @param message message
     * */
    fun e(message: String)

    /** Log error information with throwable.
     * @param message message
     * @param throwable throwable
     * */
    fun e(message: String, throwable: Throwable)

}