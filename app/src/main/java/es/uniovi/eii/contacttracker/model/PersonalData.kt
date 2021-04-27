package es.uniovi.eii.contacttracker.model

/**
 * Clase de datos que representa los datos personales
 * del usuario.
 */
data class PersonalData(
        val dni: String,
        val name: String,
        val surname: String,
        val phoneNumber: String,
        val city: String,
        val cp: String
) {
    override fun toString(): String {
        return "PersonalData(dni='$dni', name='$name', surname='$surname', phoneNumber='$phoneNumber', city='$city', cp='$cp')"
    }
}
