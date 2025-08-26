package com.esmanureral.artframe.presentation.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.esmanureral.artframe.R
import com.esmanureral.artframe.data.network.QuizQuestion
import com.esmanureral.artframe.databinding.FragmentQuizBinding
import com.esmanureral.artframe.loadWithIndicator
import com.esmanureral.artframe.showToast

class QuizFragment : Fragment() {

    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!
    private val viewModel: QuizViewModel by viewModels()
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

        setupObservers()
        setupNextButton()

        viewModel.startQuiz()
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

    private fun setupNextButton() {
        binding.btnNextQuestion.setOnClickListener {
            questionIndex++
            binding.tvQuestionNumber.text = getString(R.string.tv_question, questionIndex)
            viewModel.loadNewQuestion()
        }
    }

    private fun showQuestion(question: QuizQuestion) {
        loadArtworkImage(question.imageUrl)

        val optionButtons = listOf(binding.btnOption1, binding.btnOption2, binding.btnOption3)
        optionButtons.forEachIndexed { index, button ->
            setupOptionButton(button, question.options[index], question)
        }
    }

    private fun loadArtworkImage(imageUrl: String) {
        binding.ivArtwork.loadWithIndicator(
            url = imageUrl,
            progressIndicator = binding.progressIndicator,
            errorRes = R.drawable.error
        )
    }

    private fun setupOptionButton(button: Button, optionText: String, question: QuizQuestion) {
        resetOptionStyle(button)
        button.text = optionText
        button.setOnClickListener { checkAnswer(button, question) }
    }

    private fun checkAnswer(selectedButton: Button, question: QuizQuestion) {
        val optionButtons = listOf(binding.btnOption1, binding.btnOption2, binding.btnOption3)
        val correctButton = optionButtons.first { it.text == question.correctAnswer }

        if (selectedButton == correctButton) {
            highlightAnswer(selectedButton, R.color.correct_answer, R.color.primary)
            viewModel.onCorrectAnswer(question)
        } else {
            highlightAnswer(selectedButton, R.color.wrong_answer, R.color.primary)
            highlightAnswer(correctButton, R.color.correct_answer, R.color.primary)
        }

        disableOptions(optionButtons)
    }

    private fun updateScoreUI(correctCount: Int) {
        binding.tvScore.text = getString(R.string.quiz_score, correctCount)
        binding.tvCorrect.text = getString(R.string.tv_correct, correctCount)
    }

    private fun disableOptions(optionButtons: List<Button>) {
        optionButtons.forEach { it.isEnabled = false }
    }

    private fun resetOptionStyle(button: Button) {
        button.apply {
            isEnabled = true
            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.button_next_color))
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
