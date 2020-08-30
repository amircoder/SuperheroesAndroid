package io.github.lordraydenmk.superheroesapp.superheroes.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.jakewharton.rxbinding3.view.clicks
import io.github.lordraydenmk.superheroesapp.R
import io.github.lordraydenmk.superheroesapp.databinding.SuperheroesScreenBinding
import io.reactivex.Observable

class SuperheroesScreen(container: ViewGroup) {

    private val binding =
        SuperheroesScreenBinding.inflate(LayoutInflater.from(container.context), container)

    private val superheroesAdapter = SuperheroesAdapter()

    init {
        with(binding.rvSuperheroes) {
            layoutManager = GridLayoutManager(context, 2)
            adapter = superheroesAdapter
        }
    }

    val actions: Observable<SuperheroesAction> = binding.tvError.clicks()
        .map { Refresh }

    fun bind(viewState: SuperheroesViewState) {
        binding.groupSuperheroesContent.isVisible = viewState is Content
        binding.progressSuperheroes.isVisible = viewState is Loading
        binding.tvError.isVisible = viewState is Problem

        when (viewState) {
            Loading -> {
                // no-op
            }
            is Content -> {
                superheroesAdapter.submitList(viewState.superheroes)
                binding.tvCopyright.text = viewState.copyright
            }
            is Problem -> bindErrorView(viewState)
        }
    }

    private fun bindErrorView(viewState: Problem) = with(binding) {
        if (viewState.recoverable) {
            val retryText = tvError.resources.getString(R.string.error_retry_text)
            tvError.text = tvError.resources.getString(viewState.stringId, retryText)
        } else {
            tvError.setText(viewState.stringId)
        }
        tvError.isClickable = viewState.recoverable
    }
}