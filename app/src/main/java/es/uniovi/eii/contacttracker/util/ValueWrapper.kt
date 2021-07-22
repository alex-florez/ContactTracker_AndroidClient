package es.uniovi.eii.contacttracker.util

import es.uniovi.eii.contacttracker.model.Error

/**
 * Clase sellada que envuelve un objeto de Éxito que contiene el valor esperado
 * o un objeto de Fallo con un Error específico.
 *
 * Utilizada para modelar el flujo de información de la lógica de negocio.
 */
sealed class ValueWrapper<out T> {
    /* Valor exitoso */
    data class Success<out T>(val value: T): ValueWrapper<T>()

    /* Error */
    data class Fail(val error: Error): ValueWrapper<Nothing>()
}
