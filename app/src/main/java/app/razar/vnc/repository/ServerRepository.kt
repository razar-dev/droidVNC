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

package app.razar.vnc.repository

import androidx.annotation.WorkerThread
import app.razar.vnc.database.ServerDatabaseDao
import app.razar.vnc.database.ServerInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ServerRepository @Inject constructor(private val serverDatabaseDao: ServerDatabaseDao) {
    val getServerList: Flow<List<ServerInfo>> = serverDatabaseDao.getAll()

    @WorkerThread
    suspend fun insert(item: ServerInfo) = withContext(Dispatchers.IO) {
        serverDatabaseDao.insert(item)
    }

    @WorkerThread
    suspend fun remove(item: ServerInfo) = withContext(Dispatchers.IO) {
        serverDatabaseDao.delete(item)
    }

    @WorkerThread
    suspend fun remove(id: Long) = withContext(Dispatchers.IO) {
        serverDatabaseDao.getById(id)?.let {
            serverDatabaseDao.delete(it)
        }
    }

    @WorkerThread
    suspend fun getById(id: Long) = withContext(Dispatchers.IO) {
        serverDatabaseDao.getById(id)
    }

    @WorkerThread
    suspend fun update(info: ServerInfo) = withContext(Dispatchers.IO) {
        serverDatabaseDao.update(info)
    }
}