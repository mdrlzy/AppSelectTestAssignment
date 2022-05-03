package com.mdr.appselecttestassignment.data.movie

import com.google.gson.annotations.Expose
import retrofit2.http.GET
import retrofit2.http.Query

interface NYTimesApi {
    @GET("svc/movies/v2/reviews/all.json")
    suspend fun fetchMovies(
        @Query("api-key") apiKey: String,
        @Query("offset") offset: Int = 0
    ): ResponsePagination
}

class ResponsePagination(
    val has_more: Boolean,
    val results: List<ResponseMovie>
)

class ResponseMultiMedia(
    val src: String,
    val width: Int,
    val height: Int
)

class ResponseMovie(
    val display_title: String,
    val summary_short: String,
    val multimedia: ResponseMultiMedia
)