package com.blanktheevil.inkmangareader.data.repositories.mappers

import com.blanktheevil.inkmangareader.data.dto.responses.GetChapterPagesResponse

fun GetChapterPagesResponse.convertToUrls(dataSaver: Boolean): List<String> {
    val imageQuality = if (dataSaver && chapter.dataSaver != null) "data-saver" else "data"
    return (if (dataSaver && chapter.dataSaver != null) chapter.dataSaver else chapter.data)?.map {
        "$baseUrl/$imageQuality/${chapter.hash}/$it"
    } ?: emptyList()
}
