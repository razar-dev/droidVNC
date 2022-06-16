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

package app.razar.vnc.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "server_list")
class ServerInfo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "ip")
    var ip: String,

    @ColumnInfo(name = "port")
    var port: Int,

    @ColumnInfo(name = "add_date")
    var addDate: Long,

    @ColumnInfo(name = "last_connection")
    var lastConnection: Long = 0,

    @ColumnInfo(name = "auth")
    var auth: Int = 0,

    @ColumnInfo(name = "username")
    var username: String = "",

    @ColumnInfo(name = "password")
    var password: String = "",

): Parcelable