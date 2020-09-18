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

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ObservableField
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jsonplaceholder.BR
import com.example.jsonplaceholder.R
import com.example.jsonplaceholder.api.Status
import com.example.jsonplaceholder.databinding.ItemPostBinding
import com.example.jsonplaceholder.databinding.LayoutNewPostBinding
import com.example.jsonplaceholder.model.NewPost
import com.example.jsonplaceholder.model.Post
import com.example.jsonplaceholder.viewmodel.PostViewModel

class PostFragment : BaseFragment() {

    private val postViewModel: PostViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val postAdapter = PostAdapter()
        fragmentBaseBinding.recyclerView.adapter = postAdapter
        postViewModel.postList.observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Status.SUCCESS, Status.ERROR -> {
                    hideProgress()
                    postAdapter.submitList(resource.data)
                }

                Status.LOADING -> showProgress()
            }
        }
    }

    private fun createPost(post: Post) {
        postViewModel.createPost(post).observe(this) { newData ->
            when (newData.status) {
                Status.SUCCESS, Status.ERROR -> {
                    hideProgress()
                }
                Status.LOADING -> showProgress()
            }
        }
    }

    override fun onFabClick(view: View) {
        super.onFabClick(view)
        val newPost = NewPost()
        val binding = LayoutNewPostBinding.inflate(layoutInflater)
        binding.newPost = newPost
        AlertDialog.Builder(context)
            .setTitle(R.string.new_post)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                createPost(newPost.toPost())
                dialog?.dismiss()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ -> dialog?.dismiss() }
            .setView(binding.root)
            .show()
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.menuInfo !is MenuItemInfo<*>) {
            return super.onContextItemSelected(item)
        }
        val menuItemInfo = item.menuInfo as MenuItemInfo<*>
        if (menuItemInfo.data !is Post) {
            return super.onContextItemSelected(item)
        }

        if (item.itemId == R.id.update) {
            val newPost = NewPost(ObservableField(menuItemInfo.data.title), ObservableField(menuItemInfo.data.body))
            val binding = LayoutNewPostBinding.inflate(layoutInflater)
            binding.newPost = newPost

            AlertDialog.Builder(context)
                .setTitle(R.string.update_post)
                .setPositiveButton(R.string.ok) { dialog, _ ->
                    dialog?.dismiss()
                    val post = newPost.toPost().also { it.id = menuItemInfo.data.id }
                    updatePost(post)
                }
                .setNegativeButton(R.string.cancel) { dialog, _ -> dialog?.dismiss() }
                .setView(binding.root)
                .show()
        } else if (item.itemId == R.id.delete) {
            postViewModel.deletePost(menuItemInfo.data).observe(this) { newData ->
                when (newData.status) {
                    Status.SUCCESS, Status.ERROR -> {
                        hideProgress()
                    }
                    Status.LOADING -> showProgress()
                }
            }
        }
        return true
    }

    private fun updatePost(post: Post) {
        postViewModel.updatePost(post).observe(this) { newData ->
            when (newData.status) {
                Status.SUCCESS, Status.ERROR -> {
                    hideProgress()
                }
                Status.LOADING -> showProgress()
            }
        }
    }
}

class PostHolder(val itemPostBinding: ItemPostBinding) :
    RecyclerView.ViewHolder(itemPostBinding.root)

class PostAdapter : ListAdapter<Post, PostHolder>(Post.DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        return PostHolder(
            ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.itemPostBinding.setVariable(BR.post, getItem(position))
        holder.itemPostBinding.setVariable(BR.presenter, Presenter())
        holder.itemPostBinding.executePendingBindings()
        holder.itemPostBinding.root.setOnCreateContextMenuListener { menu, _, _ ->
            val update = menu.add(0, R.id.update, 0, R.string.update)
            update::class.java.getDeclaredField("mMenuInfo").apply {
                isAccessible = true
                set(update, MenuItemInfo(getItem(position)))
            }
            val delete = menu.add(0, R.id.delete, 1, R.string.delete)
            delete::class.java.getDeclaredField("mMenuInfo").apply {
                isAccessible = true
                set(delete, MenuItemInfo(getItem(position)))
            }
        }
    }
}