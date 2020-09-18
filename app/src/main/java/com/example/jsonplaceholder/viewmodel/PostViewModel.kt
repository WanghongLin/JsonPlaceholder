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

package com.example.jsonplaceholder.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import com.example.jsonplaceholder.JsonPlaceholderApp
import com.example.jsonplaceholder.api.Resource
import com.example.jsonplaceholder.model.Post
import com.example.jsonplaceholder.repository.PostRepository

class PostViewModel(application: Application, private val savedStateHandle: SavedStateHandle) :
    AndroidViewModel(application) {
    private val app = application as JsonPlaceholderApp
    private val postRepository = PostRepository(app.database, app.appExecutor)

    val postList = postRepository.readList()
    fun post(
        postId: Long = savedStateHandle["postId"] ?: throw RuntimeException("postId is empty")
    ): LiveData<Resource<Post>> = postRepository.read(postId)

    fun createPost(post: Post): LiveData<Resource<Post>> = postRepository.create(post)

    fun deletePost(post: Post): LiveData<Resource<Post>> = postRepository.delete(post)

    fun updatePost(post: Post): LiveData<Resource<Post>> = postRepository.update(post.id, post)
}