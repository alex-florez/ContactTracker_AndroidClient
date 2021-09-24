package es.uniovi.eii.contacttracker.location


import es.uniovi.eii.contacttracker.TestUtils.parseLocations
import es.uniovi.eii.contacttracker.model.Itinerary
import es.uniovi.eii.contacttracker.model.UserLocation
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.text.SimpleDateFormat

/**
 * Clase de tests Unitarios para comprobar el funcionamiento
 * de la clase Itinerary.
 */
class ItineraryUnitTest {

    /* Listas de localizaciones */
    private lateinit var oneDayLocations: List<UserLocation>
    private lateinit var twoDayLocations: List<UserLocation>
    private lateinit var multipleDayLocations: List<UserLocation>

    /* Date formatter */
    private val df = SimpleDateFormat("yyyy-MM-dd")

    @Before
    fun setUp() {
        oneDayLocations = parseLocations("itinerary9.txt")
        twoDayLocations = parseLocations("itinerary10.txt")
        multipleDayLocations = parseLocations("itinerary11.txt")
    }

    /* Código: I1 */
    @Test
    fun `crear itinerario con localizaciones registradas en un dia`() {
        assertEquals(15, oneDayLocations.size)
        val itinerary = Itinerary(oneDayLocations, "oneDay")
        // Comprobar lista de fechas distintas
        val dates = itinerary.dates()
        assertEquals(1, dates.size)
        assertEquals("2021-09-24", dates[0])
        // Comprobar localizaciones
        val locations = itinerary["2021-09-24"]
        assertEquals(15, locations.size)
        locations.forEach {
            assertEquals("2021-09-24", df.format(it.timestamp()))
        }
    }

    /* Código: I2 */
    @Test
    fun `crear itinerario con localizaciones registradas en dos dias distintos`() {
        assertEquals(15, twoDayLocations.size)
        val itinerary = Itinerary(twoDayLocations, "twoDay")
        // Comprobar lista de fechas distintas
        val dates = itinerary.dates()
        assertEquals(2, dates.size)
        assertEquals("2021-09-23", dates[0])
        assertEquals("2021-09-24", dates[1])

        // Comprobar localizaciones
        val locations1 = itinerary["2021-09-23"]
        val locations2 = itinerary["2021-09-24"]
        assertEquals(9, locations1.size)
        assertEquals(6, locations2.size)
        locations1.forEach {
            assertEquals("2021-09-23", df.format(it.timestamp()))
        }
        locations2.forEach {
            assertEquals("2021-09-24", df.format(it.timestamp()))
        }
    }

    /* Código: I3 */
    @Test
    fun `crear itinerario con localizaciones registradas en multiples dias distintos`() {
        assertEquals(15, multipleDayLocations.size)
        val itinerary = Itinerary(multipleDayLocations, "multipleDay")
        // Comprobar lista de fechas distintas
        val dates = itinerary.dates()
        assertEquals(4, dates.size)
        assertEquals("2021-09-20", dates[0])
        assertEquals("2021-09-23", dates[1])
        assertEquals("2021-09-24", dates[2])
        assertEquals("2021-09-25", dates[3])

        // Comprobar localizaciones
        val locations1 = itinerary["2021-09-20"]
        val locations2 = itinerary["2021-09-23"]
        val locations3 = itinerary["2021-09-24"]
        val locations4 = itinerary["2021-09-25"]

        assertEquals(6, locations1.size)
        assertEquals(3, locations2.size)
        assertEquals(4, locations3.size)
        assertEquals(2, locations4.size)


        locations1.forEach {
            assertEquals("2021-09-20", df.format(it.timestamp()))
        }
        locations2.forEach {
            assertEquals("2021-09-23", df.format(it.timestamp()))
        }
        locations3.forEach {
            assertEquals("2021-09-24", df.format(it.timestamp()))
        }
        locations4.forEach {
            assertEquals("2021-09-25", df.format(it.timestamp()))
        }
    }


}