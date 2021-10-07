package es.uniovi.eii.contacttracker.riskcontact


import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.google.android.material.chip.Chip
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
class Comprobacion2 {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun comprobacion2() {
         // Added a sleep statement to match the app's execution delay.
 // The recommended way to handle such scenarios is to use Espresso idling resources:
  // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
Thread.sleep(1000)
        
        val bottomNavigationItemView = onView(
allOf(withId(R.id.riskContacts), withContentDescription("Contactos"),
childAtPosition(
childAtPosition(
withId(R.id.bottomNavigationView),
0),
2),
isDisplayed()))
        bottomNavigationItemView.perform(click())
        
        val materialRadioButton = onView(
allOf(withId(R.id.radioBtnPeriodicCheck), withText("Comprobación periódica"),
childAtPosition(
childAtPosition(
withClassName(`is`("com.google.android.material.card.MaterialCardView")),
0),
0),
isDisplayed()))
        materialRadioButton.perform(click())
        
        val textInputEditText = onView(
allOf(withId(R.id.txtCheckHour),
childAtPosition(
childAtPosition(
withId(R.id.txtInputLayoutCheckHour),
0),
1),
isDisplayed()))
        textInputEditText.perform(click())
        textInputEditText.perform(click())
        Thread.sleep(600)
        // Establecer hora de la comprobación
        onView(withClassName(Matchers.equalTo(TimePicker::class.java.name)))
            .perform(PickerActions.setTime(10, 30))
        
