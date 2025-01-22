package com.blanktheevil.inkmangareader.data.repositories.mappers

import com.blanktheevil.inkmangareader.data.DataList
import com.blanktheevil.inkmangareader.data.dto.objects.CoverArtDto
import com.blanktheevil.inkmangareader.data.dto.objects.MangaDto
import com.blanktheevil.inkmangareader.data.dto.responses.GetMangaListResponse
import com.blanktheevil.inkmangareader.data.models.Manga
import com.blanktheevil.inkmangareader.data.models.MangaList

fun MangaDto.toManga(): Manga = Manga(
    id = this.id,
    coverArt = this.getCoverImageUrl(),
    title = this.title,
    description = this.description,
    tags = this.tags
)

fun GetMangaListResponse.toMangaLists(title: String? = null): MangaList = DataList(
    items = data.map(MangaDto::toManga),
    title = title,
    limit = limit,
    offset = offset,
    total = total
)

fun List<MangaDto>.toMangaLists(): MangaList = DataList(
    items = map(MangaDto::toManga),
    limit = this.size
)

val MangaDto.title: String
    get() {
        return this.attributes.title["en"] ?: this.attributes.title.values.firstOrNull()
        ?: "Could not find title."
    }

val MangaDto.description: String
    get() {
        return this.attributes.description?.get("en")
            ?: this.attributes.description?.values?.firstOrNull()
            ?: "Could not find description for this manga."
    }

val MangaDto.tags: List<String>
    get() {
        return this.attributes.tags?.mapNotNull {
            it.attributes.name["en"] ?: it.attributes.name.values.firstOrNull()
        } ?: emptyList()
    }

fun MangaDto.getCoverImageUrl(): String? {
    val fileName = relationships
        ?.getFirstOfType<CoverArtDto>()
        ?.attributes
        ?.fileName ?: return null

    return "https://uploads.mangadex.org/covers/${this.id}/$fileName.256.jpg"
}