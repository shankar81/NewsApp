package com.example.newsapp

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.example.newsapp.background.NewsWorkManager
import com.google.android.material.navigation.NavigationView
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.topAppBar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val navigationView: NavigationView = findViewById(R.id.navigationView)

        val headerView: View = navigationView.getHeaderView(0)

        val drawerImage: ImageView = headerView.findViewById(R.id.drawerImage)
        val drawerName: TextView = headerView.findViewById(R.id.drawerName)
        val drawerEmail: TextView = headerView.findViewById(R.id.drawerEmail)

        drawerName.text = getString(R.string.name)
        drawerEmail.text = getString(R.string.email)
        Glide
            .with(this)
            .load(R.raw.travel)
            .circleCrop()
            .into(drawerImage)


        setSupportActionBar(toolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)

        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        navigationView.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)
        toolbar.setupWithNavController(navController, appBarConfiguration)

        // Set up work request only when app is background
        // And change this request to Periodic Request
        // And also keep the time delay between 2 hours - 4 hours
        val oneTimeRequest = OneTimeWorkRequest
            .Builder(NewsWorkManager::class.java)
            .setInitialDelay(1, TimeUnit.MINUTES)
            .build()

        WorkManager
            .getInstance(this)
            .enqueueUniqueWork("One_Time_Worker", ExistingWorkPolicy.REPLACE, oneTimeRequest)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}