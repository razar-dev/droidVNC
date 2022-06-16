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

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import app.razar.vnc.ui.screen.*
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable

sealed class Screen(val route: String) {
    object ServerList : Screen("server_list")
    object ServerEdit : Screen("server_edit/{id}")
    object ServerAdd : Screen("server_add")
    object Setting : Screen("setting")
    object License : Screen("license")
}

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@ExperimentalAnimationApi
@Composable
internal fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    AnimatedNavHost(
        navController = navController,
        startDestination = Screen.ServerList.route,
        /*enterTransition = {
            slideInHorizontally(
                initialOffsetX = { 300 },
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { 300 },
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        },*/
        enterTransition = { fadeIn(animationSpec = tween(500)) },
        exitTransition = { fadeOut(animationSpec = tween(500)) },
        //popEnterTransition = { defaultPopEnterTransition() },
        //popExitTransition = { defaultPopExitTransition() },
        modifier = modifier,
    ) {
        composable(route = Screen.ServerList.route) {
            ServerList(
                navigateToServerAdd = {
                    navController.navigate(Screen.ServerAdd.route)
                },
                navigateToServerEdit = {
                    navController.navigate("server_edit/$it")
                },
                navigateToAbout = {

                },
                navigateToLicense = {
                    navController.navigate(Screen.License.route)
                },
                navigateToSetting = {
                    navController.navigate(Screen.Setting.route)
                }
            )
        }

        composable(route = Screen.ServerAdd.route) {
            AddServerLayout(goBack = navController::popBackStack)
        }
        composable(route = Screen.Setting.route) {
            SettingScreen(goBack = navController::popBackStack)
        }
        composable(route = Screen.License.route) {
            LicenseScreen(goBack = navController::popBackStack)
        }

        composable(route = Screen.ServerEdit.route, arguments = listOf(
            navArgument("id") {
                type = NavType.LongType
            }
        )) {
            ServerInfoScreen(goBack = navController::popBackStack, id = it.arguments?.getLong("id")!!)
        }
    }
}