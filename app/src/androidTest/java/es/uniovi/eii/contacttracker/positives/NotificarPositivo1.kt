package es.uniovi.eii.contacttracker.positives


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
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class NotificarPositivo1 {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun notificarPositivo1() {
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

        Thread.sleep(800)

        val textView = onView(
allOf(withText("Notificar un positivo"),
withParent(allOf(withId(R.id.action_bar),
withParent(withId(R.id.action_bar_container)))),
isDisplayed()))
        textView.check(matches(withText("Notificar un positivo")))
        
        val textView2 = onView(
allOf(withId(R.id.textView1), withText("Registra que has dado positivo en la aplicación para que tus localizaciones registradas en los últimos días se suban a la nube."),
withParent(withParent(withId(R.id.nav_host_fragment))),
isDisplayed()))
        textView2.check(matches(withText("Registra que has dado positivo en la aplicación para que tus localizaciones registradas en los últimos días se suban a la nube.")))
        
        val textView3 = onView(
allOf(withId(R.id.textView2), withText("Opcionalmente puedes adjuntar tus datos personales, los cuales quedarán asociados a tus localizaciones. De este modo, contribuyes a facilitar las tareas de rastreo realizadas por el personal sanitario."),
withParent(withParent(withId(R.id.nav_host_fragment))),
isDisplayed()))
        textView3.check(matches(withText("Opcionalmente puedes adjuntar tus datos personales, los cuales quedarán asociados a tus localizaciones. De este modo, contribuyes a facilitar las tareas de rastreo realizadas por el personal sanitario.")))
        
        val checkBox = onView(
allOf(withId(R.id.checkBoxAddPersonalData), withText("Adjuntar mis datos personales"),
withParent(withParent(withId(R.id.nav_host_fragment))),
isDisplayed()))
        checkBox.check(matches(isDisplayed()))
        checkBox.check(matches(not(isChecked())))
        
        val textView4 = onView(
allOf(withId(R.id.textView3), withText("Tus datos personales están protegidos de acuerdo con nuestra política de privacidad."),
withParent(withParent(withId(R.id.nav_host_fragment))),
isDisplayed()))
        textView4.check(matches(withText("Tus datos personales están protegidos de acuerdo con nuestra política de privacidad.")))
        
        val button = onView(
allOf(withId(R.id.btnPrivacyPolicy), withText("Política de Privacidad"),
withParent(withParent(withId(R.id.nav_host_fragment))),
isDisplayed()))
        button.check(matches(isDisplayed()))
        
        val textView5 = onView(
allOf(withId(R.id.textView4), withText("Se subirán a la nube todas tus localizaciones registradas en los últimos:"),
withParent(allOf(withId(R.id.linearLayout4),
withParent(IsInstanceOf.instanceOf(android.view.ViewGroup::class.java)))),
isDisplayed()))
        textView5.check(matches(withText("Se subirán a la nube todas tus localizaciones registradas en los últimos:")))
        
        val textView6 = onView(
allOf(withId(R.id.txtInfectivityPeriod), withText("3 días"),
withParent(allOf(withId(R.id.linearLayout4),
withParent(IsInstanceOf.instanceOf(android.view.ViewGroup::class.java)))),
isDisplayed()))
        textView6.check(matches(withText("3 días")))
        
        val button2 = onView(
allOf(withId(R.id.btnNotifyPositive), withText("HE DADO POSITIVO"),
withParent(allOf(withId(R.id.linearLayout3),
withParent(IsInstanceOf.instanceOf(android.view.ViewGroup::class.java)))),
isDisplayed()))
        button2.check(matches(isDisplayed()))
        
        val materialCheckBox = onView(
allOf(withId(R.id.checkBoxAddPersonalData), withText("Adjuntar mis datos personales"),
childAtPosition(
childAtPosition(
withId(R.id.nav_host_fragment),
0),
2),
isDisplayed()))
        materialCheckBox.perform(click())
        materialCheckBox.check(matches(isChecked()))
        
        val chip = onView(
allOf(withId(R.id.btnPrivacyPolicy), withText("Política de Privacidad"),
childAtPosition(
childAtPosition(
withId(R.id.nav_host_fragment),
0),
4),
isDisplayed()))
        chip.perform(click())
        Thread.sleep(500)

        val textView7 = onView(
allOf(withText("Política de Privacidad"),
withParent(allOf(withId(R.id.action_bar),
withParent(withId(R.id.action_bar_container)))),
isDisplayed()))
        textView7.check(matches(withText("Política de Privacidad")))
        
        val textView8 = onView(
allOf(withText("Dueño de la aplicación"),
withParent(withParent(withId(R.id.privacyPolicyScroll))),
isDisplayed()))
        textView8.check(matches(withText("Dueño de la aplicación")))
        
        val textView9 = onView(
allOf(withText("El dueño y responsable de la aplicación Contact Tracker es Alejandro Flórez Muñiz, en el cual reside total responsabilidad sobre el tratamiento de los datos de los interesados.El dueño y responsable de la aplicación Contact Tracker es Alejandro Flórez Muñiz, en el cual reside total responsabilidad sobre el tratamiento de los datos de los interesados."),
withParent(withParent(withId(R.id.privacyPolicyScroll))),
isDisplayed()))
        textView9.check(matches(withText("El dueño y responsable de la aplicación Contact Tracker es Alejandro Flórez Muñiz, en el cual reside total responsabilidad sobre el tratamiento de los datos de los interesados.El dueño y responsable de la aplicación Contact Tracker es Alejandro Flórez Muñiz, en el cual reside total responsabilidad sobre el tratamiento de los datos de los interesados.")))
        
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
        Thread.sleep(600)

        val textView10 = onView(
allOf(withText("Notificar un positivo"),
withParent(allOf(withId(R.id.action_bar),
withParent(withId(R.id.action_bar_container)))),
isDisplayed()))
        textView10.check(matches(withText("Notificar un positivo")))
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
