package es.uniovi.eii.contacttracker.activities


import android.app.TimePickerDialog
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.fragments.dialogs.timepicker.TimePickerFragment
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest5 {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun mainActivityTest5() {
         // Added a sleep statement to match the app's execution delay.
 // The recommended way to handle such scenarios is to use Espresso idling resources:
  // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
Thread.sleep(2000)
        
        val bottomNavigationItemView = onView(
allOf(withId(R.id.locationHistory), withContentDescription("Histórico"),
childAtPosition(
childAtPosition(
withId(R.id.bottomNavigationView),
0),
3),
isDisplayed()))
        bottomNavigationItemView.perform(click())
        
        val bottomNavigationItemView2 = onView(
allOf(withId(R.id.tracker), withContentDescription("Rastreador"),
childAtPosition(
childAtPosition(
withId(R.id.bottomNavigationView),
0),
0),
isDisplayed()))
        bottomNavigationItemView2.perform(click())
        
        val tabView = onView(
allOf(withContentDescription("Alarmas"),
childAtPosition(
childAtPosition(
withId(R.id.trackLocationTabLayout),
0),
2),
isDisplayed()))
        tabView.perform(click())
        
        val materialButton = onView(
allOf(withId(android.R.id.button2), withText("Cancelar"),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.ScrollView")),
0),
2)))
        materialButton.perform(scrollTo(), click())

        Thread.sleep(5000)

        val textInputEditText = onView(
allOf(withId(R.id.txtStartAutoTracking),
childAtPosition(
childAtPosition(
withId(R.id.txtInputLayoutStartAutoTracking),
0),
1),
isDisplayed()))
        textInputEditText.perform(click())

        // Establecer hora
        onView(withClassName(Matchers.equalTo(TimePicker::class.java.name))).perform(PickerActions.setTime(18, 30))


        Thread.sleep(3000)

        val materialButton2 = onView(
allOf(withId(android.R.id.button1), withText("Aceptar"),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.ScrollView")),
0),
3)))
        materialButton2.perform(scrollTo(), click())
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
    }
