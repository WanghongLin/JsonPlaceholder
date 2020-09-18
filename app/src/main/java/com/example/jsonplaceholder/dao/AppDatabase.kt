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

package com.example.jsonplaceholder.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.jsonplaceholder.model.Album
import com.example.jsonplaceholder.model.Post
import com.example.jsonplaceholder.model.User

@Database(entities = [Post::class, User::class, Album::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun userDao(): UserDao
    abstract fun albumDao(): AlbumDao
}