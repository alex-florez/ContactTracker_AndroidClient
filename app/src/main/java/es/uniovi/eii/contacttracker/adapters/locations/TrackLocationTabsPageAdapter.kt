package es.uniovi.eii.contacttracker.adapters.locations

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import es.uniovi.eii.contacttracker.fragments.DefaultBlankFragment
import es.uniovi.eii.contacttracker.fragments.tracklocation.LocationAlarmsFragment
import es.uniovi.eii.contacttracker.fragments.tracklocation.TrackerFragment

private const val NUM_PAGES = 2

/**
 * Clase que representa el adapter para las páginas que serán mostradas
 * en el TabLayout dentro del fragment de la opción de menú del rastreador de ubicación.
 */
class TrackLocationTabsPageAdapter(
    fa: FragmentActivity
) : FragmentStateAdapter(fa) {

    override fun getItemCount(): Int {
        return NUM_PAGES
    }


    override fun createFragment(position: Int): Fragment {
        when(position){
            0 -> return TrackerFragment()
            1 -> return LocationAlarmsFragment()
        }
        return DefaultBlankFragment()
    }
}