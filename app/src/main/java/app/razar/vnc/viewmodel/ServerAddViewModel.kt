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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class ServerAddViewModel @Inject constructor(private val serverRepository: ServerRepository) : ViewModel() {

    private val regexHostIP =
        "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\$|^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)+([A-Za-z]|[A-Za-z][A-Za-z0-9\\-]*[A-Za-z0-9])\$"
    private val regexPort = "^([1-9][0-9]{0,3}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])\$"

    //Precompile regexp
    private val regexHostIPCompile = Pattern.compile(regexHostIP)
    private val regexPortCompile = Pattern.compile(regexPort)


    private val _name = MutableStateFlow("")
    private val _host = MutableStateFlow("")
    private val _port = MutableStateFlow("")
    private val _authName = MutableStateFlow("")
    private val _authPass = MutableStateFlow("")

    val name: StateFlow<String> = _name.asStateFlow()
    val host: StateFlow<String> = _host.asStateFlow()
    val port: StateFlow<String> = _port.asStateFlow()
    val authName: StateFlow<String> = _authName.asStateFlow()
    val authPass: StateFlow<String> = _authPass.asStateFlow()


    /**
     * Show error if va
     */
    private val _nameShowError = MutableStateFlow(false)
    private val _hostShowError = MutableStateFlow(false)
    private val _portShowError = MutableStateFlow(false)

    val nameShowError: StateFlow<Boolean> = _nameShowError.asStateFlow()
    val hostShowError: StateFlow<Boolean> = _hostShowError.asStateFlow()
    val portShowError: StateFlow<Boolean> = _portShowError.asStateFlow()

    val getAllServer = serverRepository.getServerList
        .flowOn(Dispatchers.Main).asLiveData(context = viewModelScope.coroutineContext)

    fun insert(server: ServerInfo) = viewModelScope.launch {
        serverRepository.insert(server)
    }

    fun onNameChange(it: String) {
        _name.value = it
        _nameShowError.value = it.isEmpty()
    }

    fun onHostChange(it: String) {
        _host.value = it
        _hostShowError.value = !regexHostIPCompile.matcher(it).find() and it.isNotEmpty()
    }

    fun onPortChange(it: String) {
        _port.value = it
        _portShowError.value = !regexPortCompile.matcher(it).find() and it.isNotEmpty()
    }

    fun onAuthNameChange(it: String) {
        _authName.value = it
    }

    fun onAuthPassChange(it: String) {
        _authPass.value = it
    }

    fun onAddClick(close: () -> Unit,) {
        val hostOk = regexHostIPCompile.matcher(_host.value).find()
        val portOk = regexPortCompile.matcher(_port.value).find()
        val isValid = hostOk and portOk

        _hostShowError.value = !hostOk
        _portShowError.value = !portOk
        _nameShowError.value = _name.value.isEmpty()

        if (isValid) {
            insert(ServerInfo(name = _name.value, ip = _host.value, port = _port.value.toInt(), addDate = System.currentTimeMillis()))
            close()
        }
    }
}