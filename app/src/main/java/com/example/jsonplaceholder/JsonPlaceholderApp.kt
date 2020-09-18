/*
 * Copyright 2020 Wanghong Lin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jsonplaceholder

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.datastore.DataStore
import androidx.datastore.Serializer
import androidx.datastore.createDataStore
import androidx.room.Room
import com.example.jsonplaceholder.dao.AppDatabase
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.Executor
import java.util.concurrent.Executors

data class AppExecutor(
    val mainLooper: Executor = object : Executor {
        private val handler = Handler(Looper.getMainLooper())
        override fun execute(runnable: Runnable) {
            handler.post(runnable)
        }
    },
    val diskIO: Executor = Executors.newFixedThreadPool(1),
    val networkIO: Executor = Executors.newFixedThreadPool(2)
)

object AppSettingsSerializer : Serializer<AppSettingsOuterClass.AppSettings> {
    override fun readFrom(input: InputStream): AppSettingsOuterClass.AppSettings {
        return AppSettingsOuterClass.AppSettings.parseFrom(input)
    }

    override fun writeTo(t: AppSettingsOuterClass.AppSettings, output: OutputStream) {
        t.writeTo(output)
    }
}

class JsonPlaceholderApp : Application() {
    private lateinit var sInstance: JsonPlaceholderApp

    val database: AppDatabase by lazy {
        Room.databaseBuilder(sInstance, AppDatabase::class.java, "typicode")
            .build()
    }

    val appExecutor: AppExecutor by lazy { AppExecutor() }

    val dataStore: DataStore<AppSettingsOuterClass.AppSettings> by lazy {
        sInstance.createDataStore("app_settings.pb", serializer = AppSettingsSerializer)
    }

    override fun onCreate() {
        super.onCreate()
        sInstance = this
    }
}

