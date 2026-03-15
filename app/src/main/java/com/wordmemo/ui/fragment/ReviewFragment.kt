package com.wordmemo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.wordmemo.R
import com.wordmemo.WordMemoApplication
import com.wordmemo.data.entity.Word
import com.wordmemo.databinding.FragmentReviewBinding
import com.wordmemo.ui.viewmodel.ReviewViewModel
import com.wordmemo.ui.viewmodel.ReviewViewModelFactory

/**
 * 复习页面 Fragment
 *
 * 显示待复习单词（nextReviewDate <= 当前时间），闪卡式逐个复习，记录反馈后更新 SM-2 计划
 */
class ReviewFragment : Fragment() {

    private var _binding: FragmentReviewBinding? = null
    private val binding get() = _binding!!

    private var viewModel: ReviewViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return try {
            _binding = FragmentReviewBinding.inflate(inflater, container, false)
            binding.root
        } catch (e: Exception) {
            android.widget.TextView(requireContext()).apply {
                text = "页面加载失败"
                setPadding(48, 48, 48, 48)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (_binding == null) return
        try {
            val app = requireContext().applicationContext as? WordMemoApplication
            val container = app?.appContainer
            if (app == null || container == null) {
                showError("应用初始化失败，请重启")
                return
            }
            val factory = ReviewViewModelFactory(container.learningUseCase)
            viewModel = ViewModelProvider(this, factory).get(ReviewViewModel::class.java)
            setupUI()
            observeViewModel()
            viewModel?.initializeReview(1)
        } catch (e: Exception) {
            showError("加载失败: ${e.message ?: "未知错误"}")
        }
    }

    private fun showError(message: String) {
        try {
            binding.root.findViewById<TextView>(R.id.tv_content)?.text = message
        } catch (_: Exception) { }
    }

    private fun setupUI() {
        binding.root.findViewById<CardView>(R.id.word_card)?.setOnClickListener {
            viewModel?.flipCard()
        }
        setupFeedbackButtons()
    }

    private fun setupFeedbackButtons() {
        binding.root.findViewById<Button>(R.id.btn_forgot)?.setOnClickListener {
            viewModel?.recordFeedback(0)
        }
        binding.root.findViewById<Button>(R.id.btn_hard)?.setOnClickListener {
            viewModel?.recordFeedback(1)
        }
        binding.root.findViewById<Button>(R.id.btn_normal)?.setOnClickListener {
            viewModel?.recordFeedback(2)
        }
        binding.root.findViewById<Button>(R.id.btn_good)?.setOnClickListener {
            viewModel?.recordFeedback(4)
        }
        binding.root.findViewById<Button>(R.id.btn_perfect)?.setOnClickListener {
            viewModel?.recordFeedback(5)
        }
    }

    private fun observeViewModel() {
        viewModel?.currentWord?.observe(viewLifecycleOwner) { word ->
            updateWordDisplay(word, viewModel?.isCardFlipped?.value ?: false)
        }
        viewModel?.isCardFlipped?.observe(viewLifecycleOwner) { flipped ->
            updateWordDisplay(viewModel?.currentWord?.value, flipped)
        }

        viewModel?.feedbackMessage?.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrEmpty()) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                if (message == "暂无待复习单词") {
                    binding.root.findViewById<TextView>(R.id.tv_content)?.text =
                        "暂无待复习单词\n\n今日复习已完成，或去「学习」页学习新单词"
                }
                viewModel?.clearFeedbackMessage()
            }
        }

        viewModel?.isReviewComplete?.observe(viewLifecycleOwner) { isComplete ->
            if (isComplete && (viewModel?.getTotalWords() ?: 0) > 0) {
                val tv = binding.root.findViewById<TextView>(R.id.tv_content)
                val msg = viewModel?.feedbackMessage?.value
                tv?.text = msg?.takeIf { it.isNotEmpty() } ?: "今日复习已完成！"
            }
        }
    }

    private fun updateWordDisplay(word: Word?, flipped: Boolean) {
        val tv = binding.root.findViewById<TextView>(R.id.tv_content)
        tv?.text = when {
            word == null -> "待复习单词将在此显示"
            flipped -> buildWordDetailText(word)
            else -> word.content + "\n\n（点击卡片查看释义）"
        }
    }

    private fun buildWordDetailText(word: Word): String {
        val sb = StringBuilder()
        sb.append(word.content)
        if (word.phonetic.isNotBlank()) sb.append("  [${word.phonetic}]")
        sb.append("\n\n").append(word.translation)
        if (word.example.isNotBlank()) sb.append("\n\n例句：\n${word.example}")
        return sb.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
