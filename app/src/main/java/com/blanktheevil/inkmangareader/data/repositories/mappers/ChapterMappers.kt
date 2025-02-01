package com.blanktheevil.inkmangareader.data.repositories.mappers

import com.blanktheevil.inkmangareader.data.DataList
import com.blanktheevil.inkmangareader.data.dto.RelationshipType
import com.blanktheevil.inkmangareader.data.dto.objects.ChapterDto
import com.blanktheevil.inkmangareader.data.dto.objects.MangaDto
import com.blanktheevil.inkmangareader.data.dto.objects.ScanlationGroupDto
import com.blanktheevil.inkmangareader.data.dto.responses.GetChapterListResponse
import com.blanktheevil.inkmangareader.data.models.Chapter
import com.blanktheevil.inkmangareader.data.models.ChapterList
import com.blanktheevil.inkmangareader.data.models.ChapterTitle

fun ChapterDto.toChapter(): Chapter {
    val relatedManga = relationships?.getFirstOfType<MangaDto>()
    val relatedMangaId = relatedManga?.id
        ?: relationships?.getFirstOfType(RelationshipType.MANGA)?.id
    val relatedScanGroup = relationships?.getFirstOfType<ScanlationGroupDto>()
    val relatedScanGroupId = relatedScanGroup?.id
        ?: relationships?.getFirstOfType(RelationshipType.SCANLATION_GROUP)?.id

    return Chapter(
        id = this.id,
        title = ChapterTitle(
            primary = primaryTitle,
            short = shortTitle,
            medium = mediumTitle,
        ),
        volume = this.attributes.volume,
        chapter = this.attributes.chapter,
        externalUrl = this.attributes.externalUrl,
        relatedManga = relatedManga?.toManga(),
        relatedMangaId = relatedMangaId,
        relatedScanlationGroup = relatedScanGroup?.toScanlationGroup(),
        relatedScanlationGroupId = relatedScanGroupId,
        isRead = null
    )
}

fun GetChapterListResponse.toChapterList(title: String? = null): ChapterList = DataList(
    items = data.map(ChapterDto::toChapter),
    title = title,
    limit = limit,
    offset = offset,
    total = total,
)

fun ChapterList.setReadMarkers(markerIds: List<String>): ChapterList = ChapterList(
    items = items.map { it.copy(isRead = it.id in markerIds) },
    title = title,
    limit = limit,
    offset = offset,
    total = total
)

/**
 * Creates a string in the format "Vol. 1 Ch. 1 - Chapter Title"
 */
val ChapterDto.primaryTitle: String
    get() {
        val chapterNumber = this.attributes.chapter
        val volumeNumber = this.attributes.volume
        val title = this.attributes.title

        return listOfNotNull(
            listOfNotNull(
                volumeNumber?.let { "Vol. $it" },
                chapterNumber?.let { "Ch. $it" },
            ).joinToString(separator = " "),
            title?.let { it.ifEmpty { null } },
        ).joinToString(separator = " - ")
    }

/**
 * Creates a string in the format "Ch. 1" or "Chapter Title"
 */
val ChapterDto.shortTitle: String
    get() {
        val chapterNumber = this.attributes.chapter
        val title = this.attributes.title

        if (title.isNullOrEmpty()) return "Ch. $chapterNumber"

        return title
    }

/**
 * Creates a string in the format "Ch. 1 - Chapter Title" or "Ch. 1"
 */
val ChapterDto.mediumTitle: String
    get() {
        val chapterNumber = this.attributes.chapter
        val title = this.attributes.title

        return listOfNotNull(
            chapterNumber?.let { "Ch. $it" },
            title?.ifEmpty { null },
        ).joinToString(separator = " - ")
    }