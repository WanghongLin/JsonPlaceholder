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

import com.example.jsonplaceholder.model.Album
import retrofit2.Call
import retrofit2.http.*

interface AlbumService {

    @POST("/albums")
    fun create(@Body t: Album): Call<Album?>

    @GET("/albums/{albumId}")
    fun read(@Path("albumId") id: Long): Call<Album>

    @GET("/albums")
    fun readList(): Call<List<Album>>

    @PUT("/albums/{albumId}")
    fun update(@Path("albumId") id: Long, @Body t: Album): Call<Album>

    @DELETE("/albums/{albumId}")
    fun delete(@Path("albumId") id: Long): Call<Void>
}