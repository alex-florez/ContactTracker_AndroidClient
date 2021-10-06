package es.uniovi.eii.contacttracker.history


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.activities.MainActivity
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// CONSTANTES
private const val MONTH = "10" // Mes en el que se ejecuta la prueba.

@LargeTest
@RunWith(AndroidJUnit4::class)
class Historico1 {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun historico1() {
         // Added a sleep statement to match the app's execution delay.
 // The recommended way to handle such scenarios is to use Espresso idling resources:
  // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
Thread.sleep(1000)
        
        val bottomNavigationItemView = onView(
allOf(withId(R.id.locationHistory), withContentDescription("Histórico"),
childAtPosition(
childAtPosition(
withId(R.id.bottomNavigationView),
0),
3),
isDisplayed()))
        bottomNavigationItemView.perform(click())
        
        val textView = onView(
allOf(withText("Histórico"),
withParent(allOf(withId(R.id.action_bar),
withParent(withId(R.id.action_bar_container)))),
isDisplayed()))
        textView.check(matches(withText("Histórico")))
        
        val textView2 = onView(
allOf(withId(R.id.labelHistoryNoLocations), withText("No hay localizaciones que mostrar"),
withParent(withParent(withId(R.id.nav_host_fragment))),
isDisplayed()))
        textView2.check(matches(withText("No hay localizaciones que mostrar")))
        
        val textInputEditText = onView(
allOf(withId(R.id.txtInputEditTextHistoryDate),
childAtPosition(
childAtPosition(
withId(R.id.txtInputLayoutHistoryDate),
0),
1),
isDisplayed()))
        textInputEditText.perform(click())
        Thread.sleep(500)
        // Establecer el día
        setDay(5)
        
        val materialButton = onView(
allOf(withId(R.id.confirm_button), withText("Aceptar"),
childAtPosition(
allOf(withId(R.id.date_picker_actions),
childAtPosition(
withId(R.id.mtrl_calendar_main_pane),
1)),
1),
isDisplayed()))
        materialButton.perform(click())
        Thread.sleep(500)
        val editText = onView(
allOf(withId(R.id.txtInputEditTextHistoryDate),
withParent(withParent(withId(R.id.txtInputLayoutHistoryDate))),
isDisplayed()))
        editText.check(matches(withText("05/${MONTH}/2021")))
        
        val textView3 = onView(
allOf(withId(R.id.labelHistoryNoLocations), withText("No hay localizaciones que mostrar"),
withParent(withParent(withId(R.id.nav_host_fragment))),
isDisplayed()))
        textView3.check(matches(withText("No hay localizaciones que mostrar")))
        }
    
    private fun childAtPosition(
            parentMatcher: Matcher<View>, position: Int): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }

    /**
     * Establece un día en el DatePicker.
     */
    private fun setDay(day: Int) {
        onView(
            allOf(
                isDescendantOfA(withTagValue(equalTo("MONTHS_VIEW_GROUP_TAG"))),
                isCompletelyDisplayed(),
                withText(day.toString())
            )
        ).perform(click())
    }
    }
