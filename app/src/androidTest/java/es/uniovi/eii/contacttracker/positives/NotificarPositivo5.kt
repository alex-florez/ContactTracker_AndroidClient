package es.uniovi.eii.contacttracker.positives


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.activities.MainActivity
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
class NotificarPositivo5 {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun notificarPositivo5() {
         // Added a sleep statement to match the app's execution delay.
 // The recommended way to handle such scenarios is to use Espresso idling resources:
  // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
Thread.sleep(1000)
        
        val bottomNavigationItemView = onView(
allOf(withId(R.id.notifyPositive), withContentDescription("Positivo"),
childAtPosition(
childAtPosition(
withId(R.id.bottomNavigationView),
0),
1),
isDisplayed()))
        bottomNavigationItemView.perform(click())
        
        val materialButton = onView(
allOf(withId(R.id.btnNotifyPositive), withText("He dado positivo"),
childAtPosition(
allOf(withId(R.id.linearLayout3),
childAtPosition(
withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
7)),
0),
isDisplayed()))
        materialButton.perform(click())
        Thread.sleep(800)
        
        val materialButton2 = onView(
allOf(withId(android.R.id.button1), withText("Aceptar"),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.ScrollView")),
0),
3)))
        materialButton2.perform(scrollTo(), click())
        Thread.sleep(500)
        
        val textView = onView(
allOf(withId(R.id.snackbar_text), withText("Se han subido 34 localizaciones a la nube."),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
isDisplayed()))
        textView.check(matches(withText("Se han subido 34 localizaciones a la nube.")))
        
        val materialButton3 = onView(
allOf(withId(R.id.btnNotifyPositive), withText("He dado positivo"),
childAtPosition(
allOf(withId(R.id.linearLayout3),
childAtPosition(
withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
7)),
0),
isDisplayed()))
        materialButton3.perform(click())
        
        val materialButton4 = onView(
allOf(withId(android.R.id.button1), withText("Aceptar"),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.ScrollView")),
0),
3)))
        materialButton4.perform(scrollTo(), click())
        Thread.sleep(600)
        
        val textView2 = onView(
allOf(withId(R.id.snackbar_text), withText("Se ha superado el límite de notificación de positivos. No podrás notificar más positivos hasta pasados unos días."),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
isDisplayed()))
        textView2.check(matches(withText("Se ha superado el límite de notificación de positivos. No podrás notificar más positivos hasta pasados unos días.")))
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
