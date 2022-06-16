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

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.razar.vnc.database.ServerInfo

@ExperimentalMaterial3Api
@ExperimentalFoundationApi
@Composable
fun ServerItem(serverInfo: ServerInfo, onClick: (item: ServerInfo) -> Unit, onLongClick: (item: ServerInfo) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        border = BorderStroke((1).dp, contentColorFor(MaterialTheme.colorScheme.background)),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).combinedClickable(onClick = {onClick(serverInfo)}, onLongClick = { onLongClick(serverInfo) }),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 8.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.padding(start = 8.dp).weight(1f)) {
                Text(
                    text = serverInfo.name.uppercase(),
                    //fontFamily = quicksandFamily,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Text(
                    text = "${serverInfo.ip}:${serverInfo.port}",
                    //fontFamily = quicksandFamily,
                    //color = MaterialTheme.colors.secondaryVariant,
                    fontSize = 10.sp,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.ExtraLight
                )
            }
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowRight, "",
                modifier = Modifier.padding(end = 16.dp)
            )
        }

    }
}