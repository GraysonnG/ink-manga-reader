package com.blanktheevil.inkmangareader.data.api

import com.blanktheevil.inkmangareader.data.dto.responses.GetSeasonalDataResponse
import retrofit2.http.GET

interface GithubApi {
    @GET("mangadex-seasonal/seasonal-list.json")
    suspend fun getSeasonalData(): GetSeasonalDataResponse
}