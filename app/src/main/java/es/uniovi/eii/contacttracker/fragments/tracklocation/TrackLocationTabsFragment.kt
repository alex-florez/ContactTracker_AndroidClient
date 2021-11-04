package es.uniovi.eii.contacttracker.fragments.tracklocation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.adapters.locations.TrackLocationTabsPageAdapter
import es.uniovi.eii.contacttracker.databinding.FragmentTrackLocationTabsBinding


/**
 * Fragmento contenedor para albergar al TabLayout que contiene los
 * diferentes tabs para la opción de Menú del rastreador de ubicación.
 */
@AndroidEntryPoint
class TrackLocationTabsFragment : Fragment() {

    /**
     * View Binding.
     */
    private lateinit var binding: FragmentTrackLocationTabsBinding

    /**
     * View Pager 2 para las pestañas del tracker.
     */
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrackLocationTabsBinding.inflate(inflater, container, false)

        viewPager = binding.trackLocationViewpager
        viewPager.adapter = TrackLocationTabsPageAdapter(requireActivity())
        setTabLayout()
        return binding.root
    }

    /**
     * Se encarga de inicializar el TabLayout con el
     * Adapter de ViewPager correspondiente.
     */
    private fun setTabLayout(){
        /**
         * Títulos de las Tabs
         */
        val tabTitles = arrayListOf(getString(R.string.tabTracker), getString(R.string.tabMap), getString(R.string.tabAlarms))
        val tabLayout = binding.trackLocationTabLayout

        TabLayoutMediator(tabLayout, viewPager) {tab, position -> tab.text = tabTitles[position]}.attach()
    }
}