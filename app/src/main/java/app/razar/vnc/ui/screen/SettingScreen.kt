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

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import app.razar.vnc.R
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.toPaddingValues

/*@Composable
fun SettingScreen(goBack: () -> Unit) {
    SettingScreen(
        viewModel = hiltViewModel(),
        goBack = goBack
    )
}*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(goBack: () -> Unit) {
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
        content = {}
    )
}