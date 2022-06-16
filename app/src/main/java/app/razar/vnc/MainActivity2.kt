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

package app.razar.vnc

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.KeyEvent.*
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.W300
import androidx.compose.ui.text.font.FontWeight.Companion.W500
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import app.razar.rfbcore.other.State
import app.razar.vnc.other.MouseIcon
import app.razar.vnc.other.detectTapGestures
import app.razar.vnc.other.rememberStateWithLifecycle
import app.razar.vnc.ui.theme.CatalogTheme
import app.razar.vnc.viewmodel.ClientViewModel
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.StateFlow
import java.lang.ref.WeakReference

@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@AndroidEntryPoint
class MainActivity2 : ComponentActivity() {

    private lateinit var viewModel: ClientViewModel

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KEYCODE_BACK, KEYCODE_HOME, KEYCODE_VOLUME_DOWN, KEYCODE_VOLUME_UP, KEYCODE_VOLUME_MUTE -> {} // ignore
            KEYCODE_DEL -> {
                viewModel.onKeyEvent(0xff08)
            }
            KEYCODE_ENTER -> {
                viewModel.onKeyEvent(0xff0d)
            }
            else -> {
                event?.unicodeChar?.let {
                    viewModel.onKeyEvent(it)
                }
            }
        }

        return super.onKeyDown(keyCode, event)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        val id = intent.extras?.getLong("id")!!
        val host = intent.extras?.getString("host")!!
        val port = intent.extras?.getInt("port")!!
        val secType = intent.extras?.getInt("secType")!!
        val authName = intent.extras?.getString("authName")!!
        val authPass = intent.extras?.getString("authPass")!!

        //https://developer.android.com/guide/topics/display-cutout
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        val inputMethodManager: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        setContent {
            viewModel = hiltViewModel()
            viewModel.start(id, host, port, secType, authName, authPass)

            val drawerState = rememberDrawerState(DrawerValue.Open)
            val state by rememberStateWithLifecycle(viewModel.state)

            CatalogTheme {
                var show by remember { mutableStateOf(false) }

                LaunchedEffect(show) {
                    if (drawerState.isClosed) {
                        drawerState.open()
                    } else {
                        drawerState.close()
                    }
                }
                BackHandler {
                    show = !show
                }

                DisconectAlertDialog()

                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            Column(Modifier.padding(horizontal = 16.dp)) {
                                Text("Server: $host",
                                    fontSize = 24.sp,
                                    fontWeight = W300,
                                    modifier = Modifier.padding(vertical = 16.dp))
                                OutlinedCard(modifier = Modifier.padding(vertical = 16.dp).height(IntrinsicSize.Min)) {
                                    Row(modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically) {
                                        val speed by rememberStateWithLifecycle(viewModel.networkSpeed)
                                        val fps by rememberStateWithLifecycle(viewModel.fps)
                                        Text(fps.toString(),
                                            fontSize = 40.sp,
                                            fontWeight = W500,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.width(60.dp).padding(end = 8.dp))
                                        Divider(
                                            color = LocalContentColor.current,
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .padding(end = 8.dp)
                                                .width(1.dp)
                                        )
                                        Column {
                                            Row {
                                                Text("Send", modifier = Modifier.weight(1f))
                                                Text(speed.upload)
                                            }
                                            Row {
                                                Text("Receive", modifier = Modifier.weight(1f))
                                                Text(speed.download)
                                            }
                                        }
                                    }
                                }
                                //val currentEncoding by rememberStateWithLifecycle(viewModel.currentEncoding)
                                val levelList = stringArrayResource(R.array.level).toList()
                                Spacer(modifier = Modifier.height(8.dp))
                                ChangeItem(
                                    stringResource(R.string.encoding),
                                    viewModel.currentEncoding,
                                    FontAwesomeIcons.Solid.Code,
                                    listOf("RAW", "ZLIB", "ZRLEE", "TIGHT"),
                                    viewModel::changeEncoding
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                val currentQuality by rememberStateWithLifecycle(viewModel.currentQuality)
                                ChangeItem(
                                    stringResource(R.string.quality),
                                    levelList[currentQuality],
                                    FontAwesomeIcons.Solid.Image,
                                    levelList,
                                    viewModel::changeQuality
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                                val currentCompression by rememberStateWithLifecycle(viewModel.currentCompression)
                                ChangeItem(
                                    stringResource(R.string.compress),
                                    levelList[currentCompression],
                                    FontAwesomeIcons.Solid.Compress,
                                    levelList,
                                    viewModel::changeCompressLevel
                                )

                                Spacer(modifier = Modifier.weight(1f))
                                Divider()
                                Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                                    var expanded by remember { mutableStateOf(false) }
                                    IconButton({ expanded = true }, modifier = Modifier.weight(1f)) {
                                        Icon(FontAwesomeIcons.Solid.Mouse, "", modifier = Modifier.size(24.dp))
                                    }

                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false }
                                    ) {
                                        Text("Скопировать",
                                            fontSize = 18.sp,
                                            modifier = Modifier.padding(10.dp).clickable(onClick = {}))
                                        Text("Вставить",
                                            fontSize = 18.sp,
                                            modifier = Modifier.padding(10.dp).clickable(onClick = {}))
                                        Divider()
                                        Text("Настройки",
                                            fontSize = 18.sp,
                                            modifier = Modifier.padding(10.dp).clickable(onClick = {}))
                                    }
                                    Divider(
                                        color = Color(0x40000000),
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .width(1.dp).padding(vertical = 4.dp)
                                    )
                                    IconButton({
                                        inputMethodManager.toggleSoftInput(
                                            InputMethodManager.SHOW_FORCED, 0)
                                        show = false
                                    }, modifier = Modifier.weight(1f)) {
                                        Icon(FontAwesomeIcons.Solid.Keyboard, "", modifier = Modifier.size(24.dp))
                                    }
                                    Divider(
                                        color = Color(0x40000000),
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .width(1.dp).padding(vertical = 4.dp)
                                    )
                                    IconButton(viewModel::disconnect, modifier = Modifier.weight(1f)) {
                                        Icon(FontAwesomeIcons.Solid.PowerOff, "", modifier = Modifier.size(24.dp))
                                    }
                                }
                            }
                        },
                        content = {
                            when (state) {
                                State.CONNECTION, State.INIT, State.CONNECTED -> ConnectionScreen()
                                State.READY -> MouseControl(viewModel)
                                State.DISCONNECTED -> DisconnectScrean()
                                State.ERROR -> ErrorScrean()
                            }
                        })
                }
            }

        }
    }

    @Composable
    fun DisconectAlertDialog(viewModel: ClientViewModel = hiltViewModel()) {
        val disconnectDialogShow by rememberStateWithLifecycle(viewModel.disconnectDialogShow)
        if (disconnectDialogShow) {
            AlertDialog(
                onDismissRequest = viewModel::closeDialogDismiss,
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.closeDialogConfirm()
                        finish()
                    })
                    { Text(text = stringResource(R.string.ok)) }
                },
                dismissButton = {
                    TextButton(onClick = viewModel::closeDialogDismiss)
                    { Text(text = stringResource(R.string.cancel)) }
                },
                title = { Text(text = stringResource(R.string.confirm)) },
                text = { Text(text = stringResource(R.string.delete_connection)) }
            )
        }
    }

    @Composable
    fun Info(name: String, data: String) {
        Row {
            Text(name)
            Text(data)
        }
    }

    @Composable
    fun ErrorScrean() {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Image(Icons.Default.Warning,
                    "",
                    colorFilter = ColorFilter.tint(LocalContentColor.current),
                    modifier = Modifier.size(128.dp))
                Text("Connection error", fontSize = 36.sp)
            }
        }
    }

    @Composable
    fun DisconnectScrean() {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Image(Icons.Default.Warning,
                    "",
                    colorFilter = ColorFilter.tint(LocalContentColor.current),
                    modifier = Modifier.size(128.dp))
                Text("CONNECTION LOST", fontSize = 36.sp)
            }
        }
    }

    @Composable
    fun ConnectionScreen() {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {

                Text(stringResource(R.string.connection_title), fontSize = 36.sp)
                LinearProgressIndicator()
            }
        }
    }

    @Composable
    fun MouseControl(viewModel: ClientViewModel) {
        val painter = rememberVectorPainter(image = MouseIcon)
        val srcSize by rememberStateWithLifecycle(viewModel.size)
        val haptic = LocalHapticFeedback.current

        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = { context ->
                    ImageView(context).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT)
                        scaleType = ImageView.ScaleType.CENTER_INSIDE
                        adjustViewBounds = true
                    }
                },
                update = { test: ImageView ->
                    val w = WeakReference(test)
                    viewModel.onUpdate = {
                        w.get()?.setImageBitmap(it)
                    }

                },
                modifier = Modifier
                    .onGloballyPositioned(onGloballyPositioned = viewModel::setScreenSize)
                    .aspectRatio(srcSize.width.toFloat() / srcSize.height)
            )

            val offsetX by rememberStateWithLifecycle(viewModel.offsetX)
            val offsetY by rememberStateWithLifecycle(viewModel.offsetY)
            Canvas(modifier = Modifier.aspectRatio(srcSize.width.toFloat() / srcSize.height)
                .pointerInput(Unit) {
                    //detectTransformGestures { centroid, pan, zoom, rotation ->  }
                    detectTapGestures(
                        onLongPress = viewModel::onLongPress,
                        onTap = viewModel::onClick,
                        onDrag = viewModel::onDrag,
                        onSwap = viewModel::onSwap,
                        haptic = haptic
                    )
                }
            ) {
                translate(left = offsetX, top = offsetY) {
                    with(painter) {
                        draw(Size(15.6F, 25F))
                    }
                }
            }


        }
    }

    @ExperimentalMaterial3Api
    @Composable
    fun ChangeItem(
        name: String,
        state: String,
        icon: ImageVector,
        suggestions: List<String>,
        onClick: (index: Int) -> Unit,
    ) {
        var expanded by remember { mutableStateOf(false) }
        var textFieldSize by remember { mutableStateOf(Size.Zero) }
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
            border = BorderStroke((1).dp, contentColorFor(MaterialTheme.colorScheme.background)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp).onGloballyPositioned { coordinates ->
                //This value is used to assign to the DropDown the same width
                textFieldSize = coordinates.size.toSize()
            },
        ) {
            Row(
                modifier = Modifier.fillMaxSize().clickable(onClick = { expanded = !expanded }),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = icon, "",
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp).size(24.dp)
                )
                Text(
                    text = "$name: $state",
                    //fontFamily = quicksandFamily,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown, "",
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                Modifier
                    .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
                //modifier = Modifier.width(with(LocalDensity.current){textfieldSize.width.toDp()})
            ) {
                suggestions.forEachIndexed { index, label ->
                    DropdownMenuItem(onClick = {
                        expanded = false
                        onClick(index)
                    }, text = { Text(text = label) })
                }
            }
        }
    }

    @ExperimentalMaterial3Api
    @Composable
    fun <T> ChangeItem(
        name: String,
        state: StateFlow<T>,
        icon: ImageVector,
        suggestions: List<String>,
        onClick: (index: Int) -> Unit,
    ) {
        val currentEncoding by rememberStateWithLifecycle(state)
        ChangeItem(name, currentEncoding.toString(), icon, suggestions, onClick)
    }

    @Composable
    fun ServerErrorInfo() {

    }
}