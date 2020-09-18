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

package com.example.jsonplaceholder.ui

import android.content.Intent
import android.view.View
import com.example.jsonplaceholder.model.Album
import com.example.jsonplaceholder.model.Post
import com.example.jsonplaceholder.model.User

class Presenter {


    fun onPostItemClick(view: View, post: Post) {
        view.context.startActivity(
            Intent(
                view.context,
                PostDetailActivity::class.java
            ).apply { putExtra("postId", post.id) }
        )
    }

    fun onUserItemClick(view: View, user: User) {

    }

    fun onAlbumItemClick(view: View, album: Album) {

    }
}