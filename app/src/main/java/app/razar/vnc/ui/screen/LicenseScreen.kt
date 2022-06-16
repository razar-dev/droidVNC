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

package app.razar.vnc.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.razar.vnc.R
import app.razar.vnc.other.GPLv3Logo
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.toPaddingValues

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicenseScreen(goBack: () -> Unit) {
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                navigationIcon = {
                    IconButton(onClick = goBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
                actions = {

                },
                modifier = Modifier.padding(
                    LocalWindowInsets.current.systemBars.toPaddingValues(
                        start = false,
                        end = false,
                        bottom = false
                    )
                )
            )

        },
        content = {
            Column(modifier = Modifier.padding(it)) {
                Row(horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Text("droidVNC is released under the GNU General Public License version 3",
                        Modifier.weight(1f),
                        fontSize = 14.sp)
                    Icon(GPLv3Logo, "", modifier = Modifier.size(72.dp), tint = Color.Red)
                }

                Spacer(modifier = Modifier.size(12.dp))

                Text(stringResource(R.string.other_lic), modifier = Modifier.padding(16.dp), fontSize = 18.sp)
                Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
                    LicenseText("Android Jetpack", "Apache 2.0")
                    LicenseText("Open Source icon packs", "MIT")
                    LicenseText("Accompanist Navigation Animation", "Apache 2.0")
                    LicenseText("ACRA", "Apache 2.0")
                    LicenseText("Material Components For Android", "Apache 2.0")
                    LicenseText("Hilt Android", "Apache 2.0")
                    LicenseText("Accompanist Insets Library", "Apache 2.0")
                    LicenseText("Coil Compose", "Apache 2.0")
                }
            }
        }
    )
}

@Composable
fun LicenseText(name: String, lic: String) = Column {
    Row(horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically) {
        Text(name)
        Text(lic, fontSize = 10.sp)
    }
    Spacer(modifier = Modifier.size(8.dp))
}