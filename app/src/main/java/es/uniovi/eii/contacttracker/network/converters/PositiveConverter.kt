package es.uniovi.eii.contacttracker.network.converters

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import es.uniovi.eii.contacttracker.model.PersonalData
import es.uniovi.eii.contacttracker.model.Point
import es.uniovi.eii.contacttracker.model.Positive
import es.uniovi.eii.contacttracker.model.UserLocation
import es.uniovi.eii.contacttracker.util.DateUtils
import java.lang.reflect.Type

/* Campos del JSON */
const val POSITIVE_CODE = "positiveCode"
const val NOTIFY_TIMESTAMP = "timestamp"
const val LOCATIONS = "locations"
const val PERSONAL_DATA = "personalData"
const val ASYMPTOMATIC = "asymptomatic"
const val VACCINATED = "vaccinated"

const val LAT = "lat"
const val LNG = "lng"
const val TIMESTAMP = "locationTimestamp"

const val USER_LOCATION_ID = "userLocationId"
const val POINT = "point"
const val ACCURACY = "accuracy"
const val PROVIDER = "provider"

const val DNI = "dni"
const val NAME = "name"
const val SURNAME = "surname"
const val PHONE_NUMBER = "phoneNumber"
const val CITY = "city"
const val CP = "cp"


/**
 * Custom Converter para serializar/deserializar objetos de tipo Point.
 */
class PositiveConverter : JsonSerializer<Positive>, JsonDeserializer<Positive> {

    /* SERIALIZAR */
    override fun serialize(
        src: Positive?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        val json = JsonObject()
        src?.let {
            json.addProperty(POSITIVE_CODE, src.positiveCode)
            json.addProperty(NOTIFY_TIMESTAMP, src.timestamp.time) // Convertir a millis
            if(src.personalData != null) {
                json.add(PERSONAL_DATA, personalDataToJson(src.personalData!!))
            }
            json.addProperty(ASYMPTOMATIC, src.asymptomatic)
            json.addProperty(VACCINATED, src.vaccinated)
            val array = JsonArray()
            src.locations.forEach {
                array.add(userLocationToJson(it))
            }
            json.add(LOCATIONS, array)

        }
        return json
    }

    /* DESERIALIZAR */
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Positive {
        val type = object : TypeToken<List<UserLocation>>(){}.type
        var positive = Positive()
        val locations = mutableListOf<UserLocation>()

        json?.asJsonObject?.let {
            val arrayLocations = it.getAsJsonArray(LOCATIONS)
            arrayLocations.forEach { jsonLoc ->
                locations.add(userLocationFromJson(jsonLoc.asJsonObject))
            }
            positive = Positive(
                null,
                it.get(POSITIVE_CODE).asString,
                DateUtils.millisToDate(it.get(NOTIFY_TIMESTAMP).asLong),
                locations,
                Gson().fromJson(it.get(PERSONAL_DATA), PersonalData::class.java),
                it.get(ASYMPTOMATIC).asBoolean,
                it.get(VACCINATED).asBoolean
            )
        }
        return positive
    }

    /**
     * Convierte el objeto UserLocation en un objeto Json.
     *
     * @param location Localización de usuario.
     * @return Objeto json que representa una localización de usuario.
     */
    private fun userLocationToJson(location: UserLocation): JsonObject {
        val jsonLocation = JsonObject()
        val jsonPoint = JsonObject()
        // Punto
        jsonPoint.addProperty(LAT, location.lat())
        jsonPoint.addProperty(LNG, location.lng())
        jsonPoint.addProperty(TIMESTAMP, location.timestamp().time)

        jsonLocation.addProperty(USER_LOCATION_ID, location.userlocationID)
        jsonLocation.add(POINT, jsonPoint)
        jsonLocation.addProperty(ACCURACY, location.accuracy)
        jsonLocation.addProperty(PROVIDER, location.provider)

        return jsonLocation
    }

    /**
     * Convierte un objeto PersonalData a un objeto Json.
     *
     * @param personalData Objeto del dominio con los datos personales.
     * @return Objeto Json que representa los datos personales.
     */
    private fun personalDataToJson(personalData: PersonalData): JsonObject {
        val json = JsonObject()
        json.addProperty(DNI, personalData.dni)
        json.addProperty(NAME, personalData.name)
        json.addProperty(SURNAME, personalData.surname)
        json.addProperty(PHONE_NUMBER, personalData.phoneNumber)
        json.addProperty(CITY, personalData.city)
        json.addProperty(CP, personalData.cp)
        return json
    }

    /**
     * Convierte el objeto Json a una localización de usuario.
     *
     * @param json Objeto Json que representa una localización de usuario.
     * @return Objeto del dominio UserLocation.
     */
    private fun userLocationFromJson(json: JsonObject): UserLocation {
        val point = json.getAsJsonObject(POINT)
        return UserLocation(
            json.get(USER_LOCATION_ID).asLong,
            Point(
                point.get("lat").asDouble,
                point.get("lng").asDouble,
                DateUtils.millisToDate(point.get("locationTimestamp").asLong)
            ),
            json.get(ACCURACY).asDouble,
            json.get(PROVIDER).asString
        )
    }
}