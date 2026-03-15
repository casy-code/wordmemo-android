package com.wordmemo.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.wordmemo.R
import com.wordmemo.data.entity.Word

/**
 * 今日学习/复习单词列表 BottomSheet
 */
class TodayWordsBottomSheet : BottomSheetDialogFragment() {

    var words: List<Word> = emptyList()
        set(value) {
            field = value
            adapter?.submitList(value)
        }

    var onWordClick: ((Word) -> Unit)? = null

    private var adapter: TodayWordsAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.bottom_sheet_today_words, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recycler = view.findViewById<RecyclerView>(R.id.recycler_today_words)
        adapter = TodayWordsAdapter(words) { word ->
            onWordClick?.invoke(word)
        }
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter
        adapter?.submitList(words)
    }

    private class TodayWordsAdapter(
        private var items: List<Word>,
        private val onItemClick: (Word) -> Unit
    ) : RecyclerView.Adapter<TodayWordsAdapter.ViewHolder>() {

        fun submitList(list: List<Word>) {
            items = list
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_today_word, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val word = items[position]
            holder.bind(word)
            holder.itemView.setOnClickListener { onItemClick(word) }
        }

        override fun getItemCount() = items.size

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val tvContent = itemView.findViewById<TextView>(R.id.tv_word_content)
            private val tvTranslation = itemView.findViewById<TextView>(R.id.tv_word_translation)

            fun bind(word: Word) {
                tvContent.text = word.content
                tvTranslation.text = word.translation
            }
        }
    }
}
