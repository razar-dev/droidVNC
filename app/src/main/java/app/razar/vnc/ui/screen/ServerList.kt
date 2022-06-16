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

import android.content.res.Configuration
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import app.razar.vnc.R
import app.razar.vnc.ui.layout.ListLayout
import app.razar.vnc.viewmodel.ServerListViewModel
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.toPaddingValues
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Bug
import compose.icons.fontawesomeicons.solid.File
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Composable
fun ServerList(
    navigateToServerEdit: (id: Long) -> Unit,
    navigateToServerAdd: () -> Unit,
    navigateToSetting: () -> Unit,
    navigateToLicense: () -> Unit,
    navigateToAbout: () -> Unit,
) {
    ServerList(
        viewModel = hiltViewModel(),
        navigateToServerEdit = navigateToServerEdit,
        navigateToServerAdd = navigateToServerAdd,
        navigateToSetting = navigateToSetting,
        navigateToLicense = navigateToLicense,
        navigateToAbout = navigateToAbout
    )
}

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Composable
internal fun ServerList(
    viewModel: ServerListViewModel,
    navigateToServerEdit: (id: Long) -> Unit,
    navigateToServerAdd: () -> Unit,
    navigateToSetting: () -> Unit,
    navigateToLicense: () -> Unit,
    navigateToAbout: () -> Unit,
) {

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            drawer(navigateToSetting, navigateToLicense, navigateToAbout)
        },
        content = {
            val scrollBehavior = remember { TopAppBarDefaults.pinnedScrollBehavior() }

            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    val backgroundColors = TopAppBarDefaults.centerAlignedTopAppBarColors()
                    val backgroundColor = backgroundColors.containerColor(
                        scrollFraction = scrollBehavior.scrollFraction
                    ).value
                    val foregroundColors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent
                    )
                    Surface(color = backgroundColor) {
                        SmallTopAppBar(
                            title = { Text(stringResource(R.string.app_name)) },
                            navigationIcon = {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(
                                        imageVector = Icons.Filled.Menu,
                                        contentDescription = "Localized description"
                                    )
                                }
                            },
                            actions = {
                                IconButton(onClick = {
                                    /*openSheet(
                                        BottomSheetScreen.Screen1(
                                            bottomSheetState,
                                            viewModel
                                        )
                                    )*/
                                    navigateToServerAdd()
                                }) {
                                    Icon(
                                        imageVector = Icons.Filled.Add,
                                        contentDescription = "Localized description"
                                    )
                                }

                            },
                            scrollBehavior = scrollBehavior,
                            colors = foregroundColors,
                            /**
                             * Affect on clearFocusOnKeyboardDismiss
                             * BUG [192043120]
                             * https://issuetracker.google.com/issues/192043120?hl=sr
                             */
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
                content = { padding ->
                    ListLayout(viewModel,
                        onItemClick = {
                            navigateToServerEdit(it.id)
                        },
                        onItemLongClick = { },
                        padding = padding
                    )
                }
            )
        })


}

@ExperimentalMaterial3Api
@Composable
fun AboutItem(name: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        border = BorderStroke((1).dp, contentColorFor(MaterialTheme.colorScheme.background)),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
            .clickable(onClick = { onClick() }),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 8.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon, "",
                modifier = Modifier.padding(end = 16.dp).size(24.dp)
            )
            Text(
                text = name,
                //fontFamily = quicksandFamily,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowRight, "",
                modifier = Modifier.padding(end = 16.dp)
            )
        }

    }
}

@ExperimentalMaterial3Api
@Composable
fun drawer(
    navigateToSetting: () -> Unit,
    navigateToLicense: () -> Unit,
    navigateToAbout: () -> Unit,
) {
    val configuration = LocalConfiguration.current
    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            drawerLandscape(navigateToSetting, navigateToLicense, navigateToAbout)
        }
        else -> {
            drawerPortrait(navigateToSetting, navigateToLicense, navigateToAbout)
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun drawerPortrait(
    navigateToSetting: () -> Unit,
    navigateToLicense: () -> Unit,
    navigateToAbout: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(LocalWindowInsets.current.systemBars.toPaddingValues())
    ) {
        Box(contentAlignment = Alignment.BottomCenter) {
            Icon(
                painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = "Localized description",
                modifier = Modifier.size(200.dp)
            )
            Text(stringResource(R.string.app_name), fontSize = 36.sp)
        }
        Text(stringResource(R.string.about_1), modifier = Modifier.padding(16.dp))
        Spacer(modifier = Modifier.size(16.dp))
        AboutItem("Setting", Icons.Default.Settings, navigateToSetting)
        AboutItem("License", FontAwesomeIcons.Solid.File, navigateToLicense)
        AboutItem("Bug report", FontAwesomeIcons.Solid.Bug, navigateToAbout)
    }
}

@ExperimentalMaterial3Api
@Composable
fun drawerLandscape(
    navigateToSetting: () -> Unit,
    navigateToLicense: () -> Unit,
    navigateToAbout: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(LocalWindowInsets.current.systemBars.toPaddingValues())
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painterResource(R.drawable.ic_launcher_foreground),
                    contentDescription = "Localized description",
                    modifier = Modifier.size(84.dp)
                )
                Text(stringResource(R.string.app_name), fontSize = 36.sp)
            }
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(stringResource(R.string.about_1), modifier = Modifier.padding(horizontal = 14.dp))
                Spacer(modifier = Modifier.size(8.dp))
                AboutItem("Setting", Icons.Default.Settings, navigateToSetting)
                AboutItem("License", FontAwesomeIcons.Solid.File, navigateToLicense)
                AboutItem("Bug report", FontAwesomeIcons.Solid.Bug, navigateToAbout)
            }

        }


    }
}