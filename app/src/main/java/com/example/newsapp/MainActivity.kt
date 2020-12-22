package com.example.newsapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.bumptech.glide.Glide
import com.example.newsapp.background.OnAppKilledService
import com.example.newsapp.databinding.ActivityMainBinding
import com.example.newsapp.databinding.DrawerHeaderBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    private lateinit var binding: ActivityMainBinding
    private lateinit var headerBinding: DrawerHeaderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        val headerView: View = binding.navigationView.getHeaderView(0)

        headerBinding = DrawerHeaderBinding.bind(headerView)

        setContentView(binding.root)

        MobileAds.initialize(
            this
        ) { Log.d(TAG, "onInitializationComplete: ") }

        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

        val toolbar: Toolbar = binding.topAppBar

        headerBinding.drawerName.text = getString(R.string.name)
        headerBinding.drawerEmail.text = getString(R.string.email)
        Glide
            .with(this)
            .load(R.raw.travel)
            .circleCrop()
            .into(headerBinding.drawerImage)

        setSupportActionBar(toolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        NavigationUI.setupActionBarWithNavController(this, navController, binding.drawerLayout)

        appBarConfiguration = AppBarConfiguration(navController.graph, binding.drawerLayout)
        binding.navigationView.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)
        toolbar.setupWithNavController(navController, appBarConfiguration)

        startService(Intent(this, OnAppKilledService::class.java))
    }

    override fun onStart() {
        super.onStart()
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.logout -> {
                    toggleTheme()
                    true
                }
                else -> {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    menuItem.onNavDestinationSelected(navController)
                }
            }
        }
    }

    private fun toggleTheme() {
        when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_YES -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            else -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}