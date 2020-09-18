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
import com.example.jsonplaceholder.model.Post

@Dao
interface PostDao : CrudDao<Long, Post> {

    @Query("SELECT * from post WHERE id = :id")
    override fun query(id: Long?): LiveData<Post>

    @Query("SELECT * from post")
    override fun query(): LiveData<List<Post>>
}