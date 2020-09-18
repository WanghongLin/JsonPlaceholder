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

package com.example.jsonplaceholder.repository

import com.example.jsonplaceholder.AppExecutor
import com.example.jsonplaceholder.api.*
import com.example.jsonplaceholder.dao.AppDatabase
import com.example.jsonplaceholder.model.Post

class PostRepository(
    val appDatabase: AppDatabase,
    val appExecutor: AppExecutor
) : CrudRepository<Long, Post>(object :
    CrudResource<Long, Post>(
        appDatabase.postDao(),
        CrudService(AppServiceFactory.create(PostService::class.java), Long::class.java),
        appExecutor
    ) {
    override fun shouldReadList(resultType: List<Post>): Boolean = resultType.isEmpty()
})
