package es.uniovi.eii.contacttracker.history


import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
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

// CONSTANTES
private const val MONTH = "10" // Mes en el que se ejecuta la prueba.

@LargeTest
@RunWith(AndroidJUnit4::class)
class Historico3 {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun historico3() {
         // Added a sleep statement to match the app's execution delay.
 // The recommended way to handle such scenarios is to use Espresso idling resources:
  // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
Thread.sleep(1000)
        
        val bottomNavigationItemView = onView(
allOf(withId(R.id.locationHistory), withContentDescription("Histórico"),
childAtPosition(
childAtPosition(
withId(R.id.bottomNavigationView),
0),
3),
isDisplayed()))
        bottomNavigationItemView.perform(click())
        
        val textInputEditText = onView(
allOf(withId(R.id.txtInputEditTextHistoryDate),
childAtPosition(
childAtPosition(
withId(R.id.txtInputLayoutHistoryDate),
0),
1),
isDisplayed()))
        textInputEditText.perform(click())
        Thread.sleep(500)
        // Establecer el día que tiene localizaciones
        setDay(4)
        
        val materialButton = onView(
allOf(withId(R.id.confirm_button), withText("Aceptar"),
childAtPosition(
allOf(withId(R.id.date_picker_actions),
childAtPosition(
withId(R.id.mtrl_calendar_main_pane),
1)),
1),
isDisplayed()))
        materialButton.perform(click())
        Thread.sleep(400)

        val editText = onView(
allOf(withId(R.id.txtInputEditTextHistoryDate), withText("04/${MONTH}/2021"),
withParent(withParent(withId(R.id.txtInputLayoutHistoryDate))),
isDisplayed()))
        editText.check(matches(withText("04/${MONTH}/2021")))
        
        val textView = onView(
allOf(withId(R.id.txtNumberOfLocations), withText("34"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView.check(matches(withText("34")))
        
        val textView2 = onView(
allOf(withText("localizaciones"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
isDisplayed()))
        textView2.check(matches(withText("localizaciones")))
        
        val button = onView(
allOf(withId(R.id.btnShowMap), withText("Mapa"),
withParent(withParent(withId(R.id.historyDataBox))),
isDisplayed()))
        button.check(matches(isDisplayed()))
        
        val imageButton = onView(
allOf(withId(R.id.btnDeleteLocationsByDate), withContentDescription("Eliminar localizaciones por fecha"),
withParent(withParent(withId(R.id.historyDataBox))),
isDisplayed()))
        imageButton.check(matches(isDisplayed()))
        
        val appCompatImageButton = onView(
allOf(withId(R.id.btnDeleteLocationsByDate), withContentDescription("Eliminar localizaciones por fecha"),
childAtPosition(
childAtPosition(
withId(R.id.historyDataBox),
0),
2),
isDisplayed()))
        // Eliminar las localizaciones
        appCompatImageButton.perform(click())
        Thread.sleep(500)

        val editText2 = onView(
allOf(withId(R.id.txtInputEditTextHistoryDate),
withParent(withParent(withId(R.id.txtInputLayoutHistoryDate))),
isDisplayed()))
        editText2.check(matches(withText("04/${MONTH}/2021")))
        
        val textView3 = onView(
allOf(withId(R.id.labelHistoryNoLocations), withText("No hay localizaciones que mostrar"),
withParent(withParent(withId(R.id.nav_host_fragment))),
isDisplayed()))
        textView3.check(matches(withText("No hay localizaciones que mostrar")))
        
        val textInputEditText2 = onView(
allOf(withId(R.id.txtInputEditTextHistoryDate),
childAtPosition(
childAtPosition(
withId(R.id.txtInputLayoutHistoryDate),
0),
1),
isDisplayed()))
        textInputEditText2.perform(click())
        Thread.sleep(500)
        // Cambiar al día 05
        setDay(5)
        
        val materialButton2 = onView(
allOf(withId(R.id.confirm_button), withText("Aceptar"),
childAtPosition(
allOf(withId(R.id.date_picker_actions),
childAtPosition(
withId(R.id.mtrl_calendar_main_pane),
1)),
1),
isDisplayed()))
        materialButton2.perform(click())

        Thread.sleep(400)

        val textInputEditText3 = onView(
allOf(withId(R.id.txtInputEditTextHistoryDate), withText("05/${MONTH}/2021"),
childAtPosition(
childAtPosition(
withId(R.id.txtInputLayoutHistoryDate),
0),
1),
isDisplayed()))
        textInputEditText3.perform(click())
        Thread.sleep(500)
        // Volver al día 04
        setDay(4)
        
        val materialButton3 = onView(
allOf(withId(R.id.confirm_button), withText("Aceptar"),
childAtPosition(
allOf(withId(R.id.date_picker_actions),
childAtPosition(
withId(R.id.mtrl_calendar_main_pane),
1)),
1),
isDisplayed()))
        materialButton3.perform(click())
        
        val editText3 = onView(
allOf(withId(R.id.txtInputEditTextHistoryDate), withText("04/${MONTH}/2021"),
withParent(withParent(withId(R.id.txtInputLayoutHistoryDate))),
isDisplayed()))
        editText3.check(matches(withText("04/${MONTH}/2021")))
        
        val textView4 = onView(
allOf(withId(R.id.labelHistoryNoLocations), withText("No hay localizaciones que mostrar"),
withParent(withParent(withId(R.id.nav_host_fragment))),
isDisplayed()))
        textView4.check(matches(withText("No hay localizaciones que mostrar")))
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

    /**
     * Establece un día en el DatePicker.
     */
    private fun setDay(day: Int) {
        onView(
            allOf(
                isDescendantOfA(withTagValue(equalTo("MONTHS_VIEW_GROUP_TAG"))),
                isCompletelyDisplayed(),
                withText(day.toString())
            )
        ).perform(click())
    }

    private fun atPosition(position: Int, @NonNull itemMatcher: Matcher<View?>): Matcher<View?>? {
        checkNotNull(itemMatcher)
        return object : BoundedMatcher<View?, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("has item at position $position: ")
                itemMatcher.describeTo(description)
            }

            override fun matchesSafely(view: RecyclerView): Boolean {
                val viewHolder = view.findViewHolderForAdapterPosition(position)
                    ?: // has no item on such position
                    return false
                return itemMatcher.matches(viewHolder.itemView)
            }
        }
    }
    }
