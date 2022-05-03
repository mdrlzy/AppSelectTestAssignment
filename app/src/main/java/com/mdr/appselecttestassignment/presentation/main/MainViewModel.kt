package com.mdr.appselecttestassignment.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mdr.appselecttestassignment.domain.Movie
import com.mdr.appselecttestassignment.domain.MovieRepo
import com.mdr.appselecttestassignment.domain.NoInternetException
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

sealed class MainState {
    object Loading : MainState()

    class Movies(
        val movies: List<Movie>,
        val hasMore: Boolean
    ) : MainState()

    object NoInternet : MainState()
    object UnknownError : MainState()
}

sealed class MainSideEffect {
    object ClearLoadMore : MainSideEffect()
    object ToastNoInternet : MainSideEffect()
    object ToastUnknownError : MainSideEffect()
}

class MainViewModel(private val movieRepo: MovieRepo) : ViewModel(),
    ContainerHost<MainState, MainSideEffect> {

    override val container: Container<MainState, MainSideEffect> =
        container(MainState.Loading)

    init {
        intent {
            val result = movieRepo.fetch()
            result.onSuccess { pagin ->
                reduce {
                    MainState.Movies(pagin.results, pagin.hasMore)
                }
            }
            result.onFailure { e ->
                reduce {
                    when (e) {
                        is NoInternetException -> MainState.NoInternet
                        else -> MainState.UnknownError
                    }
                }
            }

        }
    }

    fun onLoadMore() = intent {
        val moviesState = state as MainState.Movies
        if (!moviesState.hasMore) {
            postSideEffect(MainSideEffect.ClearLoadMore)
            return@intent
        }
        val result = movieRepo.fetch(moviesState.movies.size)

        result.onSuccess { pagin ->
            val movies = moviesState.movies + pagin.results
            reduce {
                MainState.Movies(movies, pagin.hasMore)
            }
        }
        result.onFailure { e ->
            when (e) {
                is NoInternetException -> postSideEffect(MainSideEffect.ToastNoInternet)
                else -> postSideEffect(MainSideEffect.ToastUnknownError)
            }
        }

    }
}

class MainViewModelFactory @Inject constructor(private val movieRepo: MovieRepo) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(movieRepo) as T
    }
}