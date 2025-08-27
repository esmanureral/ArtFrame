package com.esmanureral.artframe.presentation.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.esmanureral.artframe.R
import com.esmanureral.artframe.data.local.ArtWorkSharedPreferences
import com.esmanureral.artframe.data.network.QuizQuestion
import com.esmanureral.artframe.databinding.FragmentQuizBinding
import com.esmanureral.artframe.loadWithIndicator
import com.esmanureral.artframe.showToast

class QuizFragment : Fragment() {

    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!
    private val viewModel: QuizViewModel by viewModels()
    private lateinit var prefs: ArtWorkSharedPreferences
    private var questionIndex = 1

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

        setupObservers()
        setupNextButton()
        initStartQuiz()

        binding.btnCorrectAnswers.setOnClickListener {
            findNavController().navigate(
                QuizFragmentDirections.actionQuizFragmentToResultGameFragment()
            )
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
        }

        viewModel.correctAnswers.observe(viewLifecycleOwner) { correctList ->
            updateScoreUI(correctList.size)
        }

        viewModel.error.observe(viewLifecycleOwner) { isError ->
            if (isError) requireContext().showToast(getString(R.string.error_message))
        }
    }

    private fun setupNextButton() = with(binding) {
        btnNextQuestion.setOnClickListener {
            questionIndex++
            tvQuestionNumber.text = getString(R.string.tv_question, questionIndex)
            prefs.saveQuestionIndex(questionIndex)
            viewModel.loadNewQuestion()
        }
    }

    private fun showQuestion(question: QuizQuestion) = with(binding) {
        val optionButtons = listOf(btnOption1, btnOption2, btnOption3)
        optionButtons.forEach { it.text = "" }

        ivArtwork.loadWithIndicator(
            url = question.imageUrl,
            progressIndicator = progressIndicator,
            errorRes = R.drawable.error,
            onSuccess = {
                optionButtons.forEachIndexed { index, button ->
                    button.text = question.options[index]
                    setupOptionButton(button, question.options[index], question)
                }

                val previousAnswer = viewModel.answeredQuestions[question.artworkId]
                previousAnswer?.let { answer ->
                    val selectedBtn = optionButtons.firstOrNull { it.text == answer }
                    selectedBtn?.let { checkAnswer(it, question) }
                }
            },
            onError = { viewModel.loadNewQuestion() }
        )
    }

    private fun setupOptionButton(button: Button, optionText: String, question: QuizQuestion) {
        resetOptionStyle(button)
        button.text = optionText
        button.setOnClickListener { checkAnswer(button, question) }
    }

    private fun checkAnswer(selectedButton: Button, question: QuizQuestion) = with(binding) {
        val optionButtons = listOf(btnOption1, btnOption2, btnOption3)
        val correctButton = optionButtons.first { it.text == question.correctAnswer }

        viewModel.recordAnswer(question.artworkId, selectedButton.text.toString())

        if (selectedButton == correctButton) {
            highlightAnswer(selectedButton, R.color.correct_answer, R.color.primary)
            viewModel.onCorrectAnswer(question)
        } else {
            highlightAnswer(selectedButton, R.color.wrong_answer, R.color.primary)
            highlightAnswer(correctButton, R.color.correct_answer, R.color.primary)
        }

        disableOptions(optionButtons)
    }

    private fun updateScoreUI(correctCount: Int) = with(binding) {
        tvScore.text = getString(R.string.quiz_score, correctCount)
    }

    private fun disableOptions(optionButtons: List<Button>) {
        optionButtons.forEach { it.isEnabled = false }
    }

    private fun resetOptionStyle(button: Button) {
        button.apply {
            isEnabled = true
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}