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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import app.razar.vnc.R
import app.razar.vnc.other.clearFocusOnKeyboardDismiss
import app.razar.vnc.viewmodel.ServerAddViewModel
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.toPaddingValues
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Key
import compose.icons.fontawesomeicons.solid.Lock
import compose.icons.fontawesomeicons.solid.NetworkWired
import compose.icons.fontawesomeicons.solid.Unlock


@ExperimentalMaterial3Api
@Composable
fun AddServerLayout(goBack: () -> Unit) {
    AddServerLayout(
        viewModel = hiltViewModel(),
        goBack = goBack
    )
}

@ExperimentalMaterial3Api
@Composable
fun AddServerLayout(
    viewModel: ServerAddViewModel,
    goBack: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Surface {
                SmallTopAppBar(
                    title = { Text(stringResource(R.string.add_server)) },
                    navigationIcon = {
                        IconButton(onClick = {
                            goBack()
                        }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Localized description"
                            )
                        }
                    },
                    actions = {
                        TextButton(onClick = {
                            viewModel.onAddClick(close = goBack)
                        }
                        ) {
                            Text(stringResource(R.string.add))
                        }
                    },
                    /**
                     * Affect on clearFocusOnKeyboardDismiss
                     * BUG [192043120]
                     * https://issuetracker.google.com/issues/192043120?hl=sr
                     */
                    modifier = Modifier.padding(
                        LocalWindowInsets.current.systemBars.toPaddingValues(
                            start = false,
                            end = false,
                            bottom = false
                        )
                    )
                )
            }
        },
        content = {
            ServerParamLayout(viewModel, it)
        }
    )


}

