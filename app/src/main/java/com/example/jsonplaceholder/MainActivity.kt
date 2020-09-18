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

package com.example.jsonplaceholder

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.lifecycleScope
import com.example.jsonplaceholder.databinding.ActivityMainBinding
import com.example.jsonplaceholder.ui.AlbumFragment
import com.example.jsonplaceholder.ui.BaseFragment
import com.example.jsonplaceholder.ui.PostFragment
import com.example.jsonplaceholder.ui.UserFragment
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = MainPagerAdapter(this, supportFragmentManager)
        binding.viewPager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    binding.setVariable(BR.fragment, adapter.tabFragments[tab.position])
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_settings, menu)
        return true
    }

    private fun showDialog(): Boolean {
        val app = application as JsonPlaceholderApp
        lifecycleScope.launch {
            val first = app.dataStore.data.catch { exception ->
                if (exception is IOException) {
                    emit(AppSettingsOuterClass.AppSettings.getDefaultInstance())
                } else {
                    throw exception
                }
            }.first()
            showDialogUI(first.refreshPeriodMinutes)
        }
        return true
    }

    private suspend fun updateRefreshPeriod(i: Int) {
        val app = application as JsonPlaceholderApp
        withContext(Dispatchers.IO) {
            app.dataStore.updateData { currentSettings ->
                currentSettings.toBuilder().setRefreshPeriodMinutes(i).build()
            }
        }
    }

    private fun showDialogUI(i: Int = 0) {
        AlertDialog.Builder(this)
            .setTitle(R.string.refresh_period)
            .setSingleChoiceItems(
                R.array.refresh_period, i
            ) { dialog, selected ->
                dialog?.dismiss()
                lifecycleScope.launch {
                    updateRefreshPeriod(selected)
                }
            }.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> showDialog()
            else -> super.onOptionsItemSelected(item)
        }
    }
}

data class TabHolder(
    val resId: Int,
    val clz: Class<out BaseFragment>
)

class MainPagerAdapter(private val context: Context, fragmentManager: FragmentManager) :
    FragmentStatePagerAdapter(fragmentManager) {

    private val tabs = arrayOf(
        TabHolder(R.string.posts, PostFragment::class.java),
        TabHolder(R.string.users, UserFragment::class.java),
        TabHolder(R.string.albums, AlbumFragment::class.java)
    )

    val tabFragments = arrayOfNulls<BaseFragment>(tabs.size)

    override fun getCount() = tabs.size

    override fun getPageTitle(position: Int): CharSequence? =
        context.getString(tabs[position].resId)

    override fun getItem(position: Int): Fragment {
        return tabs[position].clz.newInstance().apply {
            tabFragments[position] = this
        }
    }
}