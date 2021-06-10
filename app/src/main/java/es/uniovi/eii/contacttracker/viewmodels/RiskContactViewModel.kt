package es.uniovi.eii.contacttracker.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.uniovi.eii.contacttracker.model.Itinerary
import es.uniovi.eii.contacttracker.model.RiskContact
import es.uniovi.eii.contacttracker.model.UserLocation
import es.uniovi.eii.contacttracker.repositories.LocationRepository
import es.uniovi.eii.contacttracker.riskcontact.detector.RiskContactDetector
import es.uniovi.eii.contacttracker.riskcontact.detector.RiskContactDetectorImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import javax.inject.Inject

/**
 * ViewModel para el Fragmento de Comprobaciones
 * de Contactos de Riesgo.
 */
@HiltViewModel
class RiskContactViewModel @Inject constructor(
    private val locationRepository: LocationRepository
): ViewModel() {

    /**
     * RiskContact Detector
     */
    private val detector: RiskContactDetector = RiskContactDetectorImpl()

   fun detect() {
       val df = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
       // Generar itinerarios de prueba

       val u1 = UserLocation(11, 43.537852, -5.904168, 20.0, "", df.parse("08/06/2021 12:10:00"))
       val u2 = UserLocation(12, 43.537729, -5.904189, 20.0, "", df.parse("08/06/2021 12:12:23"))
       val u3 = UserLocation(13, 43.537628, -5.904214, 20.0, "", df.parse("08/06/2021 12:14:04"))
       val u4 = UserLocation(14, 43.537569, -5.904231, 20.0, "", df.parse("08/06/2021 12:15:50")) // <-- Start
       val u5 = UserLocation(15, 43.537569, -5.904165, 20.0, "", df.parse("08/06/2021 12:16:02"))
       val u6 = UserLocation(16, 43.537596, -5.904063, 20.0, "", df.parse("08/06/2021 12:17:00"))
       val u7 = UserLocation(17, 43.537634, -5.903972, 20.0, "", df.parse("08/06/2021 12:17:42"))// <-- End
       val u8 = UserLocation(18, 43.537682, -5.903906, 20.0, "", df.parse("08/06/2021 12:18:10"))
       val u9 = UserLocation(19, 43.537710, -5.903835, 20.0, "", df.parse("08/06/2021 12:19:13"))
       val u10 = UserLocation(110, 43.537722, -5.903768, 20.0, "", df.parse("08/06/2021 12:19:57"))
       val u11 = UserLocation(111, 43.537689, -5.903714, 20.0, "", df.parse("08/06/2021 12:20:36")) // <-- Start
       val u12 = UserLocation(112, 43.537640, -5.903636, 20.0, "", df.parse("08/06/2021 12:21:15"))
       val u13 = UserLocation(113, 43.537664, -5.903537, 20.0, "", df.parse("08/06/2021 12:22:20")) // <-- End

       val p1 = UserLocation(21, 43.537562, -5.904420, 20.0, "", df.parse("08/06/2021 12:08:03"))
       val p2 = UserLocation(22, 43.537563, -5.904329, 20.0, "", df.parse("08/06/2021 12:10:03"))
       val p3 = UserLocation(23, 43.537558, -5.904227, 20.0, "", df.parse("08/06/2021 12:15:02")) // <-- Start
       val p4 = UserLocation(24, 43.537568, -5.904114, 20.0, "", df.parse("08/06/2021 12:16:20"))
       val p5 = UserLocation(25, 43.537601, -5.904012, 20.0, "", df.parse("08/06/2021 12:18:03"))
       val p6 = UserLocation(26, 43.537571, -5.903884, 20.0, "", df.parse("08/06/2021 12:18:20")) // <-- End
       val p7 = UserLocation(27, 43.537630, -5.903803, 20.0, "", df.parse("08/06/2021 12:18:56"))
       val p8 = UserLocation(28, 43.537676, -5.903737, 20.0, "", df.parse("08/06/2021 12:19:58")) // <-- Start
       val p9 = UserLocation(29, 43.537638, -5.903654, 20.0, "", df.parse("08/06/2021 12:20:57"))
       val p10 = UserLocation(210, 43.537583, -5.903629, 20.0, "", df.parse("08/06/2021 12:21:46")) // <-- End

       // Usuario
       val userLocations = mutableMapOf<String, List<UserLocation>>()
       val userList = listOf(u1,u2,u3,u4,u5,u6,u7, u8, u9, u10, u11, u12, u13)
       userLocations["2021-06-08"] = userList
       val userItinerary = Itinerary(userLocations)

       // Positivo
       val positiveLocations = mutableMapOf<String, List<UserLocation>>()
       val positiveList = listOf(p1,p2,p3,p4,p5,p6,p7,p8,p9,p10)
       positiveLocations["2021-06-08"] = positiveList
       val positiveItinerary = Itinerary(positiveLocations)

       val result = detector.startChecking(userItinerary, positiveItinerary)
       Log.d("RESULTADO", result.toString())
   }


}