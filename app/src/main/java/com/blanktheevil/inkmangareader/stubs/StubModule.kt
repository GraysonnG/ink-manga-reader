package com.blanktheevil.inkmangareader.stubs

import com.blanktheevil.inkmangareader.data.repositories.manga.MangaRepository
import org.koin.dsl.module

val stubModule = module {
    single<MangaRepository> {
        MangaRepositoryStub()
    }
}