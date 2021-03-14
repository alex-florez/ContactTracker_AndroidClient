package es.uniovi.eii.contacttracker.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import es.uniovi.eii.contacttracker.fragments.DefaultBlankFragment
import es.uniovi.eii.contacttracker.fragments.menu.tracklocation.TrackLocationInfoFragment
import es.uniovi.eii.contacttracker.fragments.menu.tracklocation.ThirdItemFragment
import es.uniovi.eii.contacttracker.fragments.menu.tracklocation.TrackLocationFragment

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
    private val pageCount = 3;

    /**
     * Títulos de página.
     */
    private val tabTitles = arrayListOf("Tracker", "Info", "Other")

    override fun getCount(): Int {
        return pageCount
    }

    override fun getItem(position: Int): Fragment {
        when(position){
            0 -> return TrackLocationFragment()
            1 -> return TrackLocationInfoFragment()
            2 -> return ThirdItemFragment()
        }
        return DefaultBlankFragment()
    }

    override fun getPageTitle(position: Int): CharSequence {
        return tabTitles[position]
    }
}