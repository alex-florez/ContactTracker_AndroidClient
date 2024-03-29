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
import com.google.android.material.textfield.TextInputLayout
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.activities.MainActivity
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class NotificarPositivo3 {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun notificarPositivo3() {
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
        
        val materialCheckBox = onView(
allOf(withId(R.id.checkBoxAddPersonalData), withText("Adjuntar mis datos personales"),
childAtPosition(
childAtPosition(
withId(R.id.nav_host_fragment),
0),
2),
isDisplayed()))
        materialCheckBox.perform(click())
        
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
        
        val materialButton2 = onView(
allOf(withId(android.R.id.button1), withText("Aceptar"),
childAtPosition(
childAtPosition(
withId(R.id.buttonPanel),
0),
3)))
        materialButton2.perform(scrollTo(), click())

        onView(withId(R.id.txtNameLayout))
            .check(matches(hasTextInputError("Este campo es obligatorio")))

        onView(withId(R.id.txtSurnameLayout))
            .check(matches(hasTextInputError("Este campo es obligatorio")))

        onView(withId(R.id.txtDNILayout))
            .check(matches(hasTextInputError("Este campo es obligatorio")))

        onView(withId(R.id.txtPhoneNumberLayout))
            .check(matches(hasTextInputError("Este campo es obligatorio")))

        onView(withId(R.id.txtCityLayout))
            .check(matches(hasTextInputError("Este campo es obligatorio")))

        onView(withId(R.id.txtCPLayout))
            .check(matches(hasTextInputError("Este campo es obligatorio")))

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

    private fun hasTextInputError(errorText: String): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description?) {
            }

            override fun matchesSafely(item: View?): Boolean {
               if(item !is TextInputLayout)
                   return false

                val error = (item as TextInputLayout).error ?: return false
                return errorText == error.toString()
            }
        }
    }
}
