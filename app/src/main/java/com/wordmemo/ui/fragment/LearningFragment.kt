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
import com.wordmemo.data.entity.Word
import com.wordmemo.WordMemoApplication
import com.wordmemo.databinding.FragmentLearningBinding
import com.wordmemo.ui.viewmodel.LearnViewModel
import com.wordmemo.ui.viewmodel.LearnViewModelFactory

/**
 * 学习页面 Fragment
 * 
 * 完全集成学习流程与 UI，实现完整的学习工作流
 */
class LearningFragment : Fragment() {

    private var _binding: FragmentLearningBinding? = null
    private val binding get() = _binding!!
    
    private var viewModel: LearnViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return try {
            _binding = FragmentLearningBinding.inflate(inflater, container, false)
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
            val factory = LearnViewModelFactory(container.learningUseCase)
            viewModel = ViewModelProvider(this, factory).get(LearnViewModel::class.java)
            setupUI()
            observeViewModel()
            viewModel?.initializeLearning(1)
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
        // 设置卡片点击事件（翻转显示释义）
        binding.root.findViewById<CardView>(R.id.word_card)?.setOnClickListener {
            viewModel?.flipCard()
        }

        // 设置反馈按钮（按产品文档：quality 0-5，SM-2 算法）
        setupFeedbackButtons()
        
        // 设置导航按钮
        setupNavigationButtons()
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

    private fun setupNavigationButtons() {
        // TODO: 添加上一个/下一个/跳过按钮
    }

    private fun observeViewModel() {
        // 观察当前单词 - 在 tv_content 中展示（未翻转仅显示单词，翻转后显示释义）
        viewModel?.currentWord?.observe(viewLifecycleOwner) { word ->
            updateWordDisplay(word, viewModel?.isCardFlipped?.value ?: false)
        }
        viewModel?.isCardFlipped?.observe(viewLifecycleOwner) { flipped ->
            updateWordDisplay(viewModel?.currentWord?.value, flipped)
        }

        // 观察反馈消息
        viewModel?.feedbackMessage?.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrEmpty()) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                // 词库为空时在主区域显示提示
                if (message == "词库中没有单词") {
                    binding.root.findViewById<TextView>(R.id.tv_content)?.text =
                        "词库中没有单词\n\n请先在「词库」中添加单词到学习列表"
                }
                viewModel?.clearFeedbackMessage()
            }
        }

        // 观察学习完成状态（仅当有单词且学完时更新，词库为空时由 feedbackMessage 处理）
        viewModel?.isLearningComplete?.observe(viewLifecycleOwner) { isComplete ->
            if (isComplete && (viewModel?.getTotalWords() ?: 0) > 0) {
                val tv = binding.root.findViewById<TextView>(R.id.tv_content)
                val msg = viewModel?.feedbackMessage?.value
                tv?.text = msg?.takeIf { it.isNotEmpty() } ?: "今日学习已完成！"
            }
        }
    }

    private fun updateWordDisplay(word: Word?, flipped: Boolean) {
        val tv = binding.root.findViewById<TextView>(R.id.tv_content)
        tv?.text = when {
            word == null -> "待学习单词将在此显示"
            flipped -> "${word.content}\n\n${word.translation}"
            else -> word.content + "\n\n（点击卡片查看释义）"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
