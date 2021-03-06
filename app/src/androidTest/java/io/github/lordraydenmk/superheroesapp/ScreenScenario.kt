package io.github.lordraydenmk.superheroesapp

import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.test.core.app.ActivityScenario

class ScreenScenario<A : Any>(
    private val screen: A,
    private val activityScenario: ActivityScenario<ScreenTestActivity>
) {

    fun onView(action: (A) -> Unit) = also {
        activityScenario.onActivity { action(screen) }
    }

    companion object {

        fun <A : Any> launchInContainer(createScreen: (parent: ViewGroup) -> A): ScreenScenario<A> {
            val activityScenario = ActivityScenario.launch(ScreenTestActivity::class.java)
            var screen: A? = null
            activityScenario.onActivity { activity ->
                val rootView =
                    activity.findViewById<ConstraintLayout>(R.id.constraintViewTestContainer)
                screen = createScreen(rootView)
            }
            return ScreenScenario(checkNotNull(screen), activityScenario)
        }
    }
}