package io.github.lordraydenmk.superheroesapp

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.lordraydenmk.superheroesapp.ScreenScenario.Companion.launchInContainer
import io.github.lordraydenmk.superheroesapp.superheroes.presentation.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.hamcrest.CoreMatchers.not
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SuperheroesScreenTest {

    @Test
    fun loadingState_progressBarDisplayed() {
        val scenario = launchInContainer { parent -> SuperheroesScreen(parent) }
        scenario.onView { view -> view.bind(Loading) }

        onView(withId(R.id.progressSuperheroes)).check(matches(isDisplayed()))

        onView(withId(R.id.rvSuperheroes)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tvCopyright)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tvError)).check(matches(not(isDisplayed())))
    }

    @Test
    fun problemState_errorViewDisplayed() {
        val scenario = launchInContainer { parent -> SuperheroesScreen(parent) }
        scenario.onView { view -> view.bind(Problem("Big problem!")) }

        onView(withId(R.id.tvError)).check(matches(isDisplayed()))
        onView(withId(R.id.tvError)).check(matches(withText("Big problem!")))

        onView(withId(R.id.progressSuperheroes)).check(matches(not(isDisplayed())))
        onView(withId(R.id.rvSuperheroes)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tvCopyright)).check(matches(not(isDisplayed())))
    }

    @Test
    fun contentState_recyclerViewAndCopyrightViewDisplayed() {
        val url = "http://i.annihil.us/u/prod/marvel/i/mg/c/e0/535fecbbb9784.jpg".toHttpUrl()
        val viewState = Content(
            listOf(
                SuperheroViewEntity(42, "Ant Man", url),
                SuperheroViewEntity(43, "Spider Man", url),
                SuperheroViewEntity(44, "Iron Man", url),
                SuperheroViewEntity(45, "Hulk", url)
            ),
            "Copyright Marvel"
        )
        val scenario = launchInContainer { parent -> SuperheroesScreen(parent) }

        scenario.onView { view -> view.bind(viewState) }

        onView(withId(R.id.rvSuperheroes)).check(matches(isDisplayed()))
        onView(withId(R.id.tvCopyright)).check(matches(isDisplayed()))
        onView(withId(R.id.tvCopyright)).check(matches(withText("Copyright Marvel")))

        onView(withId(R.id.progressSuperheroes)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tvError)).check(matches(not(isDisplayed())))
    }
}