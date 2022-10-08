package com.rocky.moreapps.example

import android.content.Context
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.rocky.moreapps.example.MoreAppsExampleFragment.Companion.instance
import com.rocky.moreapps.example.R.string

class SectionsPagerAdapter internal constructor(
    private val context: Context,
    lifecycleOwner: LifecycleOwner,
    fm: FragmentManager,
) : FragmentStateAdapter(fm, lifecycleOwner.lifecycle) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> UpdaterExampleFragment.instance
            0 -> instance
            else -> instance
        }
    }

    fun getItemTitle(position: Int): String {
        return context.getString(TAB_TITLES[position])
    }

    override fun getItemCount(): Int {
        return TAB_TITLES.size
    }

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(string.tab_text_1, string.tab_text_2)
    }
}