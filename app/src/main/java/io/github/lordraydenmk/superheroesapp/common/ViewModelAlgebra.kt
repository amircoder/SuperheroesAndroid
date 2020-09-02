package io.github.lordraydenmk.superheroesapp.common

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

/**
 * Defines the algebra (set of operations) for a ViewModel
 *
 * It exposes exactly one [Observable] with type [VS] describing the state of the UI
 * It exposes exactly one [Observable] with type [E] for Effects (navigation, show scnackbar etc..)
 *
 * Exposes methods to update the State and Effects
 *
 * Exposes a method to add [Disposable] and keep track of it
 *
 * @see [JetpackViewModel]
 */
interface ViewModelAlgebra<VS, E> {

    val viewState: Observable<VS>

    fun setState(vs: VS): Completable

    val effects: Observable<E>

    fun runEffect(effect: E): Completable

    fun addToDisposable(d: Disposable)
}