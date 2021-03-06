package io.github.lordraydenmk.superheroesapp.superheroes.superheroslist

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
import io.github.lordraydenmk.superheroesapp.superheroes.superherodetails.SuperheroDetailsFragment
import io.reactivex.android.schedulers.AndroidSchedulers

class SuperheroesFragment : Fragment(R.layout.superheroes_fragment) {

    private val viewModel by viewModels<SuperheroesViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(EffectsObserver(viewModel.effects) { effect ->
            when (effect) {
                is NavigateToDetails ->
                    findNavController().navigate(
                        R.id.action_details,
                        SuperheroDetailsFragment.newBundle(effect.superheroId)
                    )
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val screen = SuperheroesScreen(view as ViewGroup)

        val module: SuperheroesModule = object : SuperheroesModule,
            AppModule by requireActivity().appModule(),
            ViewModelAlgebra<SuperheroesViewState, SuperheroesEffect> by viewModel {}

        with(module) {
            val renderObservable =
                viewState.switchMap {
                    screen.bind(it).toObservable<Unit>()
                        .evalOn(AndroidSchedulers.mainThread())
                }

            program(screen.actions)
                .mergeWith(renderObservable)
                .autoDispose(viewLifecycleOwner)
        }
    }
}