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
import com.example.jsonplaceholder.databinding.ItemAlbumBinding
import com.example.jsonplaceholder.model.Album
import com.example.jsonplaceholder.viewmodel.AlbumViewModel

class AlbumFragment : BaseFragment() {

    private val albumViewModel: AlbumViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = AlbumAdapter()
        fragmentBaseBinding.recyclerView.adapter = adapter
        albumViewModel.albumList.observe(viewLifecycleOwner) { newData ->
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

class AlbumViewHolder(val itemAlbumBinding: ItemAlbumBinding) :
    RecyclerView.ViewHolder(itemAlbumBinding.root)

class AlbumAdapter : ListAdapter<Album, AlbumViewHolder>(Album.DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        return AlbumViewHolder(
            ItemAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        holder.itemAlbumBinding.setVariable(BR.album, getItem(position))
        holder.itemAlbumBinding.executePendingBindings()
    }
}

