package es.uniovi.eii.contacttracker.activities


import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.model.NotifyPositiveConfig
import es.uniovi.eii.contacttracker.model.Point
import es.uniovi.eii.contacttracker.model.UserLocation
import es.uniovi.eii.contacttracker.repositories.ConfigRepository
import es.uniovi.eii.contacttracker.room.AppDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import java.util.Date

@LargeTest
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class MainActivityTest6 {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

//    @get:Rule
//    val testCoroutineRule = TestCoroutineRuleAndroid()

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    lateinit var configRepository: ConfigRepository

    private lateinit var db: AppDatabase
//
//    @Before
//    fun setUp() = testCoroutineRule.runBlockingTest {
////        val ctx = ApplicationProvider.getApplicationContext<Context>()
////        db = Room.inMemoryDatabaseBuilder(ctx, AppDatabase::class.java)
////            .allowMainThreadQueries()
////            .build()
////
////        db.userLocationDao().insert(
////            UserLocation(null, Point(43.55, -5.42, Date()), 0.0, "")
////        )
//        `when`(configRepository.getNotifyPositiveConfig()).thenReturn(
//            NotifyPositiveConfig(
//                10,
//                8
//            )
//        )
//    }

    @Test
    fun mainActivityTest6() {
         // Added a sleep statement to match the app's execution delay.
 // The recommended way to handle such scenarios is to use Espresso idling resources:
  // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
Thread.sleep(2000)
        
        val tabView = onView(
allOf(withContentDescription("Mapa"),
childAtPosition(
childAtPosition(
withId(R.id.trackLocationTabLayout),
0),
1),
isDisplayed()))
        tabView.perform(click())
        
        val chip = onView(
allOf(withId(R.id.chipShowMarkers), withText("Marcadores"),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.FrameLayout")),
0),
5),
isDisplayed()))
        chip.perform(click())
        
        val tabView2 = onView(
allOf(withContentDescription("Rastreador"),
childAtPosition(
childAtPosition(
withId(R.id.trackLocationTabLayout),
0),
0),
isDisplayed()))
        tabView2.perform(click())
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
