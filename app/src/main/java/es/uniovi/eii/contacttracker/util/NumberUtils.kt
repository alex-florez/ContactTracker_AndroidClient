package es.uniovi.eii.contacttracker.util

import kotlin.math.pow
import kotlin.math.roundToLong

object NumberUtils {

    /**
     * Redondea el valor pasado como parámetro con el número de
     * decimales indicado.
     *
     * @param value Valor a redondear.
     * @param decimals Número de decimales.
     * @return Valor redondeado.
     */
    fun round(value: Double, decimals: Int): Double {
        val multiplier = 10.0.pow(decimals)
        return (value * multiplier).roundToLong() / multiplier
    }
}