package io.github.lordraydenmk.superheroesapp.superheroes.superherodetails

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import io.github.lordraydenmk.superheroesapp.AppModule
import io.github.lordraydenmk.superheroesapp.R
import io.github.lordraydenmk.superheroesapp.appModule
import io.github.lordraydenmk.superheroesapp.common.presentation.ViewModelAlgebra
import io.github.lordraydenmk.superheroesapp.common.rx.EffectsObserver
import io.github.lordraydenmk.superheroesapp.common.rx.autoDispose
import io.github.lordraydenmk.superheroesapp.common.rx.evalOn
import io.github.lordraydenmk.superheroesapp.superheroes.domain.SuperheroId
import io.reactivex.android.schedulers.AndroidSchedulers

class SuperheroDetailsFragment : Fragment(R.layout.superhero_details_fragment) {

    private val superheroId: Long by lazy(LazyThreadSafetyMode.NONE) {
        val id = requireArguments().getLong(EXTRA_SUPERHERO_ID, -1)
        check(id != -1L) { "Please use newBundle() for creating the arguments" }
        id
    }

    private val viewModel by viewModels<SuperheroDetailsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(EffectsObserver(viewModel.effects) { effect ->
            when (effect) {
                NavigateUp -> findNavController().navigateUp()
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val screen = SuperheroDetailsScreen(view as ViewGroup, superheroId)

        val module = object : SuperheroDetailsModule,
            AppModule by requireActivity().appModule(),
            ViewModelAlgebra<SuperheroDetailsViewState, SuperheroDetailsEffect> by viewModel {}

        with(module) {
            val render = viewState.switchMap {
                screen.bind(it)
                    .toObservable<Unit>()
                    .evalOn(AndroidSchedulers.mainThread())
            }

            program(superheroId, screen.actions)
                .mergeWith(render)
                .autoDispose(viewLifecycleOwner)
        }
    }

    companion object {

        private const val EXTRA_SUPERHERO_ID = "EXTRA_SUPERHERO_ID"

        fun newBundle(superheroId: SuperheroId): Bundle =
            Bundle().apply {
                putLong(EXTRA_SUPERHERO_ID, superheroId)
            }
    }
}