package es.uniovi.eii.contacttracker.riskcontact


import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
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
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class Comprobacion3 {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun comprobacion3() {
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
        
        val materialButton = onView(
allOf(withId(R.id.btnManualCheck), withText("Comprobar contactos de riesgo"),
childAtPosition(
childAtPosition(
withId(R.id.materialCardView),
0),
2),
isDisplayed()))
        materialButton.perform(click())
            Thread.sleep(800)
        
        val tabView = onView(
allOf(withContentDescription("Resultados"),
childAtPosition(
childAtPosition(
withId(R.id.riskContactTabLayout),
0),
1),
isDisplayed()))
        tabView.perform(click())
        
        val textView = onView(
allOf(withText("RESULTADOS"),
withParent(allOf(withContentDescription("Resultados"),
withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java)))),
isDisplayed()))
        textView.check(matches(withText("RESULTADOS")))
        
        val textView2 = onView(
allOf(withText("Hora de la comprobación"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView2.check(matches(withText("Hora de la comprobación")))
        
        val textView3 = onView(
allOf(withText("65,84 %"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView3.check(matches(withText("65,84 %")))
        
        val textView4 = onView(
allOf(withText("Riesgo más alto"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView4.check(matches(withText("Riesgo más alto")))
        
        val textView5 = onView(
allOf(withText("Positivos"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView5.check(matches(withText("Positivos")))
        
        val textView6 = onView(
allOf(withId(R.id.numberOfContactPositives),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView6.check(matches(withText("1")))
        
        val textView7 = onView(
allOf(withText("Contactos de riesgo"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView7.check(matches(withText("Contactos de riesgo")))
        
        val textView8 = onView(
allOf(withId(R.id.numberOfContacts),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView8.check(matches(withText("1")))
        
        val recyclerView = onView(
allOf(withId(R.id.recyclerViewRiskContactResults),
childAtPosition(
withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
0)))
        recyclerView.perform(actionOnItemAtPosition<ViewHolder>(1, click()))

            Thread.sleep(1000)
        
        val textView9 = onView(
allOf(withText("Detalles del resultado"),
withParent(allOf(withId(R.id.action_bar),
withParent(withId(R.id.action_bar_container)))),
isDisplayed()))
        textView9.check(matches(withText("Detalles del resultado")))
        
        val textView10 = onView(
allOf(withText("Datos generales"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView10.check(matches(withText("Datos generales")))
        
        val textView11 = onView(
allOf(withText("Valores medios"),
withParent(withParent(IsInstanceOf.instanceOf(android.view.ViewGroup::class.java))),
isDisplayed()))
        textView11.check(matches(withText("Valores medios")))

        val textView12 = onView(
allOf(withId(R.id.generalRiskTitle),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView12.check(matches(withText("Riesgo")))
        
        val textView13 = onView(
allOf(withId(R.id.generalRiskValue),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView13.check(matches(withText("65,84 %")))

        val textView14 = onView(
allOf(
        withId(R.id.generalExposeTimeTitle),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView14.check(matches(withText("Tiempo de exposición")))
        
        val textView15 = onView(
allOf(
        withId(R.id.generalExposeTimeValue),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView15.check(matches(withText("0 min 25 sec")))
        
        val textView16 = onView(
allOf(withId(R.id.generalProximityTitle),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView16.check(matches(withText("Proximidad")))
        
        val textView17 = onView(
allOf(
        withId(R.id.generalProximityValue),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView17.check(matches(withText("5,27 m")))
        
        val textView18 = onView(
allOf(withId(R.id.numberOfContactsDetails),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView18.check(matches(withText("1")))
        
        val textView19 = onView(
allOf(withText("contacto de riesgo con"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView19.check(matches(withText("contacto de riesgo con")))
        
        val textView20 = onView(
allOf(withId(R.id.numberOfPositivesDetails),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView20.check(matches(withText("1")))
        
        val textView21 = onView(
allOf(withText("positivo"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView21.check(matches(withText("positivo")))
        
        val textView22 = onView(
allOf(withText("Contactos de riesgo"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView22.check(matches(withText("Contactos de riesgo")))
        
        val textView23 = onView(
allOf(withText("Positivo 1"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView23.check(matches(withText("Positivo 1")))
        
        val textView24 = onView(
allOf(withText("Inicio"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView24.check(matches(withText("Inicio")))
        
        val textView25 = onView(
allOf(withId(R.id.txtContactStartHour), withText("12:02:25"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView25.check(matches(withText("12:02:25")))
        
        val textView26 = onView(
allOf(withText("Fin"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView26.check(matches(withText("Fin")))
        
        val textView27 = onView(
allOf(withId(R.id.txtContactEndHour), withText("12:02:50"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView27.check(matches(withText("12:02:50")))
        
        val textView28 = onView(
allOf(withId(R.id.exposeTimeRiskContactCardTitle),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView28.check(matches(withText("Tiempo de exposición")))
        
        val textView29 = onView(
allOf(withId(R.id.txtContactExposeTime), withText("0 min 25 sec"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView29.check(matches(withText("0 min 25 sec")))
        
        val textView30 = onView(
allOf(withId(R.id.proximityRiskContactCardTitle),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView30.check(matches(withText("Proximidad media")))
        
        val textView31 = onView(
allOf(withId(R.id.txtContactMeanProximity), withText("5,27 m"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView31.check(matches(withText("5,27 m")))
        
        val textView32 = onView(
allOf(
        withId(R.id.riskRiskContactCard),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView32.check(matches(withText("RIESGO")))
        
        val textView33 = onView(
allOf(withId(R.id.txtRiskPercent), withText("65,84 %"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView33.check(matches(withText("65,84 %")))
        
        val button = onView(
allOf(withId(R.id.btnShowContactInMap), withText("Mapa"),
withParent(withParent(withId(R.id.cardRiskContact))),
isDisplayed()))
        button.check(matches(isDisplayed()))
        Thread.sleep(2000)
        val appCompatImageButton = onView(
allOf(withContentDescription("Desplazarse hacia arriba"),
childAtPosition(
allOf(withId(R.id.action_bar),
childAtPosition(
withId(R.id.action_bar_container),
0)),
2),
isDisplayed()))
        appCompatImageButton.perform(click())
        
         // Added a sleep statement to match the app's execution delay.
 // The recommended way to handle such scenarios is to use Espresso idling resources:
  // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
Thread.sleep(1000)
        
        val textView34 = onView(
allOf(withText("65,84 %"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView34.check(matches(withText("65,84 %")))
        
        val textView35 = onView(
allOf(withText("Riesgo más alto"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView35.check(matches(withText("Riesgo más alto")))
        
        val textView36 = onView(
allOf(withText("Positivos"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView36.check(matches(withText("Positivos")))
        
        val textView37 = onView(
allOf(withId(R.id.numberOfContactPositives),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView37.check(matches(withText("1")))
        
        val textView38 = onView(
allOf(withText("Contactos de riesgo"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView38.check(matches(withText("Contactos de riesgo")))
        
        val textView39 = onView(
allOf(withId(R.id.numberOfContacts),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView39.check(matches(withText("1")))
        
        val textView40 = onView(
allOf(withText("Contactos de Riesgo"),
withParent(allOf(withId(R.id.action_bar),
withParent(withId(R.id.action_bar_container)))),
isDisplayed()))
        textView40.check(matches(withText("Contactos de Riesgo")))
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
