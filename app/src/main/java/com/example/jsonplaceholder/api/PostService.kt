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

package com.example.jsonplaceholder.api

import com.example.jsonplaceholder.model.Post
import retrofit2.Call
import retrofit2.http.*

interface PostService {
    @POST("/posts")
    fun create(@Body t: Post): Call<Post?>

    @GET("/posts/{postId}")
    fun read(@Path("postId") id: Long): Call<Post>

    @GET("/posts")
    fun readList(): Call<List<Post>>

    @PUT("/posts/{postId}")
    fun update(@Path("postId") id: Long, @Body t: Post): Call<Post>

    @DELETE("/posts/{postId}")
    fun delete(@Path("postId") id: Long): Call<Void>
}