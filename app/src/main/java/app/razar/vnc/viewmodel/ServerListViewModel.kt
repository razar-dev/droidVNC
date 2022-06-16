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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import app.razar.vnc.database.ServerInfo
import app.razar.vnc.repository.ServerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServerListViewModel @Inject constructor(private val serverRepository: ServerRepository) : ViewModel() {

    val getAllServer = serverRepository.getServerList
        .flowOn(Dispatchers.Main).asLiveData(context = viewModelScope.coroutineContext)

    fun insert(server: ServerInfo) = viewModelScope.launch {
        serverRepository.insert(server)
    }

    fun remove(item: ServerInfo) = viewModelScope.launch {
        serverRepository.remove(item)
    }

}