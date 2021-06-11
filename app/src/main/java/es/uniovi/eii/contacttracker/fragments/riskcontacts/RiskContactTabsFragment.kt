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
import es.uniovi.eii.contacttracker.adapters.RiskContactTabsPageAdapter
import es.uniovi.eii.contacttracker.databinding.FragmentRiskContactTabsBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * Fragmento Contenedor de fragments que contiene el ViewPager
 * con el TabLayout para gestionar los fragmentos internos.
 */
@AndroidEntryPoint
class RiskContactTabsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    /**
     * ViewBinding.
     */
    private lateinit var binding: FragmentRiskContactTabsBinding

    /**
     * View Pager 2
     */
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentRiskContactTabsBinding.inflate(inflater, container, false)

        viewPager = binding.riskContactViewPager
        val pagerAdapter = RiskContactTabsPageAdapter(requireActivity())
        viewPager.adapter = pagerAdapter
        val tabLayout = binding.riskContactTabLayout
        val titles = arrayOf("Comprobar", "Resultados")
        TabLayoutMediator(tabLayout, viewPager) {tab, position -> tab.text = titles[position]}.attach()
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RiskContactTabsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                RiskContactTabsFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}