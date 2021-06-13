package es.uniovi.eii.contacttracker.fragments.riskcontacts

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.adapters.riskcontact.RiskContactAdapter
import es.uniovi.eii.contacttracker.databinding.FragmentResultDetailsBinding
import es.uniovi.eii.contacttracker.model.RiskContact
import es.uniovi.eii.contacttracker.model.RiskContactResult

/**
 * Argumento recibido como extra que contiene los resultados.
 */
private const val ARG_RESULT = "result"

/**
 * Fragment que muestra los detalles de un resultado de
 * comprobación de contactos de riesgo.
 */
class ResultDetailsFragment : Fragment() {

    /**
     * Extra del resultado de comprobación de contactos de riesgo.
     */
    private var result: RiskContactResult? = null

    /**
     * View Binding.
     */
    private lateinit var binding: FragmentResultDetailsBinding

    /**
     * Adapter de Contactos de Riesgo.
     */
    private lateinit var riskContactsAdapter: RiskContactAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Recuperar los argumentos
        arguments?.let {
            result = it.getParcelable(ARG_RESULT)
        }
        // Crear el adapter
        createAdapter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentResultDetailsBinding.inflate(inflater, container, false)

        initRecyclerView()
        return binding.root
    }

    /**
     * Instancia el adapter para los items de Contactos de Riesgo.
     */
    private fun createAdapter(){
        riskContactsAdapter = RiskContactAdapter(object : RiskContactAdapter.OnShowInMapClick {
            override fun onClick(riskContact: RiskContact) {
                /* Mostrar el contacto en el mapa */

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
        result?.let {
            riskContactsAdapter.submitList(it.riskContacts)
        }
    }

    companion object {
        /**
         * Factory Method que crea una nueva instancia del fragmento de los
         * detalles de un resultado de la comprobación.
         *
         * @param result Resultado de la comprobación recibido como Extra.
         * @return Nueva instancia del fragmento.
         */
        @JvmStatic
        fun newInstance(result: RiskContactResult) =
                ResultDetailsFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_RESULT, result)
                    }
                }
    }
}