package es.uniovi.eii.contacttracker.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import es.uniovi.eii.contacttracker.fragments.DefaultBlankFragment
import es.uniovi.eii.contacttracker.fragments.riskcontacts.RiskContactFragment
import es.uniovi.eii.contacttracker.fragments.riskcontacts.RiskContactResultsFragment
import es.uniovi.eii.contacttracker.fragments.tracklocation.LocationAlarmsFragment
import es.uniovi.eii.contacttracker.fragments.tracklocation.TrackerConfigurationFragment
import es.uniovi.eii.contacttracker.fragments.tracklocation.TrackerFragment

private const val NUM_PAGES = 2
class RiskContactTabsPageAdapter(
    fa: FragmentActivity
) : FragmentStateAdapter(fa) {

    override fun getItemCount(): Int {
        return NUM_PAGES
    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            0 -> return RiskContactFragment()
            1 -> return RiskContactResultsFragment()
        }
        return DefaultBlankFragment()
    }
}