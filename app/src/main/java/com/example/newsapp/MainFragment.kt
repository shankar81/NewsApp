package com.example.newsapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class MainFragment : Fragment() {
    private lateinit var newsViewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private val mainViewModel: MainViewModel by viewModels()
    private val categories = arrayListOf(
        "business",
        "entertainment",
        "general",
        "health",
        "science",
        "sports",
        "technology",
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        newsViewPager = view.findViewById(R.id.newsViewPager)
        tabLayout = view.findViewById(R.id.tabLayout)

        return view
    }

    override fun onStart() {
        super.onStart()
        newsViewPager.adapter = NewsAdapter(this)
        TabLayoutMediator(tabLayout, newsViewPager) { tab, position ->
            tab.text = categories[position]
        }.attach()
    }

    private inner class NewsAdapter(fragment: Fragment) :
        FragmentStateAdapter(fragment) {
        override fun getItemCount() = categories.size
        override fun createFragment(position: Int) = NewsFragment(categories[position])
    }
}