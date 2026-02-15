package com.blanktheevil.inkmangareader.data.builders

import com.blanktheevil.inkmangareader.data.Either
import com.blanktheevil.inkmangareader.data.api.MangaDexApi
import com.blanktheevil.inkmangareader.data.error
import com.blanktheevil.inkmangareader.data.models.Chapter
import com.blanktheevil.inkmangareader.data.repositories.mappers.toChapter
import com.blanktheevil.inkmangareader.data.success

class ChapterBuilder(
    private val mangaDexApi: MangaDexApi,
) {
    suspend fun build(chapterId: String): Either<Chapter> {
        return try {
            success(mangaDexApi.getChapter(id = chapterId)
                .data.toChapter())
        } catch (e: Exception) {
            error(e)
        }
    }
}
