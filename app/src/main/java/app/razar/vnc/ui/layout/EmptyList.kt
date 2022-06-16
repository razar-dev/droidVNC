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

package app.razar.vnc.ui.layout

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.razar.vnc.R

/**
 * Empty list layout
 */
@Preview
@Composable
fun EmptyList() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.height(175.dp).width(175.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.empty_state),
                contentDescription = null
            )
        }
        Text(
            text = stringResource(R.string.empty_list),
            //fontFamily = quicksandFamily,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal
        )
        Text(
            text = stringResource(R.string.empty_list_disc),
            //fontFamily = quicksandFamily,
            fontSize = 16.sp,
            fontWeight = FontWeight.Light
        )
        Spacer(modifier = Modifier.height(72.dp))
    }
}