        val materialButton = onView(
allOf(withId(android.R.id.button1), withText("Aceptar"),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.ScrollView")),
0),
3)))
        materialButton.perform(scrollTo(), click())
        
        val appCompatImageButton = onView(
allOf(withId(R.id.btnApplyCheckHour), withContentDescription("Añadir alarma de comprobación."),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.LinearLayout")),
2),
1),
isDisplayed()))
        appCompatImageButton.perform(click())

        // Comprobar que se ha insertado correctamente.

        val compoundButton = onView(
allOf(withText("10:30"),
withParent(allOf(withId(R.id.alarmChipGroup),
withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java)))),
isDisplayed()))
        compoundButton.check(matches(isDisplayed()))
        
        val textView = onView(
allOf(withId(R.id.snackbar_text), withText("Alarma de comprobación programada correctamente."),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
isDisplayed()))
        textView.check(matches(withText("Alarma de comprobación programada correctamente.")))

        // Intentar añadir alarma con la misma hora.
        val appCompatImageButton2 = onView(
allOf(withId(R.id.btnApplyCheckHour), withContentDescription("Añadir alarma de comprobación."),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.LinearLayout")),
2),
1),
isDisplayed()))
        appCompatImageButton2.perform(click())
        
        val textView2 = onView(
allOf(withId(R.id.snackbar_text), withText("Ya existen alarmas de comprobación programadas para esa hora."),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
isDisplayed()))
        textView2.check(matches(withText("Ya existen alarmas de comprobación programadas para esa hora.")))

        val textInputEditText2 = onView(
allOf(withId(R.id.txtCheckHour),
childAtPosition(
childAtPosition(
withId(R.id.txtInputLayoutCheckHour),
0),
1),
isDisplayed()))
        textInputEditText2.perform(click())

        Thread.sleep(600)
        // Insertar la segunda alarma de comprobación
        onView(withClassName(Matchers.equalTo(TimePicker::class.java.name)))
            .perform(PickerActions.setTime(11, 0))

        val materialButton2 = onView(
allOf(withId(android.R.id.button1), withText("Aceptar"),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.ScrollView")),
0),
3)))
        materialButton2.perform(scrollTo(), click())
        
        val appCompatImageButton3 = onView(
allOf(withId(R.id.btnApplyCheckHour), withContentDescription("Añadir alarma de comprobación."),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.LinearLayout")),
2),
1),
isDisplayed()))
        appCompatImageButton3.perform(click())

        // Comprobar que se inserta correctamente.
        val compoundButton2 = onView(
allOf(withText("11:00"),
withParent(allOf(withId(R.id.alarmChipGroup),
withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java)))),
isDisplayed()))
        compoundButton2.check(matches(isDisplayed()))
        
        val textView3 = onView(
allOf(withId(R.id.snackbar_text), withText("Alarma de comprobación programada correctamente."),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
isDisplayed()))
        textView3.check(matches(withText("Alarma de comprobación programada correctamente.")))

        // Insertar una tercera alarma
        val textInputEditText3 = onView(
allOf(withId(R.id.txtCheckHour),
childAtPosition(
childAtPosition(
withId(R.id.txtInputLayoutCheckHour),
0),
1),
isDisplayed()))
        textInputEditText3.perform(click())
        Thread.sleep(600)
        // Establecer hora
        onView(withClassName(Matchers.equalTo(TimePicker::class.java.name)))
            .perform(PickerActions.setTime(16, 0))

        val materialButton3 = onView(
allOf(withId(android.R.id.button1), withText("Aceptar"),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.ScrollView")),
0),
3)))
        materialButton3.perform(scrollTo(), click())
        
        val appCompatImageButton4 = onView(
allOf(withId(R.id.btnApplyCheckHour), withContentDescription("Añadir alarma de comprobación."),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.LinearLayout")),
2),
1),
isDisplayed()))
        appCompatImageButton4.perform(click())

        // Comprobar que se inserta correctamente.
        val compoundButton5 = onView(
            allOf(withText("16:00"),
                withParent(allOf(withId(R.id.alarmChipGroup),
                    withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java)))),
                isDisplayed()))
        compoundButton5.check(matches(isDisplayed()))

        val textView7 = onView(
            allOf(withId(R.id.snackbar_text), withText("Alarma de comprobación programada correctamente."),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                isDisplayed()))
        textView7.check(matches(withText("Alarma de comprobación programada correctamente.")))

        // Insertar una tercera alarma de comprobación
        val textInputEditText4 = onView(
allOf(withId(R.id.txtCheckHour),
childAtPosition(
childAtPosition(
withId(R.id.txtInputLayoutCheckHour),
0),
1),
isDisplayed()))
        textInputEditText4.perform(click())
        Thread.sleep(600)
        // Establecer hora
        onView(withClassName(Matchers.equalTo(TimePicker::class.java.name)))
            .perform(PickerActions.setTime(18, 0))

        val materialButton4 = onView(
allOf(withId(android.R.id.button1), withText("Aceptar"),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.ScrollView")),
0),
3)))
        materialButton4.perform(scrollTo(), click())
        
        val appCompatImageButton5 = onView(
allOf(withId(R.id.btnApplyCheckHour), withContentDescription("Añadir alarma de comprobación."),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.LinearLayout")),
2),
1),
isDisplayed()))
        appCompatImageButton5.perform(click())

        val textView5 = onView(
allOf(withId(R.id.snackbar_text), withText("No puedes programar más de 3 alarmas de comprobación."),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
isDisplayed()))
        textView5.check(matches(withText("No puedes programar más de 3 alarmas de comprobación.")))

        // Eliminar la segunda alarma de comprobación
        onView(withId(2)).perform(DeleteAlarmChip())

        val appCompatImageButton6 = onView(
allOf(withId(R.id.btnApplyCheckHour), withContentDescription("Añadir alarma de comprobación."),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.LinearLayout")),
2),
1),
isDisplayed()))
        appCompatImageButton6.perform(click())
        
        val compoundButton4 = onView(
allOf(withText("18:00"),
withParent(allOf(withId(R.id.alarmChipGroup),
withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java)))),
isDisplayed()))
        compoundButton4.check(matches(isDisplayed()))
        
        val textView6 = onView(
allOf(withId(R.id.snackbar_text), withText("Alarma de comprobación programada correctamente."),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
isDisplayed()))
        textView6.check(matches(withText("Alarma de comprobación programada correctamente.")))
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

    class DeleteAlarmChip : ViewAction {

        override fun getConstraints(): Matcher<View> {
            return ViewMatchers.isAssignableFrom(Chip::class.java)
        }

        override fun getDescription(): String {
            return "click drawable "
        }

        override fun perform(uiController: UiController, view: View) {
            val chip = view as Chip//we matched
            chip.performCloseIconClick()
        }
    }
    }
