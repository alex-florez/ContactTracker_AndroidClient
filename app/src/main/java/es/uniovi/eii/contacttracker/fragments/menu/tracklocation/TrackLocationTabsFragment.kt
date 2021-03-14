package es.uniovi.eii.contacttracker.fragments.menu.tracklocation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.adapters.TrackLocationTabsPageAdapter
import es.uniovi.eii.contacttracker.databinding.FragmentTrackLocationTabsBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * Fragmento que sirve de contenedor para albergar al
 * TabLayout que contiene los diferentes tabs para la
 * opción de Menú del rastreador de ubicación.
 */
class TrackLocationTabsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    /**
     * View Binding.
     */
    private lateinit var binding: FragmentTrackLocationTabsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrackLocationTabsBinding.inflate(inflater, container, false)

        setTabLayout()
        return binding.root
    }

    /**
     * Se encarga de inicializar el TabLayout con el
     * Adapter de ViewPager correspondiente.
     */
    private fun setTabLayout(){
        activity?.let {
            val pageAdapter = TrackLocationTabsPageAdapter(childFragmentManager)
            binding.trackLocationViewPager.adapter = pageAdapter
            binding.trackLocationTabLayout.setupWithViewPager(binding.trackLocationViewPager)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TrackLocationTabsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TrackLocationTabsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}