package com.example.newsapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.newsapp.databinding.FragmentMainBinding
import com.google.android.material.tabs.TabLayoutMediator

private const val TAG = "MainFragment"

class MainFragment : Fragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private var _binding: FragmentMainBinding? = null

    private val binding get() = _binding!!

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
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
        Log.d(TAG, "startActivity: $intent")
    }

    override fun onStart() {
        super.onStart()
        binding.newsViewPager.adapter = NewsAdapter(this)
        TabLayoutMediator(binding.tabLayout, binding.newsViewPager) { tab, position ->
            tab.text = categories[position]
        }.attach()
    }

    private inner class NewsAdapter(fragment: Fragment) :
        FragmentStateAdapter(fragment) {
        override fun getItemCount() = categories.size
        override fun createFragment(position: Int) = NewsFragment(categories[position])
    }

    // Note: Fragments outlive their views. Make sure you clean up any references to the binding class instance
    // in the fragment's onDestroyView() method.
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}