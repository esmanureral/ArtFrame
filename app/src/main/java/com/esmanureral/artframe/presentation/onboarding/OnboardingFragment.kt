package com.esmanureral.artframe.presentation.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.esmanureral.artframe.R
import com.esmanureral.artframe.databinding.FragmentOnboardingBinding
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class OnboardingFragment : Fragment(R.layout.fragment_onboarding) {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OnboardingViewModel by viewModels { provideFactory() }

    private val pageCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            onPageSelectedLogic(position)
        }
    }

    private var tabMediator: TabLayoutMediator? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBinding(view)

        setupUi()
        startCollectors()
    }

    private fun initBinding(view: View) {
        _binding = FragmentOnboardingBinding.bind(view)
    }

    private fun provideFactory(): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return OnboardingViewModel(requireActivity().application) as T
            }
        }
    }

    private fun setupUi() {
        setupViewPager()
        setupClicks()
        setInitialBackVisibility()
    }

    private fun setupViewPager() = with(binding) {
        setViewPagerAdapter()
        attachTabMediator()
        registerPagerCallback()
    }

    private fun setViewPagerAdapter() = with(binding) {
        viewPager.adapter = OnboardingAdapter(viewModel.pages)
    }

    private fun attachTabMediator() = with(binding) {
        tabMediator = TabLayoutMediator(tabIndicator, viewPager) { _, _ -> }
            .also { it.attach() }
    }

    private fun registerPagerCallback() = with(binding) {
        viewPager.registerOnPageChangeCallback(pageCallback)
    }


    private fun onPageSelectedLogic(position: Int) {
        notifyViewModelPageChanged(position)
        updateBackVisibility(position)
    }

    private fun notifyViewModelPageChanged(position: Int) {
        viewModel.onPageChanged(position)
    }

    private fun setInitialBackVisibility() = with(binding) {
        updateBackVisibility(viewPager.currentItem)
    }

    private fun updateBackVisibility(currentPage: Int) = with(binding) {
        btnBack.visibility = if (currentPage == 0) View.INVISIBLE else View.VISIBLE
    }

    private fun setupClicks() {
        setupSkipClick()
        setupBackClick()
        setupNextClick()
    }

    private fun setupSkipClick() = with(binding) {
        btnSkip.setOnClickListener { onSkipClicked() }
    }

    private fun onSkipClicked() {
        viewModel.onSkipClick()
    }

    private fun setupBackClick() = with(binding) {
        btnBack.setOnClickListener { onBackClicked() }
    }

    private fun onBackClicked() = with(binding) {
        val current = viewPager.currentItem
        if (current > 0) goToPage(current - 1)
    }

    private fun setupNextClick() = with(binding) {
        btnNext.setOnClickListener { onNextClicked() }
    }

    private fun onNextClicked() {
        val state = viewModel.uiState.value
        if (state.isLastPage) onFinishOnboarding()
        else goToNextPage(state.currentPageIndex)
    }

    private fun onFinishOnboarding() {
        viewModel.onNextClick()
    }

    private fun goToNextPage(currentIndex: Int) = with(binding) {
        goToPage(currentIndex + 1)
    }

    private fun goToPage(index: Int) = with(binding) {
        viewPager.setCurrentItem(index, true)
    }

    private fun startCollectors() {
        collectState()
        collectEvents()
    }

    private fun collectState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    renderState(state)
                }
            }
        }
    }

    private fun renderState(state: OnboardingUiState) = with(binding) {
        btnNext.setIconResource(
            if (state.isLastPage) R.drawable.ic_done else R.drawable.arrow_right
        )
    }

    private fun collectEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    handleEvent(event)
                }
            }
        }
    }

    private fun handleEvent(event: OnboardingEvent) {
        when (event) {
            is OnboardingEvent.NavigateToHome -> navigateToHome()
        }
    }

    private fun navigateToHome() {
        val options = androidx.navigation.navOptions {
            popUpTo(R.id.onboardingFragment) { inclusive = true }
        }
        findNavController().navigate(R.id.artworkListFragment, null, options)
    }

    override fun onDestroyView() {
        cleanupPager()
        clearBinding()
        super.onDestroyView()
    }

    private fun cleanupPager() = with(binding) {
        viewPager.unregisterOnPageChangeCallback(pageCallback)
        detachTabMediator()
    }

    private fun detachTabMediator() {
        tabMediator?.detach()
        tabMediator = null
    }

    private fun clearBinding() {
        _binding = null
    }
}