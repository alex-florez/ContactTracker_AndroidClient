package es.uniovi.eii.contacttracker.fragments.riskcontacts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.adapters.riskcontact.RiskContactTabsPageAdapter
import es.uniovi.eii.contacttracker.databinding.FragmentRiskContactTabsBinding

/**
 * Fragmento Contenedor de fragments que contiene el ViewPager
 * con el TabLayout para gestionar los fragmentos internos.
 */
@AndroidEntryPoint
class RiskContactTabsFragment : Fragment() {

    /**
     * ViewBinding.
     */
    private lateinit var binding: FragmentRiskContactTabsBinding

    /**
     * View Pager 2 para las pestañas de la comprobación de contactos.
     */
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentRiskContactTabsBinding.inflate(inflater, container, false)

        viewPager = binding.riskContactViewPager
        val pagerAdapter = RiskContactTabsPageAdapter(requireActivity())
        viewPager.adapter = pagerAdapter
        val tabLayout = binding.riskContactTabLayout
        val titles = arrayOf(getString(R.string.tabCheck), getString(R.string.tabResults))
        TabLayoutMediator(tabLayout, viewPager) {tab, position -> tab.text = titles[position]}.attach()
        return binding.root
    }
}