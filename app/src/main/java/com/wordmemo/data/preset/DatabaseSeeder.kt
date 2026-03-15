package com.wordmemo.data.preset

import com.wordmemo.data.db.dao.WordDao
import com.wordmemo.data.db.dao.WordListItemDao
import com.wordmemo.data.db.dao.WordListDao
import com.wordmemo.data.entity.WordListItem
import kotlinx.coroutines.runBlocking

/**
 * 数据库种子：首次启动时插入 5 个分类共 500 个预设单词
 */
object DatabaseSeeder {

    suspend fun seedIfNeeded(
        wordListDao: WordListDao,
        wordDao: WordDao,
        wordListItemDao: WordListItemDao
    ) {
        if (wordListDao.getWordListCount() > 0) return
        val lists = PresetData.toWordLists()
        val pairs = PresetData.toWordsAndItems()
        wordListDao.insertAll(lists)
        wordDao.insertAll(pairs.map { it.first })
        wordListItemDao.insertAll(pairs.map { (word, listId) ->
            WordListItem(wordId = word.id, listId = listId)
        })
    }
}
