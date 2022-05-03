package com.mdr.appselecttestassignment.presentation.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.mdr.appselecttestassignment.R
import com.mdr.appselecttestassignment.databinding.ActivityMainBinding
import com.mdr.appselecttestassignment.databinding.ItemMovieBinding
import com.mdr.appselecttestassignment.databinding.ItemProgressBinding
import com.mdr.appselecttestassignment.domain.Movie
import com.mdr.appselecttestassignment.presentation.App
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.mikepenz.fastadapter.scroll.EndlessRecyclerOnScrollListener
import org.orbitmvi.orbit.viewmodel.observe
import javax.inject.Inject

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private val binding by viewBinding(ActivityMainBinding::bind)

    @Inject
    lateinit var factory: MainViewModelFactory
    private val viewModel: MainViewModel by viewModels {
        factory
    }

    private val moviesAdapter = ItemAdapter<MovieItem>()
    private val footerAdapter = ItemAdapter<ProgressItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.instance.appComponent.inject(this)
        initUI()
        viewModel.observe(this, ::render, ::handleSideEffect)
    }

    private fun initUI() = with(binding) {
        rv.layoutManager = LinearLayoutManager(this@MainActivity)
        rv.adapter = FastAdapter.with(listOf(moviesAdapter, footerAdapter))
        rv.itemAnimator = null
        rv.addOnScrollListener(object :
            EndlessRecyclerOnScrollListener(footerAdapter) {
            override fun onLoadMore(currentPage: Int) {
                footerAdapter.add(ProgressItem())
                viewModel.onLoadMore()
            }
        })
    }

    private fun render(state: MainState) {
        when (state) {
            MainState.Loading -> {
            }
            is MainState.Movies -> binding.apply {
                footerAdapter.clear()
                moviesAdapter.set(state.movies.map { MovieItem(it) })
                progress.isVisible = false
                rv.isVisible = true
                tvNoInternet.isVisible = false
                tvSomethingWrong.isVisible = false
            }
            MainState.NoInternet -> binding.apply {
                progress.isVisible = false
                rv.isVisible = false
                tvNoInternet.isVisible = true
                tvSomethingWrong.isVisible = false
            }
            MainState.UnknownError -> binding.apply {
                progress.isVisible = false
                binding.rv.isVisible = false
                binding.tvNoInternet.isVisible = false
                binding.tvSomethingWrong.isVisible = true
            }
        }
    }

    private fun handleSideEffect(effect: MainSideEffect) {
        when (effect) {
            MainSideEffect.ClearLoadMore -> footerAdapter.clear()
            MainSideEffect.ToastNoInternet -> Toast.makeText(
                this,
                R.string.no_internet,
                Toast.LENGTH_SHORT
            ).show()
            MainSideEffect.ToastUnknownError -> Toast.makeText(
                this,
                R.string.no_internet,
                Toast.LENGTH_LONG
            ).show()
        }
    }
}

private data class MovieItem(val movie: Movie) :
    AbstractBindingItem<ItemMovieBinding>() {
    override val type = R.id.fastadapter_movie_item_id

    override fun bindView(binding: ItemMovieBinding, payloads: List<Any>) =
        with(binding) {
            (iv.layoutParams as ConstraintLayout.LayoutParams).dimensionRatio =
                "H,${movie.posterWidth}:${movie.posterHeight}"

            Glide.with(iv.context)
                .load(movie.posterUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(iv)

            tvTitle.text = movie.title
            tvDesc.text = movie.desc
        }

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = ItemMovieBinding.inflate(inflater, parent, false)
}

private class ProgressItem : AbstractBindingItem<ItemProgressBinding>() {
    override val type = R.id.fastadapter_progress_item_id

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = ItemProgressBinding.inflate(inflater, parent, false)
}