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

import android.content.Intent
import android.content.res.Configuration
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import app.razar.vnc.MainActivity2
import app.razar.vnc.R
import app.razar.vnc.other.clearFocusOnKeyboardDismiss
import app.razar.vnc.viewmodel.ServerViewModel
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.toPaddingValues
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Lock
import compose.icons.fontawesomeicons.solid.Unlock
import java.io.File

@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@Composable
fun ServerInfoScreen(goBack: () -> Unit, id: Long) {
    ServerInfoScreen(
        viewModel = hiltViewModel(),
        goBack = goBack,
        id = id
    )
}

@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@Composable
fun ServerInfoScreen(goBack: () -> Unit, viewModel: ServerViewModel, id: Long) {
    viewModel.currentServer(id)
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
                    IconButton(onClick = viewModel::onOpenDeleteDialogClicked) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Localized description"
                        )
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

        },
        content = {
            val showDialogState: Boolean by viewModel.showDeleteDialog.collectAsState()
            content(viewModel, id, it)
            DeleteAlertDialog(
                show = showDialogState,
                onDismiss = viewModel::onDeleteDialogDismiss,
                onConfirm = {
                    viewModel.onDeleteDialogConfirm()
                    goBack()
                }
            )

        }
    )

}

@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@Composable
fun content(viewModel: ServerViewModel, id: Long, padding: PaddingValues) {
    val configuration = LocalConfiguration.current
    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            ContentLandsacpe(viewModel, id, padding)
        }
        else -> {
            ContentPortrait(viewModel, id, padding)
        }
    }
}

@ExperimentalComposeUiApi
@Composable
@ExperimentalMaterial3Api
fun ContentLandsacpe(viewModel: ServerViewModel, id: Long, padding: PaddingValues) {
    val context = LocalContext.current

    val name by viewModel.name.collectAsState()
    val host by viewModel.host.collectAsState()
    val port by viewModel.port.collectAsState()
    val secType by viewModel.secType.collectAsState()
    val authName by viewModel.authName.collectAsState()
    val authPass by viewModel.authPass.collectAsState()

    Row(modifier = Modifier.padding(padding)) {
        Column( modifier = Modifier.padding(vertical = 1.dp).aspectRatio(1.7f)) {
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.padding(start = 16.dp).aspectRatio(1.7f),
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.aspectRatio(1.7f)) {
                    val cacheFile = File("${context.filesDir}/preview/$id")
                    if (cacheFile.exists()) {
                        Image(
                            rememberAsyncImagePainter(cacheFile,
                                filterQuality = FilterQuality.High),
                            contentDescription = "...",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.aspectRatio(1.7f)
                        )
                    }
                    Button(
                        modifier = Modifier.height(48.dp),
                        onClick = {
                            val intent = Intent(context, MainActivity2::class.java)
                            intent.putExtra("id", id)
                            intent.putExtra("host", host)
                            intent.putExtra("port", port.toInt())
                            intent.putExtra("secType", secType)
                            intent.putExtra("authName", authName)
                            intent.putExtra("authPass", authPass)
                            context.startActivity(intent)
                        }) {
                        Text(stringResource(R.string.connect))
                    }
                }

            }


        }

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
        ) {
            val textColor = LocalTextStyle.current.color.takeOrElse {
                LocalContentColor.current
            }
            EditButton(stringResource(R.string.server_name), name, textColor, viewModel::onNameChange)
            EditButton(stringResource(R.string.server_host), host, textColor, viewModel::onHostChange)
            EditButton(stringResource(R.string.server_port), port, textColor, viewModel::onPortChange)
            ChangeSecType(secType, viewModel::onSecChange)
            //EditButton(stringResource(R.string.server_user), authName, textColor, viewModel::onUserChange)
            if (secType > 0)
            EditButton(stringResource(R.string.server_pass), authPass, textColor, viewModel::onPassChange)

        }
    }


}

