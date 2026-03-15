package com.wordmemo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.wordmemo.R
import com.wordmemo.WordMemoApplication
import com.wordmemo.databinding.FragmentStatisticsBinding
import com.wordmemo.ui.viewmodel.StatsViewModel
import com.wordmemo.ui.viewmodel.StatsViewModelFactory

/**
 * 统计页面 Fragment
 * 
 * 显示学习统计信息，包括今日学习数、复习数、连续学习天数等
 */
class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: StatsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupUI()
        observeViewModel()
        
        // 初始化统计（词库 ID 为 1，实际应该从参数传入）
        viewModel.initializeStats(1)
    }

    private fun setupViewModel() {
        val app = requireContext().applicationContext as? WordMemoApplication
            ?: throw IllegalStateException("Application 必须是 WordMemoApplication，请检查 AndroidManifest")
        val factory = StatsViewModelFactory(app.appContainer.learningUseCase)
        viewModel = ViewModelProvider(this, factory).get(StatsViewModel::class.java)
    }

    private fun setupUI() {
        // 设置刷新按钮
        binding.root.findViewById<Button>(R.id.btn_refresh)?.setOnClickListener {
            // viewModel.refreshStatistics()
            Toast.makeText(context, "已刷新统计", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeViewModel() {
        // 观察今日学习数
        // viewModel.todayLearningCount.observe(viewLifecycleOwner) { count ->
        //     binding.root.findViewById<TextView>(R.id.today_learning_count)?.text = count.toString()
        // }

        // 观察今日复习数
        // viewModel.todayReviewCount.observe(viewLifecycleOwner) { count ->
        //     binding.root.findViewById<TextView>(R.id.today_review_count)?.text = count.toString()
        // }

        // 观察连续天数
        // viewModel.consecutiveDays.observe(viewLifecycleOwner) { days ->
        //     binding.root.findViewById<TextView>(R.id.consecutive_days)?.text = days.toString()
        // }

        // 观察学习进度
        // viewModel.learningProgress.observe(viewLifecycleOwner) { progress ->
        //     binding.root.findViewById<ProgressBar>(R.id.learning_progress)?.progress = progress
        //     binding.root.findViewById<TextView>(R.id.progress_percentage)?.text = "$progress%"
        // }

        // 观察加载状态
        // viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
        //     // 显示/隐藏加载指示器
        // }

        // 观察错误消息
        // viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
        //     if (message.isNotEmpty()) {
        //         Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        //         viewModel.clearErrorMessage()
        //     }
        // }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
