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

package app.razar.vnc.other

import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val MouseIcon: ImageVector
    get() {
        if (_mousePointer != null) {
            return _mousePointer!!
        }
        _mousePointer = ImageVector.Builder(name = "MousePointer", defaultWidth = 320.0.dp, defaultHeight =
        512.0.dp, viewportWidth = 320.0f, viewportHeight = 512.0f).apply {
            path(fill = SolidColor(Color.White), stroke = SolidColor(Color.Black), strokeLineWidth = 10.0f,
                strokeLineCap = StrokeCap.Butt, strokeLineJoin = StrokeJoin.Miter, strokeLineMiter = 10.0f,
                pathFillType = PathFillType.NonZero) {
                moveTo(302.189f, 329.126f)
                horizontalLineTo(196.105f)
                lineToRelative(55.831f, 135.993f)
                curveToRelative(3.889f, 9.428f, -0.555f, 19.999f, -9.444f, 23.999f)
                lineToRelative(-49.165f, 21.427f)
                curveToRelative(-9.165f, 4.0f, -19.443f, -0.571f, -23.332f, -9.714f)
                lineToRelative(-53.053f, -129.136f)
                lineToRelative(-86.664f, 89.138f)
                curveTo(18.729f, 472.71f, 0.0f, 463.554f, 0.0f, 447.977f)
                verticalLineTo(18.299f)
                curveTo(0.0f, 1.899f, 19.921f, -6.096f, 30.277f, 5.443f)
                lineToRelative(284.412f, 292.542f)
                curveToRelative(11.472f, 11.179f, 3.007f, 31.141f, -12.5f, 31.141f)
                close()
            }
        }
            .build()
        return _mousePointer!!
    }

private var _mousePointer: ImageVector? = null