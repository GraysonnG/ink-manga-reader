package com.blanktheevil.inkmangareader.data.state

import com.blanktheevil.inkmangareader.data.Either
import com.blanktheevil.inkmangareader.data.models.Manga
import com.blanktheevil.inkmangareader.data.repositories.manga.MangaRepository
import com.blanktheevil.inkmangareader.data.room.dao.MangaDao
import com.blanktheevil.inkmangareader.data.wrap
import kotlinx.coroutines.flow.StateFlow

typealias MangaStateFlow = StateFlow<Either<Manga>>