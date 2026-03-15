package com.wordmemo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.wordmemo.databinding.FragmentReviewBinding

/**
 * 复习页面 Fragment
 * 
 * 显示需要复习的单词卡片，用户可以复习已学单词
 */
class ReviewFragment : Fragment() {

    private var _binding: FragmentReviewBinding? = null
    private val binding get() = _binding!!

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
        setupUI()
    }

    private fun setupUI() {
        // TODO: 实现复习页面 UI 逻辑
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
