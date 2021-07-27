package es.uniovi.eii.contacttracker.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.google.android.gms.maps.MapView

/**
 * Clase que representa la vista del mapa de Google Maps y redefine
 * su comportamiento para gestionar manualmente el gesto de Swipe.
 *
 * Permite al usuario controlar el movimiento de la cámara del mapa,
 * permitiéndole a su vez controlar el Swipe dentro del ViewPager.
 */
class MapViewSwipeBorder : MapView {
    constructor(context: Context): super(context)
    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, i: Int): super(context,attributeSet,i)

    /* Flag que indica si se activa o desactiva el Swipe Event. */
    private var intercept = false

    /* Porcentaje de borde por la izquierda y por la derecha. */
    private val swipeBorder = 0.2

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when(ev.action) {
            // Pulsación
            MotionEvent.ACTION_DOWN -> {
                // Comprobar si la pulsación está dentro del margen definido por los bordes.
                intercept = ev.x > width * swipeBorder && ev.x < width * (1-swipeBorder)
            }
            // Desplazamiento (Swipe)
            MotionEvent.ACTION_MOVE -> {
                // Anular o habilitar el evento de Swipe.
                parent.requestDisallowInterceptTouchEvent(intercept)
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}