package es.uniovi.eii.contacttracker.riskcontact.detector

import es.uniovi.eii.contacttracker.model.Itinerary
import es.uniovi.eii.contacttracker.model.RiskContactResult
import es.uniovi.eii.contacttracker.model.UserLocation

/**
 * Clase que implementa la interfaz que define el Detector de
 * Contactos de Riesgo y que contiene el algoritmo para hacer
 * las comprobaciones.
 */
class RiskContactDetectorImpl : RiskContactDetector {

    override fun startChecking(user: Itinerary, positive: Itinerary): RiskContactResult {
        TODO("Not yet implemented")
    }

    override fun checkSpaceProximity(
        pointA: UserLocation,
        pointB: UserLocation,
        radius: Double
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun checkTimeProximity(
        pointA: UserLocation,
        pointB: UserLocation,
        time: Double
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun distance(pointA: UserLocation, pointB: UserLocation): Double {
        TODO("Not yet implemented")
    }
}