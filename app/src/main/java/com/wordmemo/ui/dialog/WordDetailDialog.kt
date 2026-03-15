package com.wordmemo.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.wordmemo.R
import com.wordmemo.data.entity.Word

/**
 * 单词详情对话框：释义、发音、例句
 */
class WordDetailDialog : DialogFragment() {

    companion object {
        private const val ARG_WORD = "word"

        fun newInstance(word: Word): WordDetailDialog {
            return WordDetailDialog().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_WORD, word)
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        @Suppress("DEPRECATION")
        val word = arguments?.getSerializable(ARG_WORD) as? Word ?: return super.onCreateDialog(savedInstanceState)
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_word_detail, null)
        bindView(view, word)
        return AlertDialog.Builder(requireContext(), R.style.Theme_WordMemo)
            .setView(view)
            .setPositiveButton("关闭", null)
            .create()
    }

    private fun bindView(view: View, word: Word) {
        view.findViewById<TextView>(R.id.tv_detail_content).text = word.content
        view.findViewById<TextView>(R.id.tv_detail_phonetic).apply {
            text = if (word.phonetic.isNotBlank()) "[${word.phonetic}]" else ""
            visibility = if (word.phonetic.isNotBlank()) View.VISIBLE else View.GONE
        }
        view.findViewById<TextView>(R.id.tv_detail_translation).text = word.translation
        view.findViewById<TextView>(R.id.tv_detail_example).apply {
            text = word.example.ifBlank { null } ?: "暂无例句"
            visibility = View.VISIBLE
        }
    }
}
