package es.uniovi.eii.contacttracker.settings


import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
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
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class Ajustes2 {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun ajustes2() {
         // Added a sleep statement to match the app's execution delay.
 // The recommended way to handle such scenarios is to use Espresso idling resources:
  // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
Thread.sleep(1000)
        
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

        Thread.sleep(400)

        val textView = onView(
allOf(withId(R.id.prefValue), withText("0 min 3 sec"),
withParent(withParent(withId(R.id.recycler_view))),
isDisplayed()))
        textView.check(matches(withText("0 min 3 sec")))
        
        val recyclerView = onView(
allOf(withId(R.id.recycler_view),
childAtPosition(
withId(android.R.id.list_container),
0)))
        recyclerView.perform(actionOnItemAtPosition<ViewHolder>(1, click()))

        // Establecer Intervalo de Tiempo
        val minutesPickerInterval = onView(withId(R.id.numberPickerMinutes))
        minutesPickerInterval.perform(setNumberPickerValue(2))
        val secsPickerInterval = onView(withId(R.id.numberPickerSeconds))
        secsPickerInterval.perform(setNumberPickerValue(5))
        Thread.sleep(800)

        val materialButton = onView(
allOf(withId(android.R.id.button1), withText("Aceptar"),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.ScrollView")),
0),
3)))
        materialButton.perform(scrollTo(), click())
        Thread.sleep(500)

        val textView2 = onView(
allOf(withId(R.id.prefValue), withText("2 min 5 sec"),
withParent(withParent(withId(R.id.recycler_view))),
isDisplayed()))
        textView2.check(matches(withText("2 min 5 sec")))
        
        val textView3 = onView(
allOf(withId(R.id.prefValue), withText("0 m"),
withParent(withParent(withId(R.id.recycler_view))),
isDisplayed()))
        textView3.check(matches(withText("0 m")))
        
        val recyclerView2 = onView(
allOf(withId(R.id.recycler_view),
childAtPosition(
withId(android.R.id.list_container),
0)))
        recyclerView2.perform(actionOnItemAtPosition<ViewHolder>(2, click()))

        // Establecer Despl. Mínimo
        val metersPicker = onView(withId(R.id.numberPicker))
        metersPicker.perform(setNumberPickerValue(3))
        Thread.sleep(600)

        val materialButton2 = onView(
allOf(withId(android.R.id.button1), withText("Aceptar"),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.ScrollView")),
0),
3)))
        materialButton2.perform(scrollTo(), click())
        Thread.sleep(600)
        val textView4 = onView(
allOf(withId(R.id.prefValue), withText("3 m"),
withParent(withParent(withId(R.id.recycler_view))),
isDisplayed()))
        textView4.check(matches(withText("3 m")))
        
        val textView5 = onView(
allOf(withId(R.id.prefValue), withText("3 días"),
withParent(withParent(withId(R.id.recycler_view))),
isDisplayed()))
        textView5.check(matches(withText("3 días")))
        
        val recyclerView3 = onView(
allOf(withId(R.id.recycler_view),
childAtPosition(
withId(android.R.id.list_container),
0)))
        recyclerView3.perform(actionOnItemAtPosition<ViewHolder>(4, click()))

        // Establecer Alcance de la comprobación
        val daysPicker = onView(withId(R.id.numberPicker))
        daysPicker.perform(setNumberPickerValue(1))
        Thread.sleep(600)

        val materialButton3 = onView(
allOf(withId(android.R.id.button1), withText("Aceptar"),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.ScrollView")),
0),
3)))
        materialButton3.perform(scrollTo(), click())
        
        val textView6 = onView(
allOf(withId(R.id.prefValue), withText("1 días"),
withParent(withParent(withId(R.id.recycler_view))),
isDisplayed()))
        textView6.check(matches(withText("1 días")))

        // Hacer Scroll hacia abajo
        onView(withId(androidx.preference.R.id.recycler_view)).perform(ViewActions.swipeUp())
        Thread.sleep(700)

        val checkBox = onView(
allOf(withId(android.R.id.checkbox),
withParent(allOf(withId(android.R.id.widget_frame),
withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java)))),
isDisplayed()))
        checkBox.check(matches(not(isChecked())))
        
        val recyclerView4 = onView(
allOf(withId(R.id.recycler_view),
childAtPosition(
withId(android.R.id.list_container),
0)))
        recyclerView4.perform(actionOnItemAtPosition<ViewHolder>(6, click()))


        Thread.sleep(800)
        checkBox.check(matches(isChecked()))
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

    private fun setNumberPickerValue(value: Int): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isAssignableFrom(NumberPicker::class.java)
            }

            override fun getDescription(): String {
                return "Set the passed time into the TimePicker"
            }

            override fun perform(uiController: UiController?, view: View?) {
                val np = view as NumberPicker
                np.value = value
            }
        }
    }
    }
