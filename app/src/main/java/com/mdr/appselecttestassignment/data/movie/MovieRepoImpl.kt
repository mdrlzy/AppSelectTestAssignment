package com.mdr.appselecttestassignment.data.movie

import com.mdr.appselecttestassignment.domain.Movie
import com.mdr.appselecttestassignment.domain.MovieRepo
import com.mdr.appselecttestassignment.domain.NetworkStatus
import com.mdr.appselecttestassignment.domain.NoInternetException
import com.mdr.appselecttestassignment.domain.Pagination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MovieRepoImpl(
    private val remote: MovieRemoteDataSource,
    private val networkStatus: NetworkStatus
) : MovieRepo {

    override suspend fun fetch(offset: Int): Result<Pagination<Movie>> =
        withContext(Dispatchers.IO) {
            if (networkStatus.isOnline()) {
                remote.fetchMovies(offset)
            } else {
                Result.failure(NoInternetException())
            }
        }
}