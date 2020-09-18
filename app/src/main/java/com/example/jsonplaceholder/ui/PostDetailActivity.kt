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

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.jsonplaceholder.api.Status
import com.example.jsonplaceholder.databinding.ActivityPostDetailBinding
import com.example.jsonplaceholder.viewmodel.PostViewModel

class PostDetailActivity : AppCompatActivity() {

    private val postViewModel: PostViewModel by viewModels()
    private lateinit var binding: ActivityPostDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        postViewModel.post().observe(this) { newData ->
            when (newData.status) {
                Status.SUCCESS, Status.ERROR -> {
                    binding.post = newData.data
                    binding.progressBar.visibility = View.GONE
                }
                Status.LOADING -> binding.progressBar.visibility = View.VISIBLE
            }
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}