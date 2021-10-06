package es.uniovi.eii.contacttracker.activities


import android.view.View
import android.view.ViewGroup
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import es.uniovi.eii.contacttracker.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityScenarioRule(MainActivity::class.java)

    @Rule
    @JvmField
    var mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION")

    @Test
    fun mainActivityTest() {
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
        
        val chip = onView(
allOf(withId(R.id.chipShowMarkers), withText("Marcadores"),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.FrameLayout")),
0),
5),
isDisplayed()))
        chip.perform(click())
        
        val tabView2 = onView(
allOf(withContentDescription("Alarmas"),
childAtPosition(
childAtPosition(
withId(R.id.trackLocationTabLayout),
0),
2),
isDisplayed()))
        tabView2.perform(click())
        
        val materialButton = onView(
allOf(withId(android.R.id.button2), withText("Cancelar"),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.ScrollView")),
0),
2)))
        materialButton.perform(scrollTo(), click())
        
        val tabView3 = onView(
allOf(withContentDescription("Rastreador"),
childAtPosition(
childAtPosition(
withId(R.id.trackLocationTabLayout),
0),
0),
isDisplayed()))
        tabView3.perform(click())
        
        val switchMaterial = onView(
allOf(withId(R.id.switchTrackLocation), withText("Servicio para rastrear la ubicación"),
childAtPosition(
childAtPosition(
withId(R.id.layoutCardLocationTracker),
0),
1),
isDisplayed()))

        Thread.sleep(2000)
        switchMaterial.perform(click())
        
         // Added a sleep statement to match the app's execution delay.
 // The recommended way to handle such scenarios is to use Espresso idling resources:
  // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
Thread.sleep(7000)
        
        val switchMaterial2 = onView(
allOf(withId(R.id.switchTrackLocation), withText("Servicio para rastrear la ubicación"),
childAtPosition(
childAtPosition(
withId(R.id.layoutCardLocationTracker),
0),
1),
isDisplayed()))
        switchMaterial2.perform(click())
        
        val textView = onView(
allOf(withText("RASTREADOR"),
withParent(allOf(withContentDescription("Rastreador"),
withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java)))),
isDisplayed()))
        textView.check(matches(withText("RASTREADOR")))
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
