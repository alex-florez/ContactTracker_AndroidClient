package es.uniovi.eii.contacttracker

import android.util.Log
import es.uniovi.eii.contacttracker.model.Itinerary
import es.uniovi.eii.contacttracker.util.readFile
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    var lines: List<String> = listOf()

    @Before
    fun init(){
        read()
    }
    @Test
    fun addition_isCorrect() {
        assertEquals("linea1", lines[0])
    }

    private fun read(){
        javaClass.classLoader?.getResourceAsStream("prueba.txt")?.bufferedReader().use {
            if(it != null)
                lines = it.readLines()
        }
    }
}