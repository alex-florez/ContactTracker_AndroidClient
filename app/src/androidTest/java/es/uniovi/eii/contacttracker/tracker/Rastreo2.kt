package es.uniovi.eii.contacttracker.tracker


import android.view.View
import android.view.ViewGroup
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
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class Rastreo2 {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun rastreo2() {
         // Added a sleep statement to match the app's execution delay.
 // The recommended way to handle such scenarios is to use Espresso idling resources:
  // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
Thread.sleep(2000)
        
        val tabView = onView(
allOf(withContentDescription("Mapa"),
childAtPosition(
childAtPosition(
withId(R.id.trackLocationTabLayout),
0),
1),
isDisplayed()))
        tabView.perform(click())
        
        val textView = onView(
allOf(withText("MAPA"),
withParent(allOf(withContentDescription("Mapa"),
withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java)))),
isDisplayed()))
        textView.check(matches(withText("MAPA")))
        
        val compoundButton = onView(
allOf(withId(R.id.chipShowMarkers), withText("Marcadores"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
isDisplayed()))
        compoundButton.check(matches(isDisplayed()))
        
        val imageButton = onView(
allOf(withId(R.id.fabMapType), withContentDescription("FAB para desplegar tipos de mapa"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
isDisplayed()))
        imageButton.check(matches(isDisplayed()))
        
        val chip = onView(
allOf(withId(R.id.chipShowMarkers), withText("Marcadores"),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.FrameLayout")),
0),
5),
isDisplayed()))
        chip.perform(click())
        Thread.sleep(1000)
        chip.check(matches(not(isChecked())))
        chip.perform(click())
        Thread.sleep(1000)
        chip.check(matches(isChecked()))

        
        val floatingActionButton = onView(
allOf(withId(R.id.fabMapType), withContentDescription("FAB para desplegar tipos de mapa"),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.FrameLayout")),
0),
1),
isDisplayed()))
        floatingActionButton.perform(click())

        Thread.sleep(2000)
        
        val imageButton2 = onView(
allOf(withId(R.id.fabTypeTerrain), withContentDescription("FAB para seleccionar tipo satélite de mapa"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
isDisplayed()))
        imageButton2.check(matches(isDisplayed()))
        
        val imageButton3 = onView(
allOf(withId(R.id.fabTypeSatellite), withContentDescription("FAB para seleccionar tipo satélite de mapa"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
isDisplayed()))
        imageButton3.check(matches(isDisplayed()))
        
        val imageButton4 = onView(
allOf(withId(R.id.fabTypeNormal), withContentDescription("FAB para seleccionar tipo normal de mapa"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
isDisplayed()))
        imageButton4.check(matches(isDisplayed()))
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
