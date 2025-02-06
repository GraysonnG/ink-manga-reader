package com.blanktheevil.inkmangareader.data.repositories.tags

import com.blanktheevil.inkmangareader.data.Either
import com.blanktheevil.inkmangareader.data.models.Tag

interface TagsRepository {
    suspend fun getAllTags(): Either<List<Tag>>
}