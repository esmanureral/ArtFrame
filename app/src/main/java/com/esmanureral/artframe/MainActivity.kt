package com.esmanureral.artframe

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.esmanureral.artframe.databinding.ActivityMainBinding
import com.esmanureral.artframe.BuildConfig

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var isLaunching = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupPermission()
        startLaunchAnimation()

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

                R.id.quizFragment -> {
                    navController.navigate(R.id.quizFragment)
                    true
                }

                else -> false
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (!isLaunching) {
                if (destination.id == R.id.fullScreenImageFragment ||
                    destination.id == R.id.detailFragment ||
                    destination.id == R.id.resultGameFragment ||
                    destination.id == R.id.virtualArtGalleryFragment
                ) {
                    binding.bottomNavigationView.visibility = View.GONE
                } else {
                    binding.bottomNavigationView.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setupPermission() {
        PermissionHelper.requestNotificationPermission(this) { granted ->
            if (granted) {
                println("Notification permission granted")
            } else {
                println("Notification permission denied")
            }
        }
    }

    private fun startLaunchAnimation() {
        binding.bottomNavigationView.visibility = View.GONE
        val shimmer = com.facebook.shimmer.Shimmer.AlphaHighlightBuilder()
            .build()
        binding.shimmerLayout.setShimmer(shimmer)
        binding.appNameTextView.text = getString(R.string.app_name)
        binding.shimmerLayout.startShimmer()
        Handler(Looper.getMainLooper()).postDelayed({
            binding.shimmerLayout.stopShimmer()
            binding.launchOverlay.visibility = View.GONE
            isLaunching = false
            binding.bottomNavigationView.visibility = View.VISIBLE
        }, 2500L)
    }
}