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

import com.example.jsonplaceholder.model.User
import retrofit2.Call
import retrofit2.http.*

interface UserService {
    @POST("/users")
    fun create(@Body t: User): Call<User?>

    @GET("/users/{userId}")
    fun read(@Path("userId") id: Long): Call<User>

    @GET("/users")
    fun readList(): Call<List<User>>

    @PUT("/users/{userId}")
    fun update(@Path("userId") id: Long, @Body t: User): Call<User>

    @DELETE("/users")
    fun delete(id: Long): Call<Void>
}