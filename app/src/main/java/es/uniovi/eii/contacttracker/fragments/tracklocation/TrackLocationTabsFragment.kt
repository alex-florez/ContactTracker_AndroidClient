package es.uniovi.eii.contacttracker.fragments.tracklocation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import dagger.hilt.android.AndroidEntryPoint
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.adapters.TrackLocationTabsPageAdapter
import es.uniovi.eii.contacttracker.databinding.FragmentTrackLocationTabsBinding


/**
 * Fragmento que sirve de contenedor para albergar al
 * TabLayout que contiene los diferentes tabs para la
 * opción de Menú del rastreador de ubicación.
 */
@AndroidEntryPoint
class TrackLocationTabsFragment : Fragment() {

    /**
     * View Binding.
     */
    private lateinit var binding: FragmentTrackLocationTabsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

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
            // Establecer iconos
//            setTabIcons()
        }
    }

    /**
     * Método encargado de establecer los iconos a las
     * distintas Tabs del TabLayout.
     * distintas Tabs del TabLayout.
     */
    private fun setTabIcons(){
        val tabView = layoutInflater.inflate(R.layout.tab_item, null)
        tabView.findViewById<ImageView>(R.id.tab_icon).setBackgroundResource(R.drawable.ic_launcher_foreground)
        binding.trackLocationTabLayout.getTabAt(0)?.customView = tabView

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

                }
            }
    }
}