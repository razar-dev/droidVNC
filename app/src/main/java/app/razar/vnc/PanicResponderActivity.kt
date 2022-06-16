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

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.W500
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import app.razar.vnc.ui.theme.CatalogTheme
import com.google.accompanist.insets.ProvideWindowInsets
import org.acra.dialog.CrashReportDialogHelper

@ExperimentalMaterial3Api
class PanicResponderActivity : ComponentActivity() {

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val helper = CrashReportDialogHelper(this, intent)

        setContent {
            CatalogTheme {
                ProvideWindowInsets {
                    var show by remember { mutableStateOf(false) }
                    BackHandler {
                        show = true
                    }
                    ExitDialog(show, onDismiss = {
                        show = false
                    }, onConfirm = {
                        helper.cancelReports()
                        finish()
                    })
                    // A surface container using the 'background' color from the theme
                    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                        Scaffold(
                            floatingActionButton = {
                                ExtendedFloatingActionButton({}) {
                                    Icon(Icons.Default.Send, stringResource(R.string.panic_send), modifier = Modifier.padding(end = 8.dp))
                                    Text(stringResource(R.string.panic_send))
                                }
                            },
                            content = {
                                Column(modifier = Modifier.padding(24.dp).systemBarsPadding()
                                    .verticalScroll(rememberScrollState())) {
                                    Text(stringResource(R.string.panic_oh), fontSize = 36.sp)

                                    Text(stringResource(R.string.panic_wrong), fontSize = 24.sp)

                                    Text(stringResource(R.string.panic_what),
                                        fontSize = 16.sp,
                                        fontWeight = W500,
                                        modifier = Modifier.padding(top = 24.dp))
                                    Text(stringResource(R.string.panic_crash), fontSize = 16.sp)

                                    Text(stringResource(R.string.panic_info),
                                        fontSize = 16.sp,
                                        fontWeight = W500,
                                        modifier = Modifier.padding(top = 16.dp))
                                    SelectionContainer {
                                        Text(helper.reportData["STACK_TRACE"].toString(),
                                            fontSize = 12.sp,
                                            lineHeight = 12.sp,
                                            fontWeight = FontWeight.Light)

                                    }
                                }
                            }
                        )


                    }
                }
            }
        }
    }
}

@Composable
fun ExitDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = onConfirm)
                { Text(text = stringResource(R.string.yes)) }
            },
            dismissButton = {
                TextButton(onClick = onDismiss)
                { Text(text = stringResource(R.string.no)) }
            },
            title = { Text(text = stringResource(R.string.confirm)) },
            text = { Text(text = stringResource(R.string.delete_crash_report)) }
        )
    }
}