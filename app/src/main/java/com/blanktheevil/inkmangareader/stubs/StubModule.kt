package com.blanktheevil.inkmangareader.stubs

import com.blanktheevil.inkmangareader.data.repositories.chapter.ChapterRepository
import com.blanktheevil.inkmangareader.data.repositories.manga.MangaRepository
import com.blanktheevil.inkmangareader.download.DownloadManager
import org.koin.dsl.module

val stubModule = module {
    single<MangaRepository> {
        MangaRepositoryStub()
    }
    single<ChapterRepository> {
        ChapterRepositoryStub()
    }
    single<DownloadManager> {
        DownloadManagerStub()
    }
}