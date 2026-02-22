package com.esmanureral.artframe.presentation.onboarding

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.esmanureral.artframe.R
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val currentPageIndex: Int = 0,
    val isLastPage: Boolean = false,
    val buttonTextRes: Int = R.string.onboarding_btn_next
)

sealed class OnboardingEvent {
    data object NavigateToHome : OnboardingEvent()
}

class OnboardingViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = OnboardingPrefs(application)

    val pages: List<OnboardingPage> = createPages()

    private val _uiState = MutableStateFlow(buildStateForPage(0))
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<OnboardingEvent>(
        replay = 0,
        extraBufferCapacity = 1
    )
    val events: SharedFlow<OnboardingEvent> = _events.asSharedFlow()

    init {
        checkIfAlreadyDone()
    }

    private fun createPages(): List<OnboardingPage> {
        return listOf(
            OnboardingPage(R.string.onboarding_title_1, R.string.onboarding_desc_1, "onb_1.json"),
            OnboardingPage(R.string.onboarding_title_2, R.string.onboarding_desc_2, "onb_2.json"),
            OnboardingPage(R.string.onboarding_title_3, R.string.onboarding_desc_3, "onb_3.json"),
            OnboardingPage(R.string.onboarding_title_4, R.string.onboarding_desc_4, "onb_4.json")
        )
    }

    private fun checkIfAlreadyDone() {
        viewModelScope.launch {
            if (prefs.isDone()) emitNavigateHome()
        }
    }

    fun onPageChanged(position: Int) {
        updateStateForPage(position)
    }

    fun onNextClick() {
        if (isOnLastPage()) finishOnboarding()
    }

    fun onSkipClick() {
        finishOnboarding()
    }

    private fun updateStateForPage(position: Int) {
        _uiState.value = buildStateForPage(position)
    }

    private fun buildStateForPage(position: Int): OnboardingUiState {
        val lastIndex = pages.lastIndex
        val isLast = position == lastIndex
        val btnText = if (isLast) R.string.onboarding_btn_start else R.string.onboarding_btn_next

        return OnboardingUiState(
            currentPageIndex = position,
            isLastPage = isLast,
            buttonTextRes = btnText
        )
    }

    private fun isOnLastPage(): Boolean {
        return _uiState.value.currentPageIndex == pages.lastIndex
    }

    private fun finishOnboarding() {
        viewModelScope.launch {
            markDone()
            emitNavigateHome()
        }
    }

    private suspend fun markDone() {
        prefs.setDone(true)
    }

    private suspend fun emitNavigateHome() {
        _events.emit(OnboardingEvent.NavigateToHome)
    }
}