package com.blanktheevil.inkmangareader.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.blanktheevil.inkmangareader.data.room.dao.ChapterDao
import com.blanktheevil.inkmangareader.data.room.dao.ListDao
import com.blanktheevil.inkmangareader.data.room.dao.MangaDao
import com.blanktheevil.inkmangareader.data.room.models.ChapterModel
import com.blanktheevil.inkmangareader.data.room.models.ListModel
import com.blanktheevil.inkmangareader.data.room.models.MangaModel
import com.blanktheevil.inkmangareader.data.room.temp.ModelStateDao
import com.blanktheevil.inkmangareader.data.room.temp.ModelStateModel

@Database(
    version = 2,
    exportSchema = true,
    entities = [
        MangaModel::class,
        ChapterModel::class,
        ListModel::class,
        ModelStateModel::class,
    ],
)
@TypeConverters(Converters::class)
abstract class InkDatabase : RoomDatabase() {
    abstract fun mangaDao(): MangaDao
    abstract fun chapterDao(): ChapterDao
    abstract fun listDao(): ListDao
    abstract fun modelStateDao(): ModelStateDao

    companion object {
        const val NAME = "ink-database-v2"
    }
}