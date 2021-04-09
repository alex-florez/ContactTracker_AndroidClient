package es.uniovi.eii.contacttracker.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import es.uniovi.eii.contacttracker.fragments.DefaultBlankFragment
import es.uniovi.eii.contacttracker.fragments.tracklocation.LocationAlarmsFragment
import es.uniovi.eii.contacttracker.fragments.tracklocation.TrackerInfoFragment
import es.uniovi.eii.contacttracker.fragments.tracklocation.TrackerConfigurationFragment
import es.uniovi.eii.contacttracker.fragments.tracklocation.TrackerFragment

/**
 * Clase que representa el adapter para las páginas que serán mostradas
 * en el TabLayout dentro del fragment de la opción de menú del rastreador de ubicación.
 */
class TrackLocationTabsPageAdapter(
    fm:FragmentManager
) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    /**
     * Nº de páginas.
     */
    private val pageCount = 3

    /**
     * Títulos de página.
     */
    private val tabTitles = arrayListOf("Tracker", "Alarmas", "Configurar")

    override fun getCount(): Int {
        return pageCount
    }

    override fun getItem(position: Int): Fragment {
        when(position){
            0 -> return TrackerFragment()
            1 -> return LocationAlarmsFragment()
            2 -> return TrackerConfigurationFragment()
        }
        return DefaultBlankFragment()
    }

    override fun getPageTitle(position: Int): CharSequence {
        return tabTitles[position]
    }


}