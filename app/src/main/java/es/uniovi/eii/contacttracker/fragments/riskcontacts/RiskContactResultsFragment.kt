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

/**
 * Fragmento para mostrar los diferentes resultados de las comprobaciones
 * de contactos de riesgo. Muestra las comprobaciones realizadas
 * ordenadas por fecha de comprobación.
 */
@AndroidEntryPoint
class RiskContactResultsFragment : Fragment() {

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
            results.observe(viewLifecycleOwner) {
                fillAdapter(it)
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
        // Agrupar los resultados por fechas de ejecución
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

}