package es.uniovi.eii.contacttracker.settings


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
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
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class Ajustes1 {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun ajustes1() {
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

        Thread.sleep(800)
        
        val textView = onView(
allOf(withText("Configuración"),
withParent(allOf(withId(R.id.action_bar),
withParent(withId(R.id.action_bar_container)))),
isDisplayed()))
        textView.check(matches(withText("Configuración")))
        
        val textView2 = onView(
allOf(withId(android.R.id.title), withText("Rastreo"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView2.check(matches(withText("Rastreo")))
        
        val textView3 = onView(
allOf(withId(R.id.prefTitle), withText("Intervalo de tiempo"),
withParent(withParent(withId(R.id.recycler_view))),
isDisplayed()))
        textView3.check(matches(withText("Intervalo de tiempo")))
        
        val textView4 = onView(
allOf(withId(R.id.prefDescription), withText("Intervalo de tiempo mínimo transcurrido entre cada registro de localización."),
withParent(withParent(withId(R.id.recycler_view))),
isDisplayed()))
        textView4.check(matches(withText("Intervalo de tiempo mínimo transcurrido entre cada registro de localización.")))
        
        val textView5 = onView(
allOf(withId(R.id.prefValue), withText("0 min 3 sec"),
withParent(withParent(withId(R.id.recycler_view))),
isDisplayed()))
        textView5.check(matches(withText("0 min 3 sec")))
        
        val textView6 = onView(
allOf(withId(R.id.prefTitle), withText("Desplazamiento mínimo"),
withParent(withParent(withId(R.id.recycler_view))),
isDisplayed()))
        textView6.check(matches(withText("Desplazamiento mínimo")))
        
        val textView7 = onView(
allOf(withId(R.id.prefDescription), withText("Distancia mínima de diferencia entre localizaciones para que se registren las ubicaciones durante el rastreo."),
withParent(withParent(withId(R.id.recycler_view))),
isDisplayed()))
        textView7.check(matches(withText("Distancia mínima de diferencia entre localizaciones para que se registren las ubicaciones durante el rastreo.")))
        
        val textView8 = onView(
allOf(withId(R.id.prefValue), withText("0 m"),
withParent(withParent(withId(R.id.recycler_view))),
isDisplayed()))
        textView8.check(matches(withText("0 m")))
        
        val textView9 = onView(
allOf(withId(android.R.id.title), withText("Comprobación de contactos"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView9.check(matches(withText("Comprobación de contactos")))
        
        val textView10 = onView(
allOf(withId(R.id.prefTitle), withText("Alcance de la comprobación"),
withParent(withParent(withId(R.id.recycler_view))),
isDisplayed()))
        textView10.check(matches(withText("Alcance de la comprobación")))
        
        val textView11 = onView(
allOf(withId(R.id.prefDescription), withText("Número de días que se tienen en cuenta a la hora de comprobar los contactos de riesgo. Se comparan las localizaciones registradas en los anteriores días especificados."),
withParent(withParent(withId(R.id.recycler_view))),
isDisplayed()))
        textView11.check(matches(withText("Número de días que se tienen en cuenta a la hora de comprobar los contactos de riesgo. Se comparan las localizaciones registradas en los anteriores días especificados.")))
        
        val textView12 = onView(
allOf(withId(R.id.prefValue), withText("3 días"),
withParent(withParent(withId(R.id.recycler_view))),
isDisplayed()))
        textView12.check(matches(withText("3 días")))
        
        val textView13 = onView(
allOf(withId(android.R.id.title), withText("Notificaciones"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView13.check(matches(withText("Notificaciones")))

        // Hacer Scroll hacia abajo
        onView(withId(androidx.preference.R.id.recycler_view)).perform(ViewActions.swipeUp())
        Thread.sleep(700)

        val textView14 = onView(
allOf(withId(android.R.id.title), withText("Notificaciones de positivos"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView14.check(matches(withText("Notificaciones de positivos")))
        
        val textView15 = onView(
allOf(withId(android.R.id.summary), withText("Habilita el envío de notificaciones diarias acerca del número de positivos que se han notificado ese día."),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView15.check(matches(withText("Habilita el envío de notificaciones diarias acerca del número de positivos que se han notificado ese día.")))
        
        val checkBox = onView(
allOf(withId(android.R.id.checkbox),
withParent(allOf(withId(android.R.id.widget_frame),
withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java)))),
isDisplayed()))
        checkBox.check(matches(isDisplayed()))
        checkBox.check(matches(not(isChecked())))
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
