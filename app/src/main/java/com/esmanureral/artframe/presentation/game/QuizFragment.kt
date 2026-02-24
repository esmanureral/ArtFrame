package com.esmanureral.artframe.presentation.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.esmanureral.artframe.ArtFrameApplication
import com.esmanureral.artframe.R
import com.esmanureral.artframe.data.local.ArtWorkSharedPreferences
import com.esmanureral.artframe.data.network.QuizQuestion
import com.esmanureral.artframe.databinding.FragmentQuizBinding
import com.esmanureral.artframe.loadWithIndicator
import com.esmanureral.artframe.showToast

class QuizFragment : Fragment() {

    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!
    private val viewModel: QuizViewModel by activityViewModels {
        val app = requireActivity().application as ArtFrameApplication
        val imageUrlTemplate = getString(R.string.artwork_image_url)
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return QuizViewModel(app.apiService, app.sharedPreferences, imageUrlTemplate) as T
            }
        }
    }
    private lateinit var prefs: ArtWorkSharedPreferences
    private var questionIndex = 1
    private val buttonList = mutableListOf<Button>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = ArtWorkSharedPreferences(requireContext())
        questionIndex = prefs.loadQuestionIndex()
        binding.tvQuestionNumber.text = getString(R.string.tv_question, questionIndex)

        setupButtons()
        setupObservers()
        setupNextButton()
        initStartQuiz()
        setOnClickListeners()
        checkIfCollectionsFetched()
    }

    private fun checkIfCollectionsFetched() {
        if (prefs.hasPopularArtistsFetched().not()) {
            viewModel.loadPopularArtworks()
        }
    }

    private fun setupButtons() {
        val optionButtons = listOf(binding.btnOption1, binding.btnOption2, binding.btnOption3)
        buttonList.addAll(elements = optionButtons)
        disableOptions(buttonList)
    }

    private fun setOnClickListeners() = with(binding) {
        btnCorrectAnswers.setOnClickListener {
            findNavController().navigate(
                QuizFragmentDirections.actionQuizFragmentToResultGameFragment()
            )
        }
        btnResetQuiz.setOnClickListener {
            viewModel.resetQuiz()
            questionIndex = 1
            prefs.saveQuestionIndex(questionIndex)
            tvQuestionNumber.text = getString(R.string.tv_question, questionIndex)
            buttonList.forEach { resetOptionStyle(it) }
            disableOptions(buttonList)
            viewModel.startQuiz()
        }
    }

    private fun initStartQuiz() {
        if (viewModel.quizQuestion.value == null) {
            viewModel.startQuiz()
        }
    }

    private fun setupObservers() {
        viewModel.quizQuestion.observe(viewLifecycleOwner) { question ->
            question?.let { showQuestion(it) }
            setGameVisibility()
        }

        viewModel.correctAnswers.observe(viewLifecycleOwner) { correctList ->
            updateScoreUI(correctList.size)
        }

        viewModel.error.observe(viewLifecycleOwner) { isError ->
            if (isError) requireContext().showToast(getString(R.string.error_message))
        }
        viewModel.isCollectionsReady.observe(viewLifecycleOwner) { isReady ->
            if (isReady) {
                val collections = viewModel.popularArtworks.value?.toList() ?: return@observe
                prefs.savePopularArtworks(collections)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                if (viewModel.quizQuestion.value == null) {
                    binding.groupProgress.isVisible = true
                    binding.containerGame.isVisible = false
                } else {
                    binding.progressIndicator.isVisible = true
                }
            } else {
                if (viewModel.quizQuestion.value != null) {
                    binding.groupProgress.isVisible = false
                    binding.containerGame.isVisible = true
                }
            }
        }
    }

    private fun setupNextButton() = with(binding) {
        btnNextQuestion.setOnClickListener {
            buttonList.forEach { resetOptionStyle(it) }
            questionIndex++
            tvQuestionNumber.text = getString(R.string.tv_question, questionIndex)
            prefs.saveQuestionIndex(questionIndex)
            viewModel.loadNewQuestion()
            disableOptions(buttonList)
        }
    }

    private fun showQuestion(question: QuizQuestion) = with(binding) {
        val optionButtons = listOf(btnOption1, btnOption2, btnOption3)
        btnOption1.post {
            loadButtons(question = question)
            disableOptions(optionButtons)

            ivArtwork.setOnClickListener {
                findNavController().navigate(
                    QuizFragmentDirections.actionQuizFragmentToFullScreenImageFragment(question.imageUrl)
                )
            }

            ivArtwork.loadWithIndicator(
                url = question.imageUrl,
                progressIndicator = progressIndicator,
                errorRes = R.drawable.error,
                onSuccess = {
                    val previousAnswer = viewModel.answeredQuestions[question.artworkId]
                    if (previousAnswer != null) {
                        val selectedBtn = optionButtons.firstOrNull { it.text == previousAnswer }
                        selectedBtn?.let { checkAnswer(it, question) }
                    } else {
                        optionButtons.forEach { it.isEnabled = true }
                    }
                },
                onError = { viewModel.loadNewQuestion() },
                lifecycleOwner = viewLifecycleOwner
            )
        }
    }

    private fun setupOptionButton(button: Button, optionText: String, question: QuizQuestion) {
        resetOptionStyle(button)
        button.text = optionText
        button.setOnClickListener { checkAnswer(button, question) }
    }

    private fun checkAnswer(selectedButton: Button, question: QuizQuestion) = with(binding) {
        val optionButtons = listOf(btnOption1, btnOption2, btnOption3)
        val correctButton = optionButtons.first { it.text == question.correctAnswer }

        val isAlreadyAnswered = viewModel.answeredQuestions.containsKey(question.artworkId)

        if (!isAlreadyAnswered) {
            viewModel.recordAnswer(question.artworkId, selectedButton.text.toString())
        }

        if (selectedButton == correctButton) {
            highlightAnswer(selectedButton, R.color.correct_answer, R.color.primary)
            if (!isAlreadyAnswered) {
                viewModel.onCorrectAnswer(question)
                if (question.isPopular) {
                    showPopularSuccessDialog()
                }
            }
        } else {
            highlightAnswer(selectedButton, R.color.wrong_answer, R.color.primary)
            highlightAnswer(correctButton, R.color.correct_answer, R.color.primary)
        }

        disableOptions(optionButtons)
    }

    private fun showPopularSuccessDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_popular_success, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnAwesome = dialogView.findViewById<Button>(R.id.btnAwesome)
        btnAwesome.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun updateScoreUI(correctCount: Int) = with(binding) {
        tvScore.text = getString(R.string.quiz_score, correctCount)
    }

    private fun disableOptions(optionButtons: List<Button>) {
        optionButtons.forEach { it.isEnabled = false }
    }

    private fun resetOptionStyle(button: Button) {
        button.apply {
            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.option_color))
            setTextColor(ContextCompat.getColor(requireContext(), R.color.primary))
        }
    }

    private fun highlightAnswer(button: Button, bgColorRes: Int, textColorRes: Int) {
        button.apply {
            setBackgroundColor(ContextCompat.getColor(requireContext(), bgColorRes))
            setTextColor(ContextCompat.getColor(requireContext(), textColorRes))
        }
    }

    private fun setGameVisibility() = with(binding) {
        groupProgress.isVisible = false
        containerGame.isVisible = true
    }

    private fun loadButtons(question: QuizQuestion) = with(binding) {
        val count = minOf(buttonList.size, question.options.size)

        repeat(count) { index ->
            val button = buttonList[index]
            resetOptionStyle(button)
            button.text = question.options[index]
            setupOptionButton(button, optionText = question.options[index], question)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        buttonList.clear()
    }
}