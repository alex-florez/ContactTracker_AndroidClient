package es.uniovi.eii.contacttracker.adapters.results


const val TYPE_DATE = 0
const val TYPE_OBJECT = 1

/**
 * Clase abstracta que representa un item de un recycler view, ya sea una fecha
 * o el item concreto que representa los datos.
 */
abstract class RecycleItem {

    /**
     * Devuelve el tipo concreto del item del recycler view.
     */
    abstract fun getType(): Int
}