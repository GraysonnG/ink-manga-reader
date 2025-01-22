package com.blanktheevil.inkmangareader.stubs

import com.blanktheevil.inkmangareader.data.models.Manga
import com.blanktheevil.inkmangareader.data.models.MangaList
import java.util.UUID

object StubData {
    fun manga(
        title: String = "The Stub Manga!"
    ): Manga = Manga(
        id = UUID.randomUUID().toString(),
        coverArt = "coverArt.jpg",
        description = "This is a Stub Manga",
        tags = listOf("Romance", "Action", "Shounen"),
        title = title
    )

    fun mangaList(
        title: String = "The Stub Manga List",
        length: Int
    ): MangaList = MangaList(
        items = List<Manga>(size = length) {
            manga(
                title = "Stub Manga $it"
            )
        }
    )
}