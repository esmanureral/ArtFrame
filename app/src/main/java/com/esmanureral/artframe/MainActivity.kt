package com.esmanureral.artframe

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.esmanureral.artframe.databinding.ActivityMainBinding
import com.facebook.shimmer.BuildConfig

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupPermission()
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        if (BuildConfig.DEBUG) {
            Toast.makeText(this, "Debug mode", Toast.LENGTH_SHORT).show()
        }
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.artworkListFragment -> {
                    navController.navigate(R.id.artworkListFragment)
                    true
                }

                R.id.favoritesFragment -> {
                    navController.navigate(R.id.favoritesFragment)
                    true
                }

                R.id.artistListFragment -> {
                    navController.navigate(R.id.artistListFragment)
                    true
                }

                else -> false
            }
        }
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.fullScreenImageFragment) {
                binding.bottomNavigationView.visibility = View.GONE
            } else {
                binding.bottomNavigationView.visibility = View.VISIBLE
            }
        }
    }

    private fun setupPermission() {
        PermissionHelper.requestNotificationPermission(this) { granted ->
            if (granted) println("Notification permission granted")
            else println("Notification permission denied")
        }
    }
}