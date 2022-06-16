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
import androidx.lifecycle.viewModelScope
import app.razar.vnc.database.ServerInfo
import app.razar.vnc.repository.ServerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServerViewModel @Inject constructor(private val serverRepository: ServerRepository) : ViewModel() {

    private val _showDeleteDialog = MutableStateFlow(false)
    val showDeleteDialog: StateFlow<Boolean> = _showDeleteDialog.asStateFlow()

    private val _name = MutableStateFlow("")
    private val _host = MutableStateFlow("")
    private val _port = MutableStateFlow("")
    private val _secType = MutableStateFlow(0)
    private val _authName = MutableStateFlow("")
    private val _authPass = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()
    val host: StateFlow<String> = _host.asStateFlow()
    val port: StateFlow<String> = _port.asStateFlow()
    val secType: StateFlow<Int> = _secType.asStateFlow()
    val authName: StateFlow<String> = _authName.asStateFlow()
    val authPass: StateFlow<String> = _authPass.asStateFlow()

    private var _currentServer: ServerInfo? = null

    fun onOpenDeleteDialogClicked() {
        _showDeleteDialog.value = true
    }

    fun onDeleteDialogConfirm() {
        _showDeleteDialog.value = false
        viewModelScope.launch {
            _currentServer?.id?.let {
                serverRepository.remove(it)
            }
        }
    }

    fun onDeleteDialogDismiss() {
        _showDeleteDialog.value = false
    }

    fun currentServer(id: Long) {
        viewModelScope.launch {
            serverRepository.getById(id)?.let {
                _currentServer = it
                _name.value = it.name
                _host.value = it.ip
                _port.value = it.port.toString()
                _secType.value = it.auth
                _authName.value = it.username
                _authPass.value = it.password
            }
        }
    }

    fun onNameChange(it: String) = viewModelScope.launch {
        _name.value = it
        _currentServer!!.name = it
        serverRepository.update(_currentServer!!)
    }

    fun onSecChange(it: Int) = viewModelScope.launch {
        _secType.value = it
        _currentServer!!.auth = it
        serverRepository.update(_currentServer!!)
    }

    fun onHostChange(it: String) = viewModelScope.launch {
        _host.value = it
        _currentServer!!.ip = it
        serverRepository.update(_currentServer!!)
    }

    fun onPortChange(it: String) = viewModelScope.launch {
        _port.value = it
        _currentServer!!.port = it.toInt()
        serverRepository.update(_currentServer!!)
    }

    fun onUserChange(it: String) = viewModelScope.launch {
        _authName.value = it
        _currentServer!!.username = it
        serverRepository.update(_currentServer!!)
    }

    fun onPassChange(it: String) = viewModelScope.launch {
        _authPass.value = it
        _currentServer!!.password = it
        serverRepository.update(_currentServer!!)
    }

}