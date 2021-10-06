package es.uniovi.eii.contacttracker.activities


import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.TimePicker
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import es.uniovi.eii.contacttracker.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest4 {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun mainActivityTest4() {
         // Added a sleep statement to match the app's execution delay.
 // The recommended way to handle such scenarios is to use Espresso idling resources:
  // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
Thread.sleep(2000)
        
        val overflowMenuButton = onView(
allOf(withContentDescription("Más opciones"),
childAtPosition(
childAtPosition(
withId(R.id.action_bar),
1),
0),
isDisplayed()))
        overflowMenuButton.perform(click())
        
         // Added a sleep statement to match the app's execution delay.
 // The recommended way to handle such scenarios is to use Espresso idling resources:
  // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
Thread.sleep(250)
        
        val materialTextView = onView(
allOf(withId(R.id.title), withText("Ajustes"),
childAtPosition(
childAtPosition(
withId(R.id.content),
0),
0),
isDisplayed()))
        materialTextView.perform(click())
        
        val recyclerView = onView(
allOf(withId(R.id.recycler_view),
childAtPosition(
withId(android.R.id.list_container),
0)))
        recyclerView.perform(actionOnItemAtPosition<ViewHolder>(1, click()))


        val numberPicker = onView(withId(R.id.numberPickerMinutes))
        numberPicker.perform(setTime(4))


        Thread.sleep(3000)

        val materialButton = onView(
allOf(withId(android.R.id.button1), withText("Aceptar"),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.ScrollView")),
0),
3)))
        materialButton.perform(scrollTo(), click())

        Thread.sleep(3000)

        val textView = onView(
allOf(withId(R.id.prefValue), withText("4 min 5 sec"),
withParent(withParent(withId(R.id.recycler_view))),
isDisplayed()))
        textView.check(matches(withText("4 min 5 sec")))
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

    companion object {
        fun setTime(mins: Int): ViewAction {
            return object : ViewAction {
                override fun getConstraints(): Matcher<View> {
                    return isAssignableFrom(NumberPicker::class.java)
                }

                override fun getDescription(): String {
                    return "Set the passed time into the TimePicker"
                }

                override fun perform(uiController: UiController?, view: View?) {
                    val np = view as NumberPicker
                    np.value = mins
                }
            }
        }
    }
}


