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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jsonplaceholder.api.Status
import com.example.jsonplaceholder.databinding.ItemUserBinding
import com.example.jsonplaceholder.model.User
import com.example.jsonplaceholder.viewmodel.UserViewModel

class UserFragment : BaseFragment() {

    private val userViewModel: UserViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = UserAdapter()

        fragmentBaseBinding.recyclerView.adapter = adapter
        userViewModel.userList.observe(viewLifecycleOwner) { newData ->
            when (newData.status) {
                Status.SUCCESS, Status.ERROR -> {
                    hideProgress()
                    adapter.submitList(newData.data)
                }
                Status.LOADING -> showProgress()
            }
        }
    }
}

class UserViewHolder(val itemUserBinding: ItemUserBinding) :
    RecyclerView.ViewHolder(itemUserBinding.root)

class UserAdapter : ListAdapter<User, UserViewHolder>(User.DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(
            ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.itemUserBinding.setVariable(BR.user, getItem(position))
        holder.itemUserBinding.executePendingBindings()
    }

}