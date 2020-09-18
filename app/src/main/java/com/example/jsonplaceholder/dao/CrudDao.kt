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

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CrudDao<ID, T> {

    /**
     * single item query, must provide default null argument, otherwise cannot compile with room
     */
    fun query(id: ID? = null): LiveData<out T>

    /**
     * batch query
     */
    fun query(): LiveData<out List<T>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(t: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(ts: List<T>)

    @Update
    fun update(t: T)

    @Update
    fun update(ts: List<T>)

    @Delete
    fun delete(t: T)

    @Delete
    fun delete(ts: List<T>)
}