package es.uniovi.eii.contacttracker.fragments.riskcontacts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import es.uniovi.eii.contacttracker.Constants
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.adapters.results.RiskContactResultAdapter
import es.uniovi.eii.contacttracker.databinding.FragmentRiskContactResultsBinding
import es.uniovi.eii.contacttracker.mappers.toRiskContactResult
import es.uniovi.eii.contacttracker.model.RiskContactResult
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

    /**
     * Broadcast Receiver
     */
    private var riskContactResultReceiver: RiskContactResultReceiver? = null

    /**
     * Broadcast Receiver para recibir un nuevo resultado de una
     * comprobación de contactos de riesgo.
     */
    inner class RiskContactResultReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val result = intent?.getParcelableExtra<RiskContactResult>(Constants.EXTRA_RISK_CONTACT_RESULT)
            result?.let {
                riskContactResultAdapter.addResult(it)
                binding.recyclerViewRiskContactResults.smoothScrollToPosition(0)
            }
        }

    }

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

    override fun onResume() {
        super.onResume()
        registerRecivers()
        viewModel.getRiskContactResults()
    }

    override fun onPause() {
        super.onPause()
        unregisterReceivers()
    }

    /**
     * Establece los observers para los LiveData Observables
     * del ViewModel.
     */
    private fun setObservers(){
        viewModel.results.observe(viewLifecycleOwner) {
            riskContactResultAdapter.submitList(it)
            binding.recyclerViewRiskContactResults.smoothScrollToPosition(0)
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
     * Inicia una transacción para mostrar el fragment con los
     * detalles de un resultado de la comprobación.
     *
     * @param riskContactResult Resultado de la comprobación.
     */
    private fun showResultDetails(riskContactResult: RiskContactResult){
        requireActivity().supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
            .replace(R.id.main_fragment_container, ResultDetailsFragment.newInstance(riskContactResult))
            .addToBackStack("ResultDetailsFragment")
            .commit()
    }

    /**
     * Registra los BroadcastReceivers.
     */
    private fun registerRecivers(){
        if(riskContactResultReceiver == null){
            riskContactResultReceiver = RiskContactResultReceiver()
        }
        requireActivity().registerReceiver(
            riskContactResultReceiver,
            IntentFilter(Constants.ACTION_GET_RISK_CONTACT_RESULT)
        )
    }

    /**
     * Desvincula los Broadcast Receivers.
     */
    private fun unregisterReceivers(){
        requireActivity().unregisterReceiver(riskContactResultReceiver)
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