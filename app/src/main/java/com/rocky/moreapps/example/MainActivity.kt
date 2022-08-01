package com.rocky.moreapps.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.Tab
import com.google.android.material.tabs.TabLayoutMediator
import com.rocky.moreapps.example.R.id
import com.rocky.moreapps.example.R.layout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, this, supportFragmentManager)
        val viewPager = findViewById<ViewPager2>(id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs = findViewById<TabLayout>(id.tabs)
        TabLayoutMediator(
            tabs, viewPager
        ) { tab: Tab, position: Int ->
            tab.text = sectionsPagerAdapter.getItemTitle(position)
        }.attach()
    }
}