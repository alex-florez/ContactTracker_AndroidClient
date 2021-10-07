package es.uniovi.eii.contacttracker.riskcontact


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
class Comprobacion1 {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun comprobacion1() {
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
        Thread.sleep(500)
        
        val textView = onView(
allOf(withText("Contactos de Riesgo"),
withParent(allOf(withId(R.id.action_bar),
withParent(withId(R.id.action_bar_container)))),
isDisplayed()))
        textView.check(matches(withText("Contactos de Riesgo")))
        
        val textView2 = onView(
allOf(withText("COMPROBAR"),
withParent(allOf(withContentDescription("Comprobar"),
withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java)))),
isDisplayed()))
        textView2.check(matches(withText("COMPROBAR")))
        
        val textView3 = onView(
allOf(withText("RESULTADOS"),
withParent(allOf(withContentDescription("Resultados"),
withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java)))),
isDisplayed()))
        textView3.check(matches(withText("RESULTADOS")))
        
        val radioButton = onView(
allOf(withId(R.id.radioBtnManualCheck), withText("Comprobación manual"),
withParent(withParent(withId(R.id.materialCardView))),
isDisplayed()))
        radioButton.check(matches(isDisplayed()))
        // Comprobar que está marcado
        radioButton.check(matches(isChecked()))
        
        val textView4 = onView(
allOf(withId(R.id.txtViewManual), withText("Inicia una comprobación manualmente para detectar posibles contactos de riesgo con otros positivos."),
withParent(withParent(withId(R.id.materialCardView))),
isDisplayed()))
        textView4.check(matches(withText("Inicia una comprobación manualmente para detectar posibles contactos de riesgo con otros positivos.")))
        textView4.check(matches(isEnabled()))

        val button = onView(
allOf(withId(R.id.btnManualCheck), withText("COMPROBAR CONTACTOS DE RIESGO"),
withParent(withParent(withId(R.id.materialCardView))),
isDisplayed()))
        button.check(matches(isDisplayed()))
        button.check(matches(isEnabled()))
        
        val radioButton2 = onView(
allOf(withId(R.id.radioBtnPeriodicCheck), withText("Comprobación periódica"),
withParent(withParent(IsInstanceOf.instanceOf(androidx.cardview.widget.CardView::class.java))),
isDisplayed()))
        radioButton2.check(matches(isDisplayed()))
        // Comprobar que está desmarcado
        radioButton2.check(matches(not(isChecked())))
        
        val textView5 = onView(
allOf(withId(R.id.txtViewPeriodic), withText("Programa horas concretas del día para realizar la comprobación de contactos de riesgo. Esta comprobación se repetirá todos los días a la horas indicadas."),
withParent(withParent(IsInstanceOf.instanceOf(androidx.cardview.widget.CardView::class.java))),
isDisplayed()))
        textView5.check(matches(withText("Programa horas concretas del día para realizar la comprobación de contactos de riesgo. Esta comprobación se repetirá todos los días a la horas indicadas.")))
        // Comprobar que está deshabilitado
        textView5.check(matches(not(isEnabled())))

        val imageButton = onView(
allOf(withId(R.id.btnApplyCheckHour), withContentDescription("Añadir alarma de comprobación."),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        imageButton.check(matches(isDisplayed()))
        imageButton.check(matches(not(isEnabled())))
        
        val editText = onView(
allOf(withId(R.id.txtCheckHour),
withParent(withParent(withId(R.id.txtInputLayoutCheckHour))),
isDisplayed()))
        editText.check(matches(not(isEnabled())))
        
        val textView6 = onView(
allOf(withId(R.id.labelScheduledAlarms), withText("Alarmas programadas"),
withParent(withParent(IsInstanceOf.instanceOf(androidx.cardview.widget.CardView::class.java))),
isDisplayed()))
        textView6.check(matches(withText("Alarmas programadas")))
        textView6.check(matches(not(isEnabled())))
        
        val textView7 = onView(
allOf(withId(R.id.labelNoAlarms), withText("No tienes alarmas programadas"),
withParent(withParent(IsInstanceOf.instanceOf(androidx.cardview.widget.CardView::class.java))),
isDisplayed()))
        textView7.check(matches(withText("No tienes alarmas programadas")))
        textView7.check(matches(not(isEnabled())))

        /* Cambiar el modo de comprobación */
        val materialRadioButton = onView(
allOf(withId(R.id.radioBtnPeriodicCheck),
childAtPosition(
childAtPosition(
withClassName(`is`("com.google.android.material.card.MaterialCardView")),
0),
0),
isDisplayed()))
        materialRadioButton.perform(click())
        
        val radioButton3 = onView(
allOf(withId(R.id.radioBtnPeriodicCheck),
withParent(withParent(IsInstanceOf.instanceOf(androidx.cardview.widget.CardView::class.java))),
isDisplayed()))
        radioButton3.check(matches(isChecked()))
        
        val textView8 = onView(
allOf(withId(R.id.txtViewPeriodic),
withParent(withParent(IsInstanceOf.instanceOf(androidx.cardview.widget.CardView::class.java))),
isDisplayed()))
        textView8.check(matches(withText("Programa horas concretas del día para realizar la comprobación de contactos de riesgo. Esta comprobación se repetirá todos los días a la horas indicadas.")))
        textView8.check(matches(isEnabled()))

        val editText2 = onView(
allOf(withId(R.id.txtCheckHour),
withParent(withParent(withId(R.id.txtInputLayoutCheckHour))),
isDisplayed()))
        editText2.check(matches(isEnabled()))
        
        val imageButton2 = onView(
allOf(withId(R.id.btnApplyCheckHour), withContentDescription("Añadir alarma de comprobación."),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        imageButton2.check(matches(isEnabled()))
        
        val button2 = onView(
allOf(withId(R.id.btnManualCheck), withText("COMPROBAR CONTACTOS DE RIESGO"),
withParent(withParent(withId(R.id.materialCardView))),
isDisplayed()))
        // Comprobar que está deshabilitado
        button2.check(matches(not(isEnabled())))
        
        val textView9 = onView(
allOf(withId(R.id.txtViewManual),
withParent(withParent(withId(R.id.materialCardView))),
isDisplayed()))
        textView9.check(matches(withText("Inicia una comprobación manualmente para detectar posibles contactos de riesgo con otros positivos.")))
        textView9.check(matches(not(isEnabled())))
        
        val bottomNavigationItemView2 = onView(
allOf(withId(R.id.notifyPositive), withContentDescription("Positivo"),
childAtPosition(
childAtPosition(
withId(R.id.bottomNavigationView),
0),
1),
isDisplayed()))
        bottomNavigationItemView2.perform(click())
        
        val bottomNavigationItemView3 = onView(
allOf(withId(R.id.riskContacts), withContentDescription("Contactos"),
childAtPosition(
childAtPosition(
withId(R.id.bottomNavigationView),
0),
2),
isDisplayed()))
        bottomNavigationItemView3.perform(click())
        
        val radioButton4 = onView(
allOf(withId(R.id.radioBtnPeriodicCheck), withText("Comprobación periódica"),
withParent(withParent(IsInstanceOf.instanceOf(androidx.cardview.widget.CardView::class.java))),
isDisplayed()))
        radioButton4.check(matches(isChecked()))
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
