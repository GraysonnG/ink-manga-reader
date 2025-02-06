package com.blanktheevil.inkmangareader.data.repositories.tags

import com.blanktheevil.inkmangareader.data.Either
import com.blanktheevil.inkmangareader.data.api.MangaDexApi
import com.blanktheevil.inkmangareader.data.models.Tag
import com.blanktheevil.inkmangareader.data.models.toTag
import com.blanktheevil.inkmangareader.data.repositories.makeCall

class TagsRepositoryImpl(
    private val mangaDexApi: MangaDexApi
) : TagsRepository {
    override suspend fun getAllTags(): Either<List<Tag>> = makeCall {
        mangaDexApi.getAllTags().data.map { it.toTag() }
    }
}