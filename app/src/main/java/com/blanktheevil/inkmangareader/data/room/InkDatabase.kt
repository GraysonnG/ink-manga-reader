package com.blanktheevil.inkmangareader.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.blanktheevil.inkmangareader.data.room.dao.BookmarkDao
import com.blanktheevil.inkmangareader.data.room.dao.ChapterDao
import com.blanktheevil.inkmangareader.data.room.dao.ListDao
import com.blanktheevil.inkmangareader.data.room.dao.MangaDao
import com.blanktheevil.inkmangareader.data.room.models.BookmarkModel
import com.blanktheevil.inkmangareader.data.room.models.ChapterModel
import com.blanktheevil.inkmangareader.data.room.models.ListModel
import com.blanktheevil.inkmangareader.data.room.models.MangaModel
import com.blanktheevil.inkmangareader.data.room.temp.ModelStateDao
import com.blanktheevil.inkmangareader.data.room.temp.ModelStateModel
import com.blanktheevil.inkmangareader.download.room.DownloadDao
import com.blanktheevil.inkmangareader.download.room.DownloadModel

@Database(
    version = 4,
    exportSchema = true,
    entities = [
        ChapterModel::class,
        DownloadModel::class,
        ListModel::class,
        MangaModel::class,
        ModelStateModel::class,
        BookmarkModel::class,
    ],
)
@TypeConverters(Converters::class)
abstract class InkDatabase : RoomDatabase() {
    abstract fun mangaDao(): MangaDao
    abstract fun chapterDao(): ChapterDao
    abstract fun listDao(): ListDao
    abstract fun modelStateDao(): ModelStateDao
    abstract fun downloadDao(): DownloadDao
    abstract fun bookmarkDao(): BookmarkDao

    companion object {
        const val NAME = "ink-database-v2"
    }
}