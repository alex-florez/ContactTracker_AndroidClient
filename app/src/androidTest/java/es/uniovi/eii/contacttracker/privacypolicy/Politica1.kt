package es.uniovi.eii.contacttracker.privacypolicy


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAction
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
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class Politica1 {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun politica1() {
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
allOf(withId(R.id.title), withText("Política de Privacidad"),
childAtPosition(
childAtPosition(
withId(R.id.content),
0),
0),
isDisplayed()))
        materialTextView.perform(click())
        
        val textView = onView(
allOf(withText("Dueño de la aplicación"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
isDisplayed()))
        textView.check(matches(withText("Dueño de la aplicación")))
        
        val textView2 = onView(
allOf(withText("El dueño y responsable de la aplicación Contact Tracker es Alejandro Flórez Muñiz, en el cual reside total responsabilidad sobre el tratamiento de los datos de los interesados.El dueño y responsable de la aplicación Contact Tracker es Alejandro Flórez Muñiz, en el cual reside total responsabilidad sobre el tratamiento de los datos de los interesados."),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
isDisplayed()))
        textView2.check(matches(withText("El dueño y responsable de la aplicación Contact Tracker es Alejandro Flórez Muñiz, en el cual reside total responsabilidad sobre el tratamiento de los datos de los interesados.El dueño y responsable de la aplicación Contact Tracker es Alejandro Flórez Muñiz, en el cual reside total responsabilidad sobre el tratamiento de los datos de los interesados.")))
        
        val textView3 = onView(
allOf(withText("Responsable del tratamiento"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
isDisplayed()))
        textView3.check(matches(withText("Responsable del tratamiento")))
        
        val textView4 = onView(
allOf(withText("El tratamiento de los datos es responsabilidad del dueño de la aplicación, Alejandro Flórez Muñiz, por lo que no se deriva el tratamiento de los datos a entidades de terceros. Datos de contacto del responsable:"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
isDisplayed()))
        textView4.check(matches(withText("El tratamiento de los datos es responsabilidad del dueño de la aplicación, Alejandro Flórez Muñiz, por lo que no se deriva el tratamiento de los datos a entidades de terceros. Datos de contacto del responsable:")))
        
        val textView5 = onView(
allOf(withText("• Nombre y apellidos: Alejandro Flórez Muñiz."),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView5.check(matches(withText("• Nombre y apellidos: Alejandro Flórez Muñiz.")))
        
        val textView6 = onView(
allOf(withText("• Correo electrónico: alexflorezmuniz@gmail.com"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView6.check(matches(withText("• Correo electrónico: alexflorezmuniz@gmail.com")))
        
        val textView7 = onView(
allOf(withText("• Teléfono móvil: 695 82 28 79"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView7.check(matches(withText("• Teléfono móvil: 695 82 28 79")))
        
        val textView8 = onView(
allOf(withText("• Dirección: Villalegre, Avilés, Asturias"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView8.check(matches(withText("• Dirección: Villalegre, Avilés, Asturias")))
        
        val textView9 = onView(
allOf(withText("• País: España"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView9.check(matches(withText("• País: España")))
        
        val textView10 = onView(
allOf(withText("Datos recolectados"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
isDisplayed()))
        textView10.check(matches(withText("Datos recolectados")))
        
        val textView11 = onView(
allOf(withText("Esta aplicación recopila datos de geolocalización del dispositivo de forma periódica y mientras la aplicación está en segundo plano si el usuario lo permite. Los datos de geolocalización se recogen mediante los servicios de Google instalados y quedan registrados en el almacenamiento local del dispositivo. Opcionalmente, también se registran los datos personales del usuario, si este lo permite, quedando asociados a las ubicaciones registradas. Para los datos de geolocalización se registra:"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
isDisplayed()))
        textView11.check(matches(withText("Esta aplicación recopila datos de geolocalización del dispositivo de forma periódica y mientras la aplicación está en segundo plano si el usuario lo permite. Los datos de geolocalización se recogen mediante los servicios de Google instalados y quedan registrados en el almacenamiento local del dispositivo. Opcionalmente, también se registran los datos personales del usuario, si este lo permite, quedando asociados a las ubicaciones registradas. Para los datos de geolocalización se registra:")))
        
        val textView12 = onView(
allOf(withText("• Latitud y Longitud"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
            textView12.perform(scrollTo())
        textView12.check(matches(withText("• Latitud y Longitud")))
        
        val textView13 = onView(
allOf(withText("• Fecha y hora de la ubicación"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
            textView13.perform(scrollTo())
        Thread.sleep(800)
        textView13.check(matches(withText("• Fecha y hora de la ubicación")))

        onView(withId(R.id.privacyPolicyScroll)).perform(swipeUp())
//
//        val textView14 = onView(
//allOf(withText("Para los datos personales se registra:"),
//withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
//isDisplayed()))
//        textView14.check(matches(withText("Para los datos personales se registra:")))
//
//        val textView15 = onView(
//allOf(withText("• Nombre y apellidos"),
//withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
//isDisplayed()))
//            textView15.perform(scrollTo())
//        textView15.check(matches(withText("• Nombre y apellidos")))
//
//        val textView16 = onView(
//allOf(withText("• DNI"),
//withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
//isDisplayed()))
//            textView16.perform(scrollTo())
//        textView16.check(matches(withText("• DNI")))
//
//        val textView17 = onView(
//allOf(withText("• Número de teléfono"),
//withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
//isDisplayed()))
//            textView17.perform(scrollTo())
//        textView17.check(matches(withText("• Número de teléfono")))
//
//        val textView18 = onView(
//allOf(withText("• Localidad"),
//withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
//isDisplayed()))
//            textView18.perform(scrollTo())
//        textView18.check(matches(withText("• Localidad")))
//
//        val textView19 = onView(
//allOf(withText("• Código postal"),
//withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
//isDisplayed()))
//            textView19.perform(scrollTo())
//        textView19.check(matches(withText("• Código postal")))
//
//        val textView20 = onView(
//allOf(withText("Todos estos datos se almacenan en local hasta que el usuario da su consentimiento para subirlos a la nube y compartirlos con el resto de usuarios, aunque los datos personales asociados a los itinerarios solo son visibles por los administradores desde el panel de control web."),
//withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
//isDisplayed()))
//            textView20.perform(scrollTo())
//        textView20.check(matches(withText("Todos estos datos se almacenan en local hasta que el usuario da su consentimiento para subirlos a la nube y compartirlos con el resto de usuarios, aunque los datos personales asociados a los itinerarios solo son visibles por los administradores desde el panel de control web.")))
//
//        val textView21 = onView(
//allOf(withText("Permisos de la aplicación"),
//withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
//isDisplayed()))
//            textView21.perform(scrollTo())
//        textView21.check(matches(withText("Permisos de la aplicación")))
//
//        val textView22 = onView(
//allOf(withText("La aplicación solicita una serie de permisos para acceder a los servicios de ubicación del dispositivo:"),
//withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
//isDisplayed()))
//            textView22.perform(scrollTo())
//        textView22.check(matches(withText("La aplicación solicita una serie de permisos para acceder a los servicios de ubicación del dispositivo:")))
//
//        val textView23 = onView(
//allOf(withText("1. ACCESS_FINE_LOCATION: permite acceder a los datos sobre la ubicación precisa del usuario."),
//withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
//isDisplayed()))
//        textView23.check(matches(withText("1. ACCESS_FINE_LOCATION: permite acceder a los datos sobre la ubicación precisa del usuario.")))
//
//        val textView24 = onView(
//allOf(withText("2. Activar la ubicaci�n: se solicita al usuario que active el GPS del dispositivo para poder recibir actualizaciones de ubicaci�n."),
//withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
//isDisplayed()))
//        textView24.check(matches(withText("2. Activar la ubicaci�n: se solicita al usuario que active el GPS del dispositivo para poder recibir actualizaciones de ubicaci�n.")))
//
//        val textView25 = onView(
//allOf(withText("3. Localizaci�n en segundo plano: permite recopilar datos de ubicaci�n mientras la aplicaci�n no est� activa."),
//withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
//isDisplayed()))
//        textView25.check(matches(withText("3. Localizaci�n en segundo plano: permite recopilar datos de ubicaci�n mientras la aplicaci�n no est� activa.")))
//
//        val textView26 = onView(
//allOf(withText("Consentimiento del interesado"),
//withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
//isDisplayed()))
//        textView26.check(matches(withText("Consentimiento del interesado")))
//
//        val textView27 = onView(
//allOf(withText("Para realizar cualquier operaci�n que involucre el tratamiento de datos personales del usuario se solicita previamente su consentimiento inform�ndole sobre la pol�tica de privacidad."),
//withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
//isDisplayed()))
//        textView27.check(matches(withText("Para realizar cualquier operaci�n que involucre el tratamiento de datos personales del usuario se solicita previamente su consentimiento inform�ndole sobre la pol�tica de privacidad.")))
//
//        val textView28 = onView(
//allOf(withText("Antes de asociar los datos personales del usuario con sus itinerarios y subirlos a la nube, se muestra una cl�usula informativa para solicitar su consentimiento."),
//withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
//isDisplayed()))
//        textView28.check(matches(withText("Antes de asociar los datos personales del usuario con sus itinerarios y subirlos a la nube, se muestra una cl�usula informativa para solicitar su consentimiento.")))
//
//        val textView29 = onView(
//allOf(withText("Del mismo modo, para registrar las ubicaciones es necesario que el usuario conceda los permisos citados en el apartado anterior."),
//withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
//isDisplayed()))
//        textView29.check(matches(withText("Del mismo modo, para registrar las ubicaciones es necesario que el usuario conceda los permisos citados en el apartado anterior.")))
//
        Thread.sleep(2000)
        val textView30 = onView(
allOf(withText("Finalidad del tratamiento"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
isDisplayed()))
        textView30.check(matches(withText("Finalidad del tratamiento")))
        
        val textView31 = onView(
allOf(withText("El tratamiento de los datos tiene como objetivo facilitar las tareas de rastreo al personal sanitario al asociar los itinerarios del usuario con sus datos personales, de forma que los profesionales puedan contactar fácilmente con estos usuarios y visualizar las zonas por las que han estado."),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
isDisplayed()))
        textView31.check(matches(withText("El tratamiento de los datos tiene como objetivo facilitar las tareas de rastreo al personal sanitario al asociar los itinerarios del usuario con sus datos personales, de forma que los profesionales puedan contactar fácilmente con estos usuarios y visualizar las zonas por las que han estado.")))
        
        val textView32 = onView(
allOf(withText("Además, el registro de coordenadas tiene como finalidad almacenar las ubicaciones por las que ha transcurrido el usuario que ha dado positivo poniendo en peligro a otras personas, de modo que se pueda notificar a dichas personas sobre un posible contacto de riesgo."),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
isDisplayed()))
        textView32.check(matches(withText("Además, el registro de coordenadas tiene como finalidad almacenar las ubicaciones por las que ha transcurrido el usuario que ha dado positivo poniendo en peligro a otras personas, de modo que se pueda notificar a dichas personas sobre un posible contacto de riesgo.")))
        
        val textView33 = onView(
allOf(withText("Derechos del interesado"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
isDisplayed()))
        textView33.check(matches(withText("Derechos del interesado")))
        
        val textView34 = onView(
allOf(withText("Entre los derechos que el interesado dispone frente al tratamiento de sus datos personales, se encuentran:"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
isDisplayed()))
        textView34.check(matches(withText("Entre los derechos que el interesado dispone frente al tratamiento de sus datos personales, se encuentran:")))
        
        val textView35 = onView(
allOf(withText("• Derecho de acceso: consiste en el derecho del usuario para solicitar el acceso a sus datos personales que están siendo tratados. En el caso de que sus localizaciones hayan sido subidas a la nube de forma anónima sin ser asociadas a sus datos personales este derecho no será aplicable dada la anonimidad de los datos."),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView35.check(matches(withText("• Derecho de acceso: consiste en el derecho del usuario para solicitar el acceso a sus datos personales que están siendo tratados. En el caso de que sus localizaciones hayan sido subidas a la nube de forma anónima sin ser asociadas a sus datos personales este derecho no será aplicable dada la anonimidad de los datos.")))
        
        val textView36 = onView(
allOf(withText("• Derecho de rectificación: derecho del usuario para rectificar sus datos, es decir, para contactar con el responsable del tratamiento de forma que este corrija sus datos personales en caso de que no sean correctos o estén desactualizados."),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView36.check(matches(withText("• Derecho de rectificación: derecho del usuario para rectificar sus datos, es decir, para contactar con el responsable del tratamiento de forma que este corrija sus datos personales en caso de que no sean correctos o estén desactualizados.")))
        
        val textView37 = onView(
allOf(withText("• Derecho de cancelación: se refiere al derecho del usuario para eliminar sus datos personales del sistema."),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView37.check(matches(withText("• Derecho de cancelación: se refiere al derecho del usuario para eliminar sus datos personales del sistema.")))
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
