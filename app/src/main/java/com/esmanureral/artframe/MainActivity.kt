package com.esmanureral.artframe

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.esmanureral.artframe.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isLaunching = true

    private val handler = Handler(Looper.getMainLooper())
    private val endLaunchRunnable = Runnable { endLaunchAnimation() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()

        val navController = findNavController()

        setupPermission()
        showDebugToastIfNeeded()
        setupBottomNav(navController)
        setupDestinationListener(navController)

        startLaunchAnimation()
    }

    override fun onDestroy() {
        handler.removeCallbacks(endLaunchRunnable)
        super.onDestroy()
    }

    private fun initBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun findNavController(): NavController {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHostFragment.navController
    }

    private fun setupPermission() {
        PermissionHelper.requestNotificationPermission(this) { granted ->
            logNotificationPermission(granted)
        }
    }

    private fun logNotificationPermission(granted: Boolean) {
        if (granted) println("Notification permission granted")
        else println("Notification permission denied")
    }

    private fun showDebugToastIfNeeded() {
        if (BuildConfig.DEBUG) {
            Toast.makeText(this, "Debug mode", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBottomNav(navController: NavController) = with(binding) {
        bottomNavigationView.setOnItemSelectedListener { item ->
            onBottomNavItemSelected(navController, item.itemId)
        }
    }

    private fun onBottomNavItemSelected(navController: NavController, destinationId: Int): Boolean {
        return when (destinationId) {
            R.id.artworkListFragment,
            R.id.favoritesFragment,
            R.id.artistListFragment,
            R.id.quizFragment -> {
                navigateBottom(navController, destinationId)
                true
            }
            else -> false
        }
    }

    private fun navigateBottom(navController: NavController, destinationId: Int) {
        if (isAlreadyOnDestination(navController, destinationId)) return

        navController.navigate(destinationId, null, bottomNavOptions())
    }

    private fun isAlreadyOnDestination(navController: NavController, destinationId: Int): Boolean {
        return navController.currentDestination?.id == destinationId
    }

    private fun bottomNavOptions(): NavOptions {
        return NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setRestoreState(true)
            .setPopUpTo(R.id.artworkListFragment, false, true)
            .build()
    }

    private fun setupDestinationListener(navController: NavController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            handleBottomNavVisibility(destination.id)
        }
    }

    private fun handleBottomNavVisibility(destinationId: Int) {
        val shouldHide = shouldHideBottomNav(destinationId)

        with(binding) {
            if (shouldHide) {
                bottomNavigationView.visibility = View.GONE
            } else if (!isLaunching) {
                bottomNavigationView.visibility = View.VISIBLE
            }
        }
    }

    private fun shouldHideBottomNav(destinationId: Int): Boolean {
        return destinationId == R.id.fullScreenImageFragment ||
                destinationId == R.id.detailFragment ||
                destinationId == R.id.resultGameFragment ||
                destinationId == R.id.virtualArtGalleryFragment ||
                destinationId == R.id.onboardingFragment
    }

    private fun startLaunchAnimation() {
        hideBottomNavForLaunch()
        startShimmer()
        scheduleEndLaunch()
    }

    private fun hideBottomNavForLaunch() = with(binding) {
        bottomNavigationView.visibility = View.GONE
    }

    private fun startShimmer() = with(binding) {
        val shimmer = com.facebook.shimmer.Shimmer.AlphaHighlightBuilder().build()
        shimmerLayout.setShimmer(shimmer)
        appNameTextView.text = getString(R.string.app_name)
        shimmerLayout.startShimmer()
    }

    private fun scheduleEndLaunch() {
        handler.postDelayed(endLaunchRunnable, 2500L)
    }

    private fun endLaunchAnimation() {
        stopShimmer()
        hideLaunchOverlay()
        markLaunchFinished()
        showBottomNavIfAllowed()
    }

    private fun stopShimmer() = with(binding) {
        shimmerLayout.stopShimmer()
    }

    private fun hideLaunchOverlay() = with(binding) {
        launchOverlay.visibility = View.GONE
    }

    private fun markLaunchFinished() {
        isLaunching = false
    }

    private fun showBottomNavIfAllowed() = with(binding) {
        val currentId = findNavController().currentDestination?.id
        if (currentId != R.id.onboardingFragment) {
            bottomNavigationView.visibility = View.VISIBLE
        }
    }
}