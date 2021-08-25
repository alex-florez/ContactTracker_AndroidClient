package es.uniovi.eii.contacttracker.network.model

/**
 * Modela la respuesta de la API al registrar una nueva instalación.
 */
data class NewInstallResponse(
    val registered: Boolean,
    val msg: String
)
