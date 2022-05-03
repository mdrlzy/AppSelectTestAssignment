package com.mdr.appselecttestassignment.data.movie

import com.mdr.appselecttestassignment.domain.Movie
import com.mdr.appselecttestassignment.domain.Pagination

class MovieRemoteDataSource(
    private val api: NYTimesApi,
    private val apiKey: String,
) {
    suspend fun fetchMovies(offset: Int): Result<Pagination<Movie>> = try {
        val responsePagination = api.fetchMovies(apiKey, offset)
        Result.success(Pagination(
            responsePagination.has_more,
            responsePagination.results
                .map { it.toMovie() }
        ))
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }

    private fun ResponseMovie.toMovie() =
        Movie(
            display_title,
            summary_short,
            multimedia.src,
            multimedia.width,
            multimedia.height
        )
}