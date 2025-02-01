package com.blanktheevil.inkmangareader.stubs

import com.blanktheevil.inkmangareader.data.models.Chapter
import com.blanktheevil.inkmangareader.data.models.ChapterList
import com.blanktheevil.inkmangareader.data.models.ChapterTitle
import com.blanktheevil.inkmangareader.data.models.Manga
import com.blanktheevil.inkmangareader.data.models.MangaList
import com.blanktheevil.inkmangareader.data.models.ScanlationGroup
import java.util.UUID

object StubData {
    fun manga(
        title: String = "The Stub Manga!",
        coverArt: String? = null,
    ): Manga = Manga(
        id = UUID.randomUUID().toString(),
        coverArt = coverArt,
        description = "This is a Stub Manga",
        tags = listOf("Romance", "Action", "Shounen"),
        title = title
    )

    fun mangaList(
        title: String? = "The Stub Manga List",
        length: Int
    ): MangaList = MangaList(
        title = title,
        items = List(size = length) {
            manga(
                title = "Stub Manga $it"
            )
        }
    )

    fun chapter(
        title: String = "Stub Chapter",
        chapter: Int? = 1,
        volume: Int? = 1,
        externalUrl: String? = null,
        isRead: Boolean = false,
        scanlationGroupName: String = "Stub ScanlationGroup"
    ): Chapter = Chapter(
        id = UUID.randomUUID().toString(),
        chapter = chapter.toString(),
        volume = volume.toString(),
        externalUrl = externalUrl,
        relatedManga = manga(),
        relatedMangaId = null,
        relatedScanlationGroup = scanlationGroup(
            name = scanlationGroupName
        ),
        relatedScanlationGroupId = null,
        title = ChapterTitle(
            primary = "Vol. $volume Ch. $chapter - $title",
            medium = "Ch. $chapter - $title",
            short = title
        ),
        isRead = isRead,
    )

    fun chapterList(
        title: String = "Stub Chapter List",
        vol: (Int) -> Int? = { it },
        length: Int,
    ): ChapterList = ChapterList(
        items = List(size = length) {
            chapter(
                chapter = it,
                volume = vol(it)
            )
        }
    )

    fun scanlationGroup(
        name: String = "Stub ScanlationGroup",
    ): ScanlationGroup = ScanlationGroup(
        id = UUID.randomUUID().toString(),
        name = name,
        website = "https://google.com"
    )
}