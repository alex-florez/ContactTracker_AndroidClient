package es.uniovi.eii.contacttracker.tracker


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import androidx.test.runner.AndroidJUnit4
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.activities.MainActivity
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class Rastreo1 {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Rule
    @JvmField
    var mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION")

    @Test
    fun rastreo1() {
         // Added a sleep statement to match the app's execution delay.
 // The recommended way to handle such scenarios is to use Espresso idling resources:
  // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
Thread.sleep(2000)
        
        val textView = onView(
allOf(withText("Rastreo de ubicación"),
withParent(allOf(withId(R.id.action_bar),
withParent(withId(R.id.action_bar_container)))),
isDisplayed()))
        textView.check(matches(withText("Rastreo de ubicación")))
        
        val textView2 = onView(
allOf(withText("RASTREADOR"),
withParent(allOf(withContentDescription("Rastreador"),
withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java)))),
isDisplayed()))
        textView2.check(matches(withText("RASTREADOR")))
        
        val textView3 = onView(
allOf(withText("Rastreo de Ubicación"),
withParent(withParent(withId(R.id.layoutCardLocationTracker))),
isDisplayed()))
        textView3.check(matches(withText("Rastreo de Ubicación")))
        
        val textView4 = onView(
allOf(withId(R.id.labelNoLocations), withText("No hay localizaciones que mostrar."),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
isDisplayed()))
        textView4.check(matches(withText("No hay localizaciones que mostrar.")))
        
        val switchMaterial = onView(
allOf(withId(R.id.switchTrackLocation), withText("Servicio para rastrear la ubicación"),
childAtPosition(
childAtPosition(
withId(R.id.layoutCardLocationTracker),
0),
1),
isDisplayed()))
        switchMaterial.perform(click())

        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText(R.string.labelStartedService)))
        
         // Added a sleep statement to match the app's execution delay.
 // The recommended way to handle such scenarios is to use Espresso idling resources:
  // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
Thread.sleep(3000)
        
        val switchMaterial2 = onView(
allOf(withId(R.id.switchTrackLocation), withText("Servicio para rastrear la ubicación"),
childAtPosition(
childAtPosition(
withId(R.id.layoutCardLocationTracker),
0),
1),
isDisplayed()))
        switchMaterial2.perform(click())

        // Comprobar Snackbar
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText(R.string.labelStoppedService)))

        Thread.sleep(2000)
        
        val textView5 = onView(
allOf(withId(R.id.labelNoLocations), withText("No hay localizaciones que mostrar."),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
isDisplayed()))
        textView5.check(matches(isDisplayed()))
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
