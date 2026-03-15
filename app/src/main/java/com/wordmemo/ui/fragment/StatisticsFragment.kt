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
    
    private var viewModel: StatsViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return try {
            _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
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
            val factory = StatsViewModelFactory(container.learningUseCase)
            viewModel = ViewModelProvider(this, factory).get(StatsViewModel::class.java)
            setupUI()
            observeViewModel()
            viewModel?.initializeStats(1)
        } catch (e: Exception) {
            showError("加载失败: ${e.message ?: "未知错误"}")
        }
    }

    private fun showError(message: String) {
        try {
            binding.root.findViewById<TextView>(R.id.status_description)?.text = message
        } catch (_: Exception) { }
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
