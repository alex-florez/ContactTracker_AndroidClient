package es.uniovi.eii.contacttracker.util

import es.uniovi.eii.contacttracker.model.Error

/**
 * Clase sellada genérica que envuelve un objeto de Éxito o un objeto de Fallo,
 * en función de si ha habido algún error. Si no hay errores, el objeto de Éxito contiene
 * el valor del tipo genérico definido. Si hay errores, se devuelve un objeto de Fallo
 * que contiene el error concreto en cada caso.
 *
 * Utilizada para modelar el flujo de información de la lógica de negocio.
 */
sealed class ValueWrapper<out T> {
    /* Valor exitoso */
    data class Success<out T>(val value: T): ValueWrapper<T>()

    /* Error */
    data class Fail(val error: Error): ValueWrapper<Nothing>()
}
