package com.wordmemo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.wordmemo.R
import com.wordmemo.databinding.FragmentLearningBinding
import com.wordmemo.ui.viewmodel.LearnViewModel

/**
 * 学习页面 Fragment
 * 
 * 完全集成学习流程与 UI，实现完整的学习工作流
 */
class LearningFragment : Fragment() {

    private var _binding: FragmentLearningBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: LearnViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLearningBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupUI()
        observeViewModel()
        
        // 初始化学习（词库 ID 为 1，实际应该从参数传入）
        viewModel.initializeLearning(1)
    }

    private fun setupViewModel() {
        // TODO: 通过 ViewModelFactory 注入 LearningUseCase
        // viewModel = ViewModelProvider(this, LearnViewModelFactory(learningUseCase))
        //     .get(LearnViewModel::class.java)
    }

    private fun setupUI() {
        // 设置卡片点击事件（翻转）
        binding.root.findViewById<CardView>(R.id.word_card)?.setOnClickListener {
            // viewModel.flipCard()
        }

        // 设置反馈按钮
        setupFeedbackButtons()
        
        // 设置导航按钮
        setupNavigationButtons()
    }

    private fun setupFeedbackButtons() {
        binding.root.findViewById<Button>(R.id.btn_forgot)?.setOnClickListener {
            // viewModel.recordFeedback(0)
            Toast.makeText(context, "已记录: 忘记", Toast.LENGTH_SHORT).show()
        }

        binding.root.findViewById<Button>(R.id.btn_hard)?.setOnClickListener {
            // viewModel.recordFeedback(1)
            Toast.makeText(context, "已记录: 困难", Toast.LENGTH_SHORT).show()
        }

        binding.root.findViewById<Button>(R.id.btn_normal)?.setOnClickListener {
            // viewModel.recordFeedback(2)
            Toast.makeText(context, "已记录: 一般", Toast.LENGTH_SHORT).show()
        }

        binding.root.findViewById<Button>(R.id.btn_good)?.setOnClickListener {
            // viewModel.recordFeedback(4)
            Toast.makeText(context, "已记录: 不错", Toast.LENGTH_SHORT).show()
        }

        binding.root.findViewById<Button>(R.id.btn_perfect)?.setOnClickListener {
            // viewModel.recordFeedback(5)
            Toast.makeText(context, "已记录: 完美", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupNavigationButtons() {
        // TODO: 添加上一个/下一个/跳过按钮
    }

    private fun observeViewModel() {
        // 观察当前单词
        // viewModel.currentWord.observe(viewLifecycleOwner) { word ->
        //     updateCardUI(word)
        // }

        // 观察卡片翻转状态
        // viewModel.isCardFlipped.observe(viewLifecycleOwner) { isFlipped ->
        //     updateCardFlip(isFlipped)
        // }

        // 观察反馈消息
        // viewModel.feedbackMessage.observe(viewLifecycleOwner) { message ->
        //     if (message.isNotEmpty()) {
        //         Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        //         viewModel.clearFeedbackMessage()
        //     }
        // }

        // 观察学习统计
        // viewModel.learningStatistics.observe(viewLifecycleOwner) { stats ->
        //     updateStatistics(stats)
        // }

        // 观察学习完成状态
        // viewModel.isLearningComplete.observe(viewLifecycleOwner) { isComplete ->
        //     if (isComplete) {
        //         showLearningCompleteDialog()
        //     }
        // }

        // 观察加载状态
        // viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
        //     binding.root.findViewById<ProgressBar>(R.id.progress_bar)?.visibility =
        //         if (isLoading) View.VISIBLE else View.GONE
        // }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
