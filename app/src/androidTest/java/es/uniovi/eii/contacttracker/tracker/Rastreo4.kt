package es.uniovi.eii.contacttracker.tracker


import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.activities.MainActivity
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@LargeTest
@RunWith(AndroidJUnit4::class)
class Rastreo4 {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun rastreo4() {
         // Added a sleep statement to match the app's execution delay.
 // The recommended way to handle such scenarios is to use Espresso idling resources:
  // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
Thread.sleep(1000)
        
        val tabView = onView(
allOf(withContentDescription("Alarmas"),
childAtPosition(
childAtPosition(
withId(R.id.trackLocationTabLayout),
0),
2),
isDisplayed()))
        tabView.perform(click())
        
        val textView = onView(
allOf(withId(R.id.txtLabelNoAlarms), withText("No tienes alarmas programadas"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
isDisplayed()))
        textView.check(matches(withText("No tienes alarmas programadas")))
        
        val textView2 = onView(
allOf(withText("Alarmas de localización"),
withParent(withParent(withId(R.id.layoutCardLocationAlarm))),
isDisplayed()))
        textView2.check(matches(withText("Alarmas de localización")))
        
        val textView3 = onView(
allOf(withText("Agregar una nueva alarma de localización"),
withParent(withParent(withId(R.id.layoutCardLocationAlarm))),
isDisplayed()))
        textView3.check(matches(withText("Agregar una nueva alarma de localización")))
        
        val textInputEditText = onView(
allOf(withId(R.id.txtStartAutoTracking),
childAtPosition(
childAtPosition(
withId(R.id.txtInputLayoutStartAutoTracking),
0),
1),
isDisplayed()))
        textInputEditText.perform(click())

        Thread.sleep(1000)
        // Establecer hora de INICIO
        onView(withClassName(Matchers.equalTo(TimePicker::class.java.name))).perform(PickerActions.setTime(12, 15))

        val materialButton2 = onView(
allOf(withId(android.R.id.button1), withText("Aceptar"),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.ScrollView")),
0),
3)))
        materialButton2.perform(scrollTo(), click())
        
        val textInputEditText2 = onView(
allOf(withId(R.id.txtEndAutoTracking),
childAtPosition(
childAtPosition(
withId(R.id.txtInputLayoutEndAutoTracking),
0),
1),
isDisplayed()))
        textInputEditText2.perform(click())
        textInputEditText2.perform(click())
        Thread.sleep(1000)
        // Establecer hora de FIN
        onView(withClassName(Matchers.equalTo(TimePicker::class.java.name))).perform(PickerActions.setTime(12, 45))

        val materialButton3 = onView(
allOf(withId(android.R.id.button1), withText("Aceptar"),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.ScrollView")),
0),
3)))
        materialButton3.perform(scrollTo(), click())
        
        val editText = onView(
allOf(withId(R.id.txtStartAutoTracking), withText("12:15"),
withParent(withParent(withId(R.id.txtInputLayoutStartAutoTracking))),
isDisplayed()))
        editText.check(matches(withText("12:15")))
        
        val editText2 = onView(
allOf(withId(R.id.txtEndAutoTracking), withText("12:45"),
withParent(withParent(withId(R.id.txtInputLayoutEndAutoTracking))),
isDisplayed()))
        editText2.check(matches(withText("12:45")))
        
        val appCompatImageButton = onView(
allOf(withId(R.id.btnAddLocationAlarm), withContentDescription("Añadir alarma de localización"),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.LinearLayout")),
0),
2),
isDisplayed()))
        appCompatImageButton.perform(click())

        Thread.sleep(800)

        val textView4 = onView(
allOf(withId(R.id.startHour), withText("12:15"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView4.check(matches(withText("12:15")))
        
        val textView5 = onView(
allOf(withId(R.id.endHour), withText("12:45"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView5.check(matches(withText("12:45")))
        
        val textInputEditText3 = onView(
allOf(withId(R.id.txtStartAutoTracking), withText("12:15"),
childAtPosition(
childAtPosition(
withId(R.id.txtInputLayoutStartAutoTracking),
0),
1),
isDisplayed()))
        textInputEditText3.perform(click())
        textInputEditText3.perform(click())
        Thread.sleep(800)
        // Establecer hora de INICIO
        onView(withClassName(Matchers.equalTo(TimePicker::class.java.name))).perform(PickerActions.setTime(12, 20))

        
        val materialButton4 = onView(
allOf(withId(android.R.id.button1), withText("Aceptar"),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.ScrollView")),
0),
3)))
        materialButton4.perform(scrollTo(), click())
        
        val textInputEditText4 = onView(
allOf(withId(R.id.txtEndAutoTracking), withText("12:45"),
childAtPosition(
childAtPosition(
withId(R.id.txtInputLayoutEndAutoTracking),
0),
1),
isDisplayed()))
        textInputEditText4.perform(click())
        textInputEditText4.perform(click())
        Thread.sleep(800)
        // Establecer hora de FIN
        onView(withClassName(Matchers.equalTo(TimePicker::class.java.name))).perform(PickerActions.setTime(12, 35))

        val materialButton5 = onView(
allOf(withId(android.R.id.button1), withText("Aceptar"),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.ScrollView")),
0),
3)))
        materialButton5.perform(scrollTo(), click())
        
        val appCompatImageButton2 = onView(
allOf(withId(R.id.btnAddLocationAlarm), withContentDescription("Añadir alarma de localización"),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.LinearLayout")),
0),
2),
isDisplayed()))
        appCompatImageButton2.perform(click())

        // Comprobar Snackbar de error de Colisión de alarmas
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText(R.string.errorAlarmCollision)))
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
