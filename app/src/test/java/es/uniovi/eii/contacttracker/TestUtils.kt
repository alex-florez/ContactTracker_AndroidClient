package es.uniovi.eii.contacttracker

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import es.uniovi.eii.contacttracker.model.Itinerary
import es.uniovi.eii.contacttracker.model.Point
import es.uniovi.eii.contacttracker.model.UserLocation
import java.text.SimpleDateFormat
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/* Date Formatter */
private val df = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

/* Funciones de Utilidad para los Tests Unitarios */
object TestUtils {
    /**
     * Lee las líneas del fichero de nombre indicado y
     * devuelve una lista de Strings con cada una de las líneas.
     *
     * @param filename Nombre del fichero.
     * @return Lista con las líneas del fichero.
     */
    fun readFile(filename: String): List<String>{
        var list = listOf<String>()
        javaClass.classLoader?.getResourceAsStream(filename)?.bufferedReader()
            .use {
                if(it != null)
                    list = it.readLines()
            }
        return list
    }

    /**
     * Lee los datos del fichero de nombre indicado y
     * construye un Itinerario con las localizaciones.
     *
     * @param filename Nombre del fichero.
     * @return Objeto Itinerary.
     */
    fun parseItinerary(filename: String): Itinerary {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd")
        val lines = readFile(filename)
        val locations = mutableListOf<UserLocation>()
        var count = 1
        lines.forEach {
            val data = it.split(",")
            val lng = data[0].toDouble()
            val lat = data[1].toDouble()
            val date = df.parse(data[2])
            locations.add(UserLocation(count.toLong(), Point(lat, lng, date!!), 0.0,""))
            count++
        }
        // Crear itinerario
        return Itinerary(locations, filename)
    }
}

/**
 *  Extension Function para observar los cambios del LiveData.
 *  [https://medium.com/swlh/unit-testing-with-kotlin-coroutines-the-android-way-19289838d257]
 */
@VisibleForTesting(otherwise = VisibleForTesting.NONE)
fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    afterObserve: () -> Unit = {}
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            data = o
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }
    this.observeForever(observer)

    try {
        afterObserve.invoke()

        // Don't wait indefinitely if the LiveData is not set.
        if (!latch.await(time, timeUnit)) {
            throw TimeoutException("LiveData value was never set.")
        }

    } finally {
        this.removeObserver(observer)
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}

