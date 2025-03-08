package com.blanktheevil.inkmangareader.data.api

import com.blanktheevil.inkmangareader.data.ContentFilter
import com.blanktheevil.inkmangareader.data.ContentRatings
import com.blanktheevil.inkmangareader.data.Tags
import com.blanktheevil.inkmangareader.data.dto.requests.MarkChapterReadRequest
import com.blanktheevil.inkmangareader.data.dto.responses.AuthData
import com.blanktheevil.inkmangareader.data.dto.responses.AuthResponse
import com.blanktheevil.inkmangareader.data.dto.responses.GetAuthorListResponse
import com.blanktheevil.inkmangareader.data.dto.responses.GetChapterIdsResponse
import com.blanktheevil.inkmangareader.data.dto.responses.GetChapterListResponse
import com.blanktheevil.inkmangareader.data.dto.responses.GetChapterPagesResponse
import com.blanktheevil.inkmangareader.data.dto.responses.GetChapterResponse
import com.blanktheevil.inkmangareader.data.dto.responses.GetMangaAggregateResponse
import com.blanktheevil.inkmangareader.data.dto.responses.GetMangaCoversResponse
import com.blanktheevil.inkmangareader.data.dto.responses.GetMangaListResponse
import com.blanktheevil.inkmangareader.data.dto.responses.GetMangaResponse
import com.blanktheevil.inkmangareader.data.dto.responses.GetTagsResponse
import com.blanktheevil.inkmangareader.data.dto.responses.GetUserListResponse
import com.blanktheevil.inkmangareader.data.dto.responses.GetUserListsResponse
import com.blanktheevil.inkmangareader.data.dto.responses.GetUserResponse
import com.blanktheevil.inkmangareader.data.dto.responses.Refresh
import com.blanktheevil.inkmangareader.helpers.getCreatedAtSinceString
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface MangaDexApi {
    @POST("auth/login")
    suspend fun authLogin(@Body authData: AuthData): AuthResponse

    @POST("auth/refresh")
    suspend fun authRefresh(@Body refreshToken: Refresh): AuthResponse

    @POST("manga/{id}/read")
    suspend fun markChapterRead(
        @Header("Authorization") authorization: String,
        @Path("id") id: String,
        @Body body: MarkChapterReadRequest,
    )

    @GET("manga/{id}")
    suspend fun getMangaById(
        @Path("id") id: String,
        @Query("includes[]") includes: List<String> = listOf("cover_art"),
    ): GetMangaResponse

    @GET("manga")
    suspend fun getManga(
        @Query("ids[]") ids: List<String>,
        @Query("limit") limit: Int = 32,
        @Query("offset") offset: Int = 0,
        @Query("contentRating[]") contentRating: ContentRatings = ContentFilter.DEFAULT_RATINGS,
        @Query("includes[]") includes: List<String> = listOf("cover_art"),
    ): GetMangaListResponse

    @GET("manga")
    suspend fun getMangaSearch(
        @Query("limit") limit: Int = 5,
        @Query("offset") offset: Int = 0,
        @Query("contentRating[]") contentRating: ContentRatings = ContentFilter.DEFAULT_RATINGS,
        @Query("includes[]") includes: List<String> = listOf("cover_art"),
        @QueryMap order: Map<String, String>,
        @Query("title") title: String? = null,
        @Query("publicationDemographic[]") publicationDemographic: List<String>? = null,
        @Query("status[]") status: List<String>? = null,
        @Query("includedTags[]") includedTags: List<String>? = null,
        @Query("excludedTags[]") excludedTags: List<String>? = null,
        @Query("includedTagsMode") includedTagsMode: Tags.Mode? = null,
        @Query("excludedTagsMode") excludedTagsMode: Tags.Mode? = null,
        @Query("authors[]") authors: List<String>? = null,
        @Query("artists[]") artists: List<String>? = null,
        @Query("year") year: String? = null,
        @Query("hasAvailableChapters") hasAvailableChapters: Boolean = true,
        ): GetMangaListResponse

    @GET("manga")
    suspend fun getMangaPopular(
        @Query("includes[]") includes: List<String> = listOf("cover_art"),
        @Query("order[followedCount]") order: List<String> = listOf("desc"),
        @Query("contentRating[]") contentRating: ContentRatings = ContentFilter.DEFAULT_RATINGS,
        @Query("hasAvailableChapters") hasAvailableChapters: Boolean = true,
        @Query("createdAtSince") createdAtSince: String = getCreatedAtSinceString(),
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
    ): GetMangaListResponse

    @GET("manga")
    suspend fun getMangaRecent(
        @Query("includes[]") includes: List<String> = listOf("cover_art"),
        @Query("order[latestUploadedChapter]") order: List<String> = listOf("desc"),
        @Query("contentRating[]") contentRating: ContentRatings = ContentFilter.DEFAULT_RATINGS,
        @Query("hasAvailableChapters") hasAvailableChapters: Boolean = true,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
    ): GetMangaListResponse

    @GET("user/follows/manga")
    suspend fun getFollowsList(
        @Header("Authorization") authorization: String,
        @Query("limit") limit: Int = 10,
        @Query("offset") offset: Int = 0,
        @Query("includes[]") includes: List<String> = listOf("cover_art")
    ): GetMangaListResponse

    @GET("user/follows/manga/feed")
    suspend fun getFollowsChapterFeed(
        @Header("Authorization") authorization: String,
        @Query("limit") limit: Int = 15,
        @Query("offset") offset: Int = 0,
        @Query("translatedLanguage[]") translatedLanguage: List<String> = listOf("en"),
        @Query("order[readableAt]") order: List<String> = listOf("desc"),
        @Query("includes[]") includes: List<String> = listOf("scanlation_group", "manga"),
    ): GetChapterListResponse

    @GET("user/follows/manga/{id}")
    suspend fun getIsUserFollowingManga(
        @Header("Authorization") authorization: String,
        @Path("id") id: String,
    ): Any

    @GET("manga/{id}/feed")
    suspend fun getMangaFeed(
        @Path("id") id: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("includes[]") includes: List<String> = listOf("scanlation_group"),
        @Query("translatedLanguage[]") translatedLanguage: List<String> = listOf("en"),
        @Query("order[volume]") orderVolume: List<String> = listOf("desc"),
        @Query("order[chapter]") orderChapter: List<String> = listOf("desc"),
        @Query("contentRating[]") contentRating: ContentRatings = ContentFilter.DEFAULT_RATINGS,
    ): GetChapterListResponse

    @GET("cover")
    suspend fun getMangaCovers(
        @Query("manga[]") manga: List<String>,
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0,
        @Query("order[volume]") order: List<String> = listOf("desc"),
    ): GetMangaCoversResponse

    @GET("chapter/{id}")
    suspend fun getChapter(
        @Path("id") id: String,
        @Query("includes[]") includes: List<String> = listOf("scanlation_group", "manga"),
    ): GetChapterResponse

    @GET("chapter")
    suspend fun getChapterList(
        @Query("ids[]") ids: List<String>,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("order[chapter]") order: List<String> = listOf("desc"),
        @Query("includes[]") includes: List<String> = listOf("scanlation_group"),
    ): GetChapterListResponse

    /**
     * Important for the reader so you can go to the next chapter without issues
     */
    @GET("manga/{id}/aggregate")
    suspend fun getMangaAggregate(
        @Path("id") id: String,
        @Query("translatedLanguage[]") translatedLanguage: List<String> = listOf("en"),
    ): GetMangaAggregateResponse

    @GET("at-home/server/{chapterId}")
    suspend fun getChapterPages(
        @Path("chapterId") chapterId: String,
    ): GetChapterPagesResponse

    @GET("manga/read")
    suspend fun getReadChapterIdsByMangaIds(
        @Header("Authorization") authorization: String,
        @Query("ids[]") ids: List<String>,
    ): GetChapterIdsResponse

    /**
     * Maybe just include this with the manga queries
     */
    @GET("manga/tag")
    suspend fun getAllTags(): GetTagsResponse

    @GET("author")
    suspend fun getAuthorList(
        @Query("name") name: String,
        @Query("limit") limit: Int = 10,
    ): GetAuthorListResponse

    @GET("author")
    suspend fun getAuthorList(
        @Query("ids[]") ids: List<String>,
        @Query("limit") limit: Int = 10,
    ): GetAuthorListResponse

    @GET("user/{id}")
    suspend fun getUserInfo(
        @Path("id") id: String,
    ): GetUserResponse

    @GET("user/me")
    suspend fun getCurrentUserInfo(
        @Header("Authorization") authorization: String,
    ): GetUserResponse

    @POST("manga/{id}/follow")
    suspend fun followManga(
        @Header("Authorization") authorization: String,
        @Path("id") id: String,
    ): Any

    @DELETE("manga/{id}/follow")
    suspend fun unfollowManga(
        @Header("Authorization") authorization: String,
        @Path("id") id: String,
    ): Any

    @GET("user/list")
    suspend fun getCurrentUserLists(
        @Header("Authorization") authorization: String,
        @Query("limit") limit: Int = 20,
    ): GetUserListsResponse

    @GET("user/{id}/list")
    suspend fun getUserLists(
        @Path("id") id: String,
    ): GetUserListsResponse

    @POST("manga/{id}/list/{listId}")
    suspend fun addMangaToList(
        @Header("Authorization") authorization: String,
        @Path("id") id: String,
        @Path("listId") listId: String,
    )

    @DELETE("manga/{id}/list/{listId}")
    suspend fun removeMangaFromList(
        @Header("Authorization") authorization: String,
        @Path("id") id: String,
        @Path("listId") listId: String,
    )

    @GET("list/{id}")
    suspend fun getListById(
        @Header("Authorization") authorization: String,
        @Path("id") listId: String,
        @Query("includes[]") includes: List<String> = listOf("user"),
    ): GetUserListResponse
}