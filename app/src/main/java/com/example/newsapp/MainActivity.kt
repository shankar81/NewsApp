package com.example.newsapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private val categories = arrayListOf(
        "business",
        "entertainment",
        "general",
        "health",
        "science",
        "sports",
        "technology",
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(topAppBar)
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        newsViewPager.adapter = NewsAdapter(supportFragmentManager, lifecycle)
        TabLayoutMediator(tabLayout, newsViewPager) { tab, position ->
            tab.text = categories[position]
        }.attach()
    }

    private inner class NewsAdapter(manager: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(manager, lifecycle) {
        override fun getItemCount() = categories.size
        override fun createFragment(position: Int) = NewsFragment(categories[position])
    }
}