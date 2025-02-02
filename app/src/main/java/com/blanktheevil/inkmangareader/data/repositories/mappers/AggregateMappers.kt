package com.blanktheevil.inkmangareader.data.repositories.mappers

import com.blanktheevil.inkmangareader.data.dto.responses.GetMangaAggregateResponse

fun GetMangaAggregateResponse.toLinkedChapters(): List<LinkedChapter> =
    volumes.values
        .flatMap { it.chapters.entries.map { ch -> Pair(it.volume, ch) } }
        .let { data -> data.mapIndexed { index, (volume, ch) ->
            val prevId = data.getOrNull(index + 1)?.second?.value?.id
            val nextId = data.getOrNull(index - 1)?.second?.value?.id

            LinkedChapter(
                volume = volume,
                chapter = ch.value.chapter,
                id = ch.value.id,
                others = ch.value.others,
                prevId = prevId,
                nextId = nextId,
            )
        } }
        .distinctBy { it.chapter }

data class LinkedChapter(
    val volume: String,
    val chapter: String,
    val id: String,
    val others: List<String>,
    val nextId: String?,
    val prevId: String?,
)

/**
 * Gets the current chapter given the id
 */
fun List<LinkedChapter>.currentChapter(id: String): LinkedChapter? =
    this.firstOrNull { it.id == id || id in it.others }

/**
 * Gets the next chapter given the current chapter.
 */
fun List<LinkedChapter>.nextChapter(currentChapter: LinkedChapter): LinkedChapter? =
    this.firstOrNull { it.id == currentChapter.nextId || currentChapter.nextId in it.others }

/**
 * Gets the previous chapter given the current chapter.
 */
fun List<LinkedChapter>.prevChapter(currentChapter: LinkedChapter): LinkedChapter? =
    this.firstOrNull { it.id == currentChapter.prevId || currentChapter.prevId in it.others }

