package io.github.lordraydenmk.superheroesapp.superheroes.superheroslist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.jakewharton.rxbinding3.view.clicks
import io.github.lordraydenmk.superheroesapp.common.presentation.Screen
import io.github.lordraydenmk.superheroesapp.common.setTextResource
import io.github.lordraydenmk.superheroesapp.databinding.SuperheroesScreenBinding
import io.reactivex.Completable
import io.reactivex.Observable

class SuperheroesScreen(container: ViewGroup) : Screen<SuperheroesAction, SuperheroesViewState> {

    private val binding =
        SuperheroesScreenBinding.inflate(LayoutInflater.from(container.context), container)

    private val superheroesAdapter = SuperheroesAdapter()

    init {
        with(binding.rvSuperheroes) {
            layoutManager = GridLayoutManager(context, 2)
            adapter = superheroesAdapter
        }
    }

    override val actions: Observable<SuperheroesAction> = Observable.merge(
        binding.tvError.clicks().map { Refresh },
        superheroesAdapter.actions.map { LoadDetails(it) }
    )

    override fun bind(viewState: SuperheroesViewState): Completable = Completable.fromCallable {
        binding.groupSuperheroesContent.isVisible = viewState is Content
        binding.progressSuperheroes.isVisible = viewState is Loading
        binding.tvError.isVisible = viewState is Problem

        when (viewState) {
            Loading -> {
                // no-op
            }
            is Content -> {
                superheroesAdapter.submitList(viewState.superheroes)
                binding.copyrightLayout.tvCopyright.text = viewState.copyright
            }
            is Problem -> bindErrorView(viewState)
        }
    }

    private fun bindErrorView(viewState: Problem) = with(binding) {
        tvError.setTextResource(viewState.stringId)
        tvError.isClickable = viewState.isRecoverable
    }
}