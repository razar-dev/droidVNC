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

import android.app.Application
import android.content.Context
import androidx.compose.material3.ExperimentalMaterial3Api
import dagger.hilt.android.HiltAndroidApp
import org.acra.ACRA.isACRASenderServiceProcess
import org.acra.config.dialog
import org.acra.data.StringFormat
import org.acra.ktx.initAcra


@ExperimentalMaterial3Api
@HiltAndroidApp
class BaseApplication : Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        if (isACRASenderServiceProcess()) {
            return
        }

        initAcra {
            buildConfigClass = BuildConfig::class.java
            reportFormat = StringFormat.JSON

            dialog {
                reportDialogClass = PanicResponderActivity::class.java
            }
        }
    }
}