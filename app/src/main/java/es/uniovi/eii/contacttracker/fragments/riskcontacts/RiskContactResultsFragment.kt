package es.uniovi.eii.contacttracker.fragments.riskcontacts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import es.uniovi.eii.contacttracker.adapters.results.DateItem
import es.uniovi.eii.contacttracker.adapters.results.ObjectItem
import es.uniovi.eii.contacttracker.adapters.results.RecycleItem
import es.uniovi.eii.contacttracker.adapters.results.RiskContactResultAdapter
import es.uniovi.eii.contacttracker.databinding.FragmentRiskContactResultsBinding
import es.uniovi.eii.contacttracker.model.RiskContactResult
import es.uniovi.eii.contacttracker.util.DateUtils
import es.uniovi.eii.contacttracker.viewmodels.RiskContactResultViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * Fragmento para mostrar los diferentes resultados de las comprobaciones
 * de contactos de riesgo. Muestra las comprobaciones realizadas
 * ordenadas por fecha de comprobación.
 */
@AndroidEntryPoint
class RiskContactResultsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    /**
     * View Binding
     */
    private lateinit var binding: FragmentRiskContactResultsBinding

    /**
     * ViewModel
     */
    private val viewModel: RiskContactResultViewModel by viewModels()

    /**
     * Adapter para los objetos RiskContactResult.
     */
    private lateinit var riskContactResultAdapter: RiskContactResultAdapter

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
        binding = FragmentRiskContactResultsBinding.inflate(inflater, container, false)

        createAdapter()
        setRecyclerView()
        setObservers()
        return binding.root
    }

    /**
     * Establece los observers para los LiveData Observables
     * del ViewModel.
     */
    private fun setObservers(){
        viewModel.apply {
            /* Resultados de la comprobación */
            getAllRiskContactResults().observe(viewLifecycleOwner) {
                fillAdapter(it)
//                riskContactResultAdapter.submitList(it)
                binding.txtLabelEmpty.visibility = if(it.isEmpty()) View.VISIBLE else View.GONE
            }
            /* Icono de carga */
            isLoading.observe(viewLifecycleOwner) {
                binding.riskContactResultProgress.visibility = if(it) View.VISIBLE else View.GONE
                // Ocultar etiqueta de lista vacía si está cargando
                if(it) binding.txtLabelEmpty.visibility = View.GONE
            }
            /* Etiqueta de lista vacía */
            isEmpty.observe(viewLifecycleOwner) {
                binding.txtLabelEmpty.visibility = if(it) View.VISIBLE else View.GONE
            }
        }
    }

    /**
     * Inicializa el RecyclerView con el Adapter de objetos
     * RiskContactResult.
     */
    private fun setRecyclerView(){
        val manager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewRiskContactResults.apply {
            layoutManager = manager
            adapter = riskContactResultAdapter
        }
    }

    /**
     * Instancia el Adapter para los objetos de resultados de
     * contactos de riesgo.
     */
    private fun createAdapter(){
        riskContactResultAdapter = RiskContactResultAdapter(object : RiskContactResultAdapter.OnRiskContactResultClick {
            override fun onClick(riskContactResult: RiskContactResult) {
                // Ver detalles del resultado de la comprobación.
                showResultDetails(riskContactResult)
            }
        })
    }

    /**
     * Rellena el adapter de resultados de comprobación con los resultados
     * almacenados en la lista pasada como parámetro.
     *
     * Crea un HashMap agrupando los resultados por fecha (día) y lo convierte
     * en una lista ordenada con los correspondientes objetos RecyclerItem.
     */
    private fun fillAdapter(results: List<RiskContactResult>) {
        val df = "dd/MM/yyyy"
        val items = mutableListOf<RecycleItem>()
        /* Diferentes fechas */
        val dates = results.map {
            DateUtils.formatDate(it.timestamp, df)
        }.distinct()
        dates.forEach { date ->
            val filteredResults = results.filter {
                DateUtils.formatDate(it.timestamp, df) == date
            }
            DateUtils.toDate(date, df)?.let {
                val dateItem = DateItem(it) // Item para la fecha
                val objectItems = filteredResults.map { result -> // Items con los resultados
                    ObjectItem(result)
                }
                // Añadir a la lista
                items.add(dateItem)
                items.addAll(objectItems)
            }
        }
        riskContactResultAdapter.submitList(items) {
            binding.recyclerViewRiskContactResults.scrollToPosition(0)
        }
    }

    /**
     * Navega hasta el fragment que muestra los detalles de un resultado.
     *
     * @param riskContactResult Resultado de la comprobación.
     */
    private fun showResultDetails(riskContactResult: RiskContactResult){
        val action = RiskContactTabsFragmentDirections.showResultDetails(riskContactResult)
        findNavController().navigate(action)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RiskContactResultsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RiskContactResultsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}