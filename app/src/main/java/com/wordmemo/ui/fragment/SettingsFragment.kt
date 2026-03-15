package com.wordmemo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.wordmemo.databinding.FragmentSettingsBinding

/**
 * 设置页面 Fragment
 * 
 * 显示应用设置选项，用户可以配置应用参数
 */
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return try {
            _binding = FragmentSettingsBinding.inflate(inflater, container, false)
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
        // TODO: 实现设置页面 UI 逻辑
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
