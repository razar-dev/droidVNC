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

package app.razar.vnc.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat


@Composable
fun CatalogTheme(content: @Composable () -> Unit) {
    val darkTheme = isSystemInDarkTheme()
    val colorScheme = if (!darkTheme) lightColorScheme() else darkColorScheme()
    val view = LocalView.current
    val context = LocalContext.current
    SideEffect {
        WindowCompat.getInsetsController(context.findActivity().window, view)!!.isAppearanceLightStatusBars = !darkTheme
    }
    // TODO: M3 MaterialTheme doesn't provide LocalIndication, remove when it does
    CompositionLocalProvider(LocalIndication provides rememberRipple()) {
        androidx.compose.material3.MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}

private tailrec fun Context.findActivity(): Activity =
    when (this) {
        is Activity -> this
        is ContextWrapper -> this.baseContext.findActivity()
        else -> throw IllegalArgumentException("Could not find activity!")
    }

