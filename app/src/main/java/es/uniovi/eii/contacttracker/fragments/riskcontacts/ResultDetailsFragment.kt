package es.uniovi.eii.contacttracker.fragments.riskcontacts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.adapters.riskcontact.RiskContactAdapter
import es.uniovi.eii.contacttracker.databinding.FragmentResultDetailsBinding
import es.uniovi.eii.contacttracker.model.RiskContact
import es.uniovi.eii.contacttracker.model.RiskContactResult
import es.uniovi.eii.contacttracker.model.RiskLevel
import es.uniovi.eii.contacttracker.util.DateUtils

/**
 * Argumento recibido como extra que contiene los resultados.
 */
private const val ARG_RESULT = "result"

/**
 * Fragment que muestra los detalles de un resultado de
 * comprobación de contactos de riesgo.
 */
@AndroidEntryPoint
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
        initRecyclerView() // Iniciar recyclerview
        bindData() // Vincular datos
        setListeners()
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
        result?.let {
            riskContactsAdapter.submitList(it.riskContacts)
        }
    }

    /**
     * Inicializa y establece los Listeners para los eventos
     * de los componentes de la interfaz.
     */
    private fun setListeners(){
        binding.apply {
            btnShowContactInMap.setOnClickListener {
                result?.let { res ->
                    showRiskContactInMap(res.getHighestRiskContact())
                }
            }
        }
    }

    /**
     * Se encarga de vincular los datos del resultado de la comprobación
     * con las vistas del Fragment.
     */
    private fun bindData(){
        result?.let {
            val riskiestContact = it.getHighestRiskContact()
            binding.apply {
                /* Contacto de mayor riesgo */
                txtContactDate.text = DateUtils.formatDate(riskiestContact.startDate, "dd/MM/yyyy")
                txtContactStartHour.text = DateUtils.formatDate(riskiestContact.startDate, "HH:mm:ss")
                txtContactEndHour.text = DateUtils.formatDate(riskiestContact.endDate, "HH:mm:ss")
                // Tiempo de exposición
                val exposeTime = DateUtils.getMinuteSecond(riskiestContact.exposeTime)
                val exposeTimeText = "${exposeTime[0]} min ${exposeTime[1]} sec"
                txtContactExposeTime.text = exposeTimeText
                // Proximidad media
                val meanProxText = "${riskiestContact.meanProximity} m"
                txtContactMeanProximity.text = meanProxText
                // Porcentaje de riesgo
                val riskPercentText = "${riskiestContact.riskPercent} %"
                txtHighestRiskPercentDetails.text = riskPercentText

                /* Datos generales */
                txtNumberOfPositivesDetails.text = it.numberOfPositives.toString()
                val totalMeanRiskText = "${it.getTotalMeanRisk()} %"
                val totalMeanExposeTime = DateUtils.getMinuteSecond(it.getTotalMeanExposeTime())
                val totalMeanExposeTimeText = "${totalMeanExposeTime[0]} min ${totalMeanExposeTime[1]} sec"
                val totalMeanProximityText = "${it.getTotalMeanProximity()} m"

                txtTotalMeanRisk.text = totalMeanRiskText
                txtTotalMeanExposeTime.text = totalMeanExposeTimeText
                txtTotalMeanProximity.text = totalMeanProximityText

                /* Color del card del contacto de mayor riesgo */
                setCardColor(riskiestContact.riskLevel)
            }
        }
    }

    /**
     * Cambia el color del card del contacto de mayorn riesgo en
     * función del nivel de riesgo del contacto.
     */
    private fun setCardColor(riskLevel: RiskLevel){
        val color = when(riskLevel) {
            RiskLevel.AMARILLO -> {
                ContextCompat.getDrawable(requireContext(), R.color.yellowWarning)
            }
            RiskLevel.NARANJA -> {
                ContextCompat.getDrawable(requireContext(), R.color.orangeWarning)
            }
            RiskLevel.ROJO -> {
                ContextCompat.getDrawable(requireContext(), R.color.redDanger)
            }
            else -> ContextCompat.getDrawable(requireContext(), R.color.greenOk)
        }
        if(color != null)
            binding.cardRiskiestContact.background = color
    }

    /**
     * Muestra el contacto de riesgo indicado en un mapa de Google Maps.
     */
    private fun showRiskContactInMap(riskContact: RiskContact) {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
            .replace(R.id.main_fragment_container, RiskContactMapFragment.newInstance(riskContact))
            .addToBackStack("RiskContactMapFragment")
            .commit()
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