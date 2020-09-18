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

package com.example.jsonplaceholder.model

import androidx.databinding.ObservableField

data class NewPost(
    val title: ObservableField<String> = ObservableField(""),
    val body: ObservableField<String> = ObservableField("")
) {
    fun toPost() = Post(0, 0, title.get() ?: "", body.get() ?: "")
}