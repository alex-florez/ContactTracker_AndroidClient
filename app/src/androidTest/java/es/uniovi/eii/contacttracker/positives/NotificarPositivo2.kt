package es.uniovi.eii.contacttracker.positives


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.*
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
class NotificarPositivo2 {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun notificarPositivo2() {
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


        val addPersonalDataCB = onView(
            allOf(withId(R.id.checkBoxAddPersonalData), withText("Adjuntar mis datos personales"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.nav_host_fragment),
                        0),
                    2),
                isDisplayed()))
        addPersonalDataCB.check(matches(not(isChecked())))


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

        Thread.sleep(500)

        val textView = onView(
allOf(withText("¿Has tenido síntomas de COVID-19?"),
withParent(withParent(withId(android.R.id.custom))),
isDisplayed()))
        textView.check(matches(withText("¿Has tenido síntomas de COVID-19?")))
        
        val compoundButton = onView(
allOf(withId(R.id.q1Yes), withText("Sí"),
withParent(allOf(withId(R.id.question1),
withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java)))),
isDisplayed()))
        compoundButton.check(matches(isDisplayed()))
        compoundButton.check(matches(isChecked()))
        
        val compoundButton2 = onView(
allOf(withId(R.id.q1No), withText("No, soy asintomático."),
withParent(allOf(withId(R.id.question1),
withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java)))),
isDisplayed()))
        compoundButton2.check(matches(isDisplayed()))
        
        val compoundButton3 = onView(
allOf(withId(R.id.q2Yes), withText("Sí"),
withParent(allOf(withId(R.id.question2),
withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java)))),
isDisplayed()))
        compoundButton3.check(matches(isDisplayed()))
        compoundButton3.check(matches(isChecked()))
        
        val compoundButton4 = onView(
allOf(withId(R.id.q2No), withText("No, aún no estoy vacunado."),
withParent(allOf(withId(R.id.question2),
withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java)))),
isDisplayed()))
        compoundButton4.check(matches(isDisplayed()))
        
        val materialButton2 = onView(
allOf(withId(android.R.id.button2), withText("Cancelar"),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.ScrollView")),
0),
2)))
        materialButton2.perform(scrollTo(), click())
        
        val materialCheckBox = onView(
allOf(withId(R.id.checkBoxAddPersonalData), withText("Adjuntar mis datos personales"),
childAtPosition(
childAtPosition(
withId(R.id.nav_host_fragment),
0),
2),
isDisplayed()))
        materialCheckBox.perform(click())
        
        val checkBox = onView(
allOf(withId(R.id.checkBoxAddPersonalData), withText("Adjuntar mis datos personales"),
withParent(withParent(withId(R.id.nav_host_fragment))),
isDisplayed()))
        checkBox.check(matches(isChecked()))
        
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

        Thread.sleep(500)

        val textView2 = onView(
allOf(withText("Introduce tus datos personales"),
withParent(withParent(withId(R.id.custom))),
isDisplayed()))
        textView2.check(matches(withText("Introduce tus datos personales")))
        
        val textView3 = onView(
allOf(withText("Al introducir tus datos estás facilitando las actividades de rastreo de COVID-19. Tus datos personales quedarán asociados a las localizaciones que se suban a la nube."),
withParent(withParent(withId(R.id.custom))),
isDisplayed()))
        textView3.check(matches(withText("Al introducir tus datos estás facilitando las actividades de rastreo de COVID-19. Tus datos personales quedarán asociados a las localizaciones que se suban a la nube.")))
        
        val textInputEditText = onView(
allOf(withId(R.id.txtNameEditText),
childAtPosition(
childAtPosition(
withId(R.id.txtNameLayout),
0),
0),
isDisplayed()))
        textInputEditText.perform(click())
        
        val textInputEditText2 = onView(
allOf(withId(R.id.txtNameEditText),
childAtPosition(
childAtPosition(
withId(R.id.txtNameLayout),
0),
0),
isDisplayed()))
        textInputEditText2.perform(replaceText("Alejandro"), closeSoftKeyboard())

        val surnameLayout = onView(withId(R.id.txtSurnameLayout))
        surnameLayout.perform(click())

        val textInputEditText3 = onView(
allOf(withId(R.id.txtSurnameEditText),
childAtPosition(
childAtPosition(
withId(R.id.txtSurnameLayout),
0),
0),
isDisplayed()))
        textInputEditText3.perform(click())
        textInputEditText3.perform(replaceText("Flórez"), closeSoftKeyboard())

        val dniLayout = onView(withId(R.id.txtDNILayout))
        dniLayout.perform(click())

        val textInputEditText4 = onView(
allOf(withId(R.id.txtDNIEditText),
childAtPosition(
childAtPosition(
withId(R.id.txtDNILayout),
0),
0),
isDisplayed()))
        textInputEditText4.perform(replaceText("12333324H"), closeSoftKeyboard())

        val numberLayout = onView(withId(R.id.txtPhoneNumberLayout))
        numberLayout.perform(click())

        val textInputEditText5 = onView(
allOf(withId(R.id.txtPhoneNumberEditText),
childAtPosition(
childAtPosition(
withId(R.id.txtPhoneNumberLayout),
0),
0),
isDisplayed()))
        textInputEditText5.perform(replaceText("695828829"), closeSoftKeyboard())

        val cityLayout = onView(withId(R.id.txtCityLayout))
        cityLayout.perform(click())

        val textInputEditText6 = onView(
allOf(withId(R.id.txtCityEditText),
childAtPosition(
childAtPosition(
withId(R.id.txtCityLayout),
0),
0),
isDisplayed()))
        textInputEditText6.perform(replaceText("Avilés"), closeSoftKeyboard())

        val cpLayout = onView(withId(R.id.txtCPLayout))
        cpLayout.perform(click())

        val textInputEditText7 = onView(
allOf(withId(R.id.txtCPEditText),
childAtPosition(
childAtPosition(
withId(R.id.txtCPLayout),
0),
0),
isDisplayed()))
        textInputEditText7.perform(replaceText("33400"), closeSoftKeyboard())

        
        val materialButton4 = onView(
allOf(withId(android.R.id.button1), withText("Aceptar"),
childAtPosition(
childAtPosition(
withId(R.id.buttonPanel),
0),
3)))
        materialButton4.perform(scrollTo(), click())
        Thread.sleep(500)
        
        val textView4 = onView(
allOf(withText("Consentimiento de Protección de Datos"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
isDisplayed()))
        textView4.check(matches(withText("Consentimiento de Protección de Datos")))
        
        val textView5 = onView(
allOf(withText("Esta aplicación utiliza tus datos personales asociados a las localizaciones registradas para facilitar y mejorar las tareas de rastreo por parte de las autoridades sanitarias. Estos datos personales serán subidos a la nube una vez notifiques un resultado positivo y quedarán asociados a tus ubicaciones."),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
isDisplayed()))
        textView5.check(matches(withText("Esta aplicación utiliza tus datos personales asociados a las localizaciones registradas para facilitar y mejorar las tareas de rastreo por parte de las autoridades sanitarias. Estos datos personales serán subidos a la nube una vez notifiques un resultado positivo y quedarán asociados a tus ubicaciones.")))
        
        val textView6 = onView(
allOf(withText("Si lo deseas puedes optar por no indicar tus datos personales y notificar que has dado positivo de manera anónima."),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
isDisplayed()))
        textView6.check(matches(withText("Si lo deseas puedes optar por no indicar tus datos personales y notificar que has dado positivo de manera anónima.")))
        
        val textView7 = onView(
allOf(withText("De acuerdo con lo establecido en la normativa vigente sobre protección de datos de la LOPD/GDD de 2018 necesitamos tu consentimiento voluntario para llevar a cabo el tratamiento de dichos datos personales de forma lícita."),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
isDisplayed()))
        textView7.check(matches(withText("De acuerdo con lo establecido en la normativa vigente sobre protección de datos de la LOPD/GDD de 2018 necesitamos tu consentimiento voluntario para llevar a cabo el tratamiento de dichos datos personales de forma lícita.")))
        
        val textView8 = onView(
allOf(withText("Al aceptar, estás confirmando que tienes más de 14 años y que estás de acuerdo con el tratamiento de los datos personales aportados y con nuestra Política de Privacidad."),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
isDisplayed()))
        textView8.check(matches(withText("Al aceptar, estás confirmando que tienes más de 14 años y que estás de acuerdo con el tratamiento de los datos personales aportados y con nuestra Política de Privacidad.")))
        
        val button = onView(
allOf(withId(R.id.acceptAgreement), withText("Sí, estoy de acuerdo"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        button.check(matches(isDisplayed()))
        
        val button2 = onView(
allOf(withId(R.id.rejectAgreement), withText("NO, GRACIAS"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        button2.check(matches(isDisplayed()))
        
        val materialButton5 = onView(
allOf(withId(R.id.rejectAgreement), withText("No, gracias"),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.LinearLayout")),
5),
1)))
        materialButton5.perform(scrollTo(), click())
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
