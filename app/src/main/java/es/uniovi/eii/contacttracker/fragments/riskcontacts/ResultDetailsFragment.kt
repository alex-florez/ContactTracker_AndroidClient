package es.uniovi.eii.contacttracker.fragments.riskcontacts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import es.uniovi.eii.contacttracker.adapters.riskcontact.RiskContactAdapter
import es.uniovi.eii.contacttracker.databinding.FragmentResultDetailsBinding
import es.uniovi.eii.contacttracker.model.RiskContact
import es.uniovi.eii.contacttracker.model.RiskContactResult
import es.uniovi.eii.contacttracker.util.DateUtils

/**
 * Fragment que muestra los detalles de un resultado de
 * comprobación de contactos de riesgo.
 */
@AndroidEntryPoint
class ResultDetailsFragment : Fragment() {


    /**
     * View Binding.
     */
    private lateinit var binding: FragmentResultDetailsBinding

    /**
     * Adapter de Contactos de Riesgo.
     */
    private lateinit var riskContactsAdapter: RiskContactAdapter

    /* Argumentos */
    private val args: ResultDetailsFragmentArgs by navArgs()

    /* Referencia al Resultado */
    private lateinit var result: RiskContactResult

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Crear el adapter
        createAdapter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentResultDetailsBinding.inflate(inflater, container, false)
        result = args.result
        binding.du = DateUtils
        binding.result = args.result
        val meanExposeTime = DateUtils.getMinuteSecond(args.result.getTotalMeanExposeTime())
        binding.meanExposeTimeMins = meanExposeTime[0]
        binding.meanExposeTimeSecs = meanExposeTime[1]

        initRecyclerView() // Iniciar recyclerview
        return binding.root
    }

    /**
     * Instancia el adapter para los items de Contactos de Riesgo.
     */
    private fun createAdapter(){
        riskContactsAdapter = RiskContactAdapter(object : RiskContactAdapter.OnShowInMapClick {
            override fun onClick(riskContact: RiskContact) {
                /* Mostrar el contacto en el mapa */
                showRiskContactInMap(riskContact)
            }
        })
    }

    /**
     * Inicializa el recyclerview de contactos de riesgo
     * con el adapter.
     */
    private fun initRecyclerView(){
        val manager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewRiskContacts.apply {
            layoutManager = manager
            adapter = riskContactsAdapter
        }
        /* Rellenar el recyclerview */
        result.orderRiskContactsByRisk()
        riskContactsAdapter.submitList(result.riskContacts)
    }

    /**
     * Muestra el contacto de riesgo indicado en un mapa de Google Maps.
     */
    private fun showRiskContactInMap(riskContact: RiskContact) {
        val action = ResultDetailsFragmentDirections.showRiskContactInMap(riskContact)
        findNavController().navigate(action)
    }
}