@Composable
@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
fun ContentPortrait(viewModel: ServerViewModel, id: Long, padding: PaddingValues) {
    val context = LocalContext.current

    val name by viewModel.name.collectAsState()
    val host by viewModel.host.collectAsState()
    val port by viewModel.port.collectAsState()
    val secType by viewModel.secType.collectAsState()
    val authName by viewModel.authName.collectAsState()
    val authPass by viewModel.authPass.collectAsState()

    Column(
        modifier = Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState())
    ) {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier.padding(horizontal = 16.dp).aspectRatio(1.7f)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.aspectRatio(1.7f)) {
                val cacheFile = File("${context.filesDir}/preview/$id")
                if (cacheFile.exists()) {
                    Image(
                        rememberAsyncImagePainter(cacheFile,
                            filterQuality = FilterQuality.High),
                        contentDescription = "...",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.aspectRatio(1.7f)
                    )
                } else {
                    Box(
                        modifier = Modifier.clip(RectangleShape).background(Color.Red)
                    ) {
                        Text(
                            "---NO PREVIEW---\nConnect to server for create preview",
                            modifier = Modifier.padding(4.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        val textColor = LocalTextStyle.current.color.takeOrElse {
            LocalContentColor.current
        }
        Spacer(modifier = Modifier.height(12.dp))
        EditButton(stringResource(R.string.server_name), name, textColor, viewModel::onNameChange)
        EditButton(stringResource(R.string.server_host), host, textColor, viewModel::onHostChange)
        EditButton(stringResource(R.string.server_port), port, textColor, viewModel::onPortChange)
        ChangeSecType(secType, viewModel::onSecChange)
        //EditButton(stringResource(R.string.server_user), authName, textColor, viewModel::onUserChange)
        if (secType > 0)
        EditButton(stringResource(R.string.server_pass), authPass, textColor, viewModel::onPassChange)
        Spacer(modifier = Modifier.weight(1f))
        val density = LocalDensity.current
        val bottomBarSize = with(density) { LocalWindowInsets.current.navigationBars.bottom.toDp() }

        Button(
            shape = RoundedCornerShape(0),
            modifier = Modifier.fillMaxWidth().height(48.dp + bottomBarSize),
            onClick = {
                val intent = Intent(context, MainActivity2::class.java)
                intent.putExtra("id", id)
                intent.putExtra("host", host)
                intent.putExtra("port", port.toInt())
                intent.putExtra("secType", secType)
                intent.putExtra("authName", authName)
                intent.putExtra("authPass", authPass)
                context.startActivity(intent)
            }) {
            Text(stringResource(R.string.connect), modifier = Modifier.padding(bottom = bottomBarSize))
        }
    }
}

@Composable
fun ChangeSecType(selectedItem: Int, onChange: (Int) -> Unit) {
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    var expanded by remember { mutableStateOf(false) }
    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp //it requires androidx.compose.material:material-icons-extended
    else
        Icons.Filled.ArrowDropDown
    val suggestions = listOf(stringResource(R.string.sec_type0), stringResource(R.string.sec_type1))
    val textColor = LocalTextStyle.current.color.takeOrElse {
        LocalContentColor.current
    }
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
        OutlinedTextField(
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = textColor,
                unfocusedLabelColor = textColor,
                disabledLabelColor = textColor,
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
            Modifier.width(with(LocalDensity.current) { textFieldSize.width.toDp() })
        ) {
            suggestions.forEachIndexed { index, label ->
                DropdownMenuItem(onClick = {
                    expanded = false
                    onChange(index)
                }, text = { Text(text = label) })
            }
        }
    }
}

@Composable
fun DeleteAlertDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = onConfirm)
                { Text(text = stringResource(R.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = onDismiss)
                { Text(text = stringResource(R.string.cancel)) }
            },
            title = { Text(text = stringResource(R.string.confirm)) },
            text = { Text(text = stringResource(R.string.delete_connection)) }
        )
    }
}

@ExperimentalMaterial3Api
@Composable
fun EditButton(name: String, value: String, textColor: Color, onConfirm: (String) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        border = BorderStroke((1).dp, contentColorFor(MaterialTheme.colorScheme.background)),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
    ) {
        var showDialog by remember { mutableStateOf(false) }
        Box(modifier = Modifier.clickable(onClick = { showDialog = true })) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 8.dp, top = 10.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                    Text(
                        text = name,
                        //fontFamily = quicksandFamily,
                        fontWeight = FontWeight.Light,
                    )
                    Text(
                        text = value,
                        //fontFamily = quicksandFamily,
                        fontWeight = FontWeight.Normal,
                    )
                }
                Icon(
                    imageVector = Icons.Outlined.Edit, "",
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
            if (showDialog) {
                var newText by remember { mutableStateOf(value) }
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    confirmButton = {
                        TextButton(onClick = {
                            onConfirm(newText)
                            showDialog = false
                        })
                        { Text(text = stringResource(R.string.ok)) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false })
                        { Text(text = stringResource(R.string.cancel)) }
                    },
                    title = { Text(text = stringResource(R.string.confirm)) },
                    text = {
                        OutlinedTextField(
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                unfocusedBorderColor = textColor,
                                textColor = textColor
                            ),
                            value = newText,
                            onValueChange = { newText = it },
                            leadingIcon = { Icon(imageVector = Icons.Filled.Edit, "") },
                            modifier = Modifier.fillMaxWidth().clearFocusOnKeyboardDismiss(),
                            singleLine = true,
                        )
                    }
                )
            }
        }
    }
}