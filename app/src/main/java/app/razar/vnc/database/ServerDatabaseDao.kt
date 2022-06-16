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

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ServerDatabaseDao {

    @Query("SELECT * from server_list")
    fun getAll(): Flow<List<ServerInfo>>

    @Query("SELECT * from server_list where id = :id")
    suspend fun getById(id: Long): ServerInfo?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ServerInfo)

    @Update
    suspend fun update(item: ServerInfo)

    @Delete
    suspend fun delete(item: ServerInfo)

    @Query("DELETE FROM server_list")
    suspend fun deleteAllTodos()

}