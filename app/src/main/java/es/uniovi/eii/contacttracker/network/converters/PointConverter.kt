package es.uniovi.eii.contacttracker.network.converters

import com.google.gson.*
import es.uniovi.eii.contacttracker.model.Point
import es.uniovi.eii.contacttracker.util.DateUtils
import org.json.JSONObject
import java.lang.reflect.Type
import java.util.Date

/* Campos del JSON */
const val LAT = "lat"
const val LNG = "lng"
const val TIMESTAMP = "locationTimestamp"


/**
 * Custom Converter para serializar/deserializar objetos
 * de tipo Point.
 */
class PointConverter : JsonSerializer<Point>, JsonDeserializer<Point> {

    /* SERIALIZAR */
    override fun serialize(
        src: Point?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        val json = JsonObject()
        src?.let {
            json.addProperty(LAT, src.lat)
            json.addProperty(LNG, src.lng)
            json.addProperty(TIMESTAMP, src.timestamp.time) // Convertir a millis
        }
        return json
    }

    /* DESERIALIZAR */
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Point {
        var point = Point(0.0, 0.0, Date())
        json?.asJsonObject?.let {
            point = Point(
                it.get(LAT).asDouble,
                it.get(LNG).asDouble,
                DateUtils.millisToDate(it.get(TIMESTAMP).asLong)
            )
        }
        return point
    }
}