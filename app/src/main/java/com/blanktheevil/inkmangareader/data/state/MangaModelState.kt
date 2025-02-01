package com.blanktheevil.inkmangareader.data.state

import com.blanktheevil.inkmangareader.data.Either
import com.blanktheevil.inkmangareader.data.models.Manga
import kotlinx.coroutines.flow.StateFlow

typealias MangaStateFlow = StateFlow<Either<Manga>>