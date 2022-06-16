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

package app.razar.vnc.di

import android.content.Context
import androidx.room.Room
import app.razar.vnc.database.ServerDatabase
import app.razar.vnc.database.ServerDatabaseDao
import app.razar.vnc.repository.ServerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun providesServerDao(serverDatabase: ServerDatabase): ServerDatabaseDao = serverDatabase.serverDao()

    @Provides
    @Singleton
    fun providesServerDatabase(@ApplicationContext context: Context): ServerDatabase =
        Room.databaseBuilder(context, ServerDatabase::class.java, "ServerDB").build()

    @Provides
    fun providesUserRepository(serverDatabaseDao: ServerDatabaseDao): ServerRepository =
        ServerRepository(serverDatabaseDao)
}