package com.blanktheevil.inkmangareader.data.room

import androidx.room.TypeConverter
import com.blanktheevil.inkmangareader.data.DataList
import com.blanktheevil.inkmangareader.data.models.Chapter
import com.blanktheevil.inkmangareader.data.models.Manga
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class Converters {
    private val moshi = Moshi.Builder().build()

    @TypeConverter
    fun mangaDataListToJson(
        value: DataList<Manga>
    ): String {
        val type = Types.newParameterizedType(DataList::class.java, Manga::class.java)

        return moshi
            .adapter<DataList<Manga>>(type)
            .toJson(value)
    }

    @TypeConverter
    fun jsonToMangaDataList(
        value: String
    ): DataList<Manga>? {
        val type = Types.newParameterizedType(DataList::class.java, Manga::class.java)

        return moshi
            .adapter<DataList<Manga>?>(type)
            .fromJson(value)
    }

    @TypeConverter
    fun mangaToJson(
        value: Manga,
    ): String {
        return moshi
            .adapter(Manga::class.java)
            .toJson(value)
    }

    @TypeConverter
    fun jsonToManga(
        value: String
    ): Manga? {
        return moshi
            .adapter(Manga::class.java)
            .fromJson(value)
    }

    @TypeConverter
    fun chapterToJson(
        value: Chapter,
    ): String {
        return moshi
            .adapter(Chapter::class.java)
            .toJson(value)
    }

    @TypeConverter
    fun jsonToChapter(
        value: String,
    ): Chapter? {
        return moshi
            .adapter(Chapter::class.java)
            .fromJson(value)
    }

    @TypeConverter
    fun stringListToJson(
        value: List<String>,
    ): String {
        return value.joinToString(", ")
    }

    @TypeConverter
    fun jsonToStringList(
        value: String,
    ): List<String> {
        return value.split(", ")
    }

    @TypeConverter
    fun simpleMapToJson(
        value: Map<String, String>,
    ): String {
        return moshi
            .adapter(Map::class.java)
            .toJson(value)
    }

    @TypeConverter
    fun simpleMapFromJson(
        string: String,
    ): Map<String, String> {
        return moshi
            .adapter<Map<String, String>>(Map::class.java)
            .fromJson(string) ?: emptyMap()
    }
}