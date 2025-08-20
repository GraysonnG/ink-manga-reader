package com.blanktheevil.inkmangareader.data.repositories


sealed class MangaListRequest(
    val type: String
) {
    data class Generic(
        val data: List<String>,
        val name: String? = null,
    ) : MangaListRequest(type = data.joinToString { it.takeLast(4) } + name.orEmpty() )
    data object Popular : MangaListRequest(type = "Popular")
    data object Recent : MangaListRequest(type = "Recent")
    data object Seasonal : MangaListRequest(type = "Seasonal")
    data class Follows(val userId: String) : MangaListRequest(type = "Follows-$userId")
    data class Search(
        val params: SearchParams,
    ) : MangaListRequest(type = params.toString())
    data class UserList(
        val listId: String,
    ) : MangaListRequest(type = "UserList-$listId")
}