@Composable
fun ServerParamLayout(
    viewModel: ServerAddViewModel,
    padding: PaddingValues,
) {

    val valueName by viewModel.name.collectAsState()
    val valueHost by viewModel.host.collectAsState()
    val valuePort by viewModel.port.collectAsState()
    val valueAuthPass by viewModel.authPass.collectAsState()


    val nameShowError by viewModel.nameShowError.collectAsState()
    val hostShowError by viewModel.hostShowError.collectAsState()
    val portShowError by viewModel.portShowError.collectAsState()

    val textColor = LocalTextStyle.current.color.takeOrElse {
        LocalContentColor.current
    }
    Column(
        modifier = Modifier.padding(start = 16.dp,
            end = 16.dp,
            top = padding.calculateTopPadding(),
            bottom = padding.calculateBottomPadding()
        ).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = textColor,
                textColor = textColor
            ),
            value = valueName,
            onValueChange = viewModel::onNameChange,
            label = { Text(stringResource(R.string.server_name)) },
            leadingIcon = { Icon(imageVector = Icons.Filled.Edit, "") },
            modifier = Modifier.fillMaxWidth().clearFocusOnKeyboardDismiss(),
            singleLine = true,
            isError = nameShowError,
            trailingIcon = {
                if (nameShowError)
                    Icon(Icons.Filled.Warning, "error", tint = MaterialTheme.colorScheme.error)
            },
        )
        AnimatedVisibility(
            visible = nameShowError,
            enter = fadeIn(
                // Overwrites the initial value of alpha to 0.4f for fade in, 0 by default
                //initialAlpha = 0.1f
                animationSpec = tween(durationMillis = 350)
            ),
            exit = fadeOut(
                // Overwrites the default animation with tween
                //animationSpec = tween(durationMillis = 250)
                animationSpec = tween(durationMillis = 350)
            )
        ) {
            Text(
                stringResource(R.string.name_error),
                color = MaterialTheme.colorScheme.error,
                fontSize = 10.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
        Row {
            OutlinedTextField(
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor = textColor,
                    textColor = textColor
                ),
                value = valueHost,
                onValueChange = viewModel::onHostChange,
                label = { Text(stringResource(R.string.server_host)) },
                leadingIcon = {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.NetworkWired,
                        "",
                        modifier = Modifier.size(24.dp)
                    )
                },
                modifier = Modifier.weight(1f).padding(end = 8.dp).clearFocusOnKeyboardDismiss(),
                isError = hostShowError,
                trailingIcon = {
                    if (hostShowError)
                        Icon(Icons.Filled.Warning, "error", tint = MaterialTheme.colorScheme.error)
                },
                singleLine = true
            )
            OutlinedTextField(
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor = textColor,
                    textColor = textColor
                ),
                value = valuePort,
                onValueChange = viewModel::onPortChange,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text(stringResource(R.string.server_port)) },
                modifier = Modifier.weight(0.45f).clearFocusOnKeyboardDismiss(),
                isError = portShowError,
                trailingIcon = {
                    if (portShowError)
                        Icon(Icons.Filled.Warning, "error", tint = MaterialTheme.colorScheme.error)
                },
                singleLine = true
            )
        }


        AnimatedVisibility(
            visible = hostShowError,
            enter = fadeIn(
                // Overwrites the initial value of alpha to 0.4f for fade in, 0 by default
                //initialAlpha = 0.1f
                animationSpec = tween(durationMillis = 350)
            ),
            exit = fadeOut(
                // Overwrites the default animation with tween
                //animationSpec = tween(durationMillis = 250)
                animationSpec = tween(durationMillis = 350)
            )
        ) {
            Text(
                stringResource(R.string.host_error),
                color = MaterialTheme.colorScheme.error,
                fontSize = 10.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
        AnimatedVisibility(
            visible = portShowError,
            enter = fadeIn(
                // Overwrites the initial value of alpha to 0.4f for fade in, 0 by default
                //initialAlpha = 0.1f
                animationSpec = tween(durationMillis = 350)
            ),
            exit = fadeOut(
                // Overwrites the default animation with tween
                //animationSpec = tween(durationMillis = 250)
                animationSpec = tween(durationMillis = 350)
            )
        ) {
            Text(
                stringResource(R.string.port_error),
                color = MaterialTheme.colorScheme.error,
                fontSize = 10.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }


        var expanded by remember { mutableStateOf(false) }
        val suggestions = listOf(stringResource(R.string.sec_type0), stringResource(R.string.sec_type1))

        var selectedItem by remember { mutableStateOf(0) }

        var textFieldSize by remember { mutableStateOf(Size.Zero) }

        val icon = if (expanded)
            Icons.Filled.KeyboardArrowUp //it requires androidx.compose.material:material-icons-extended
        else
            Icons.Filled.ArrowDropDown


        Column {
            OutlinedTextField(
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor = textColor,
                    unfocusedLabelColor = textColor,
                    disabledLabelColor = textColor,
                    disabledLeadingIconColor = textColor,
                    disabledBorderColor = textColor,
                    disabledTextColor = textColor,
                    textColor = textColor
                ),
                value = suggestions[selectedItem],
                readOnly = true,
                enabled = false,
                onValueChange = { },
                leadingIcon = {
                    Icon(
                        imageVector = when (selectedItem) {
                            0 -> FontAwesomeIcons.Solid.Unlock
                            else -> FontAwesomeIcons.Solid.Lock
                        }, "", modifier = Modifier.size(24.dp)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        //This value is used to assign to the DropDown the same width
                        textFieldSize = coordinates.size.toSize()
                    }.clickable(onClick = { expanded = !expanded }),
                label = { Text(stringResource(R.string.server_auth)) },
                trailingIcon = {
                    Icon(icon, "contentDescription",
                        Modifier.clickable { expanded = !expanded })
                },
                singleLine = true
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                Modifier
                    .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
            ) {
                suggestions.forEachIndexed { index, label ->
                    DropdownMenuItem(onClick = {
                        expanded = false
                        selectedItem = index
                    }, text = { Text(text = label) })
                }
            }
        }



        AnimatedVisibility(
            visible = selectedItem != 0,
            enter = fadeIn(
                // Overwrites the initial value of alpha to 0.4f for fade in, 0 by default
                //initialAlpha = 0.1f
                animationSpec = tween(durationMillis = 350)
            ),
            exit = fadeOut(
                // Overwrites the default animation with tween
                //animationSpec = tween(durationMillis = 250)
                animationSpec = tween(durationMillis = 350)
            )
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        unfocusedBorderColor = textColor,
                        textColor = textColor
                    ),
                    value = valueAuthPass,
                    onValueChange = viewModel::onAuthPassChange,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = { Text(stringResource(R.string.server_pass)) },
                    leadingIcon = {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.Key,
                            "",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth().clearFocusOnKeyboardDismiss(),
                    singleLine = true
                )
                Text(
                    stringResource(R.string.not_safe),
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }

    }
}