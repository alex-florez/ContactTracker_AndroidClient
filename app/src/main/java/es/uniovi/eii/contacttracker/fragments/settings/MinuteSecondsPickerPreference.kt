package es.uniovi.eii.contacttracker.fragments.settings

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.NumberPicker
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.databinding.CustomPreferenceLayoutBinding
import es.uniovi.eii.contacttracker.util.DateUtils

/**
 * Preference personalizada para seleccionar minutos y segundos con
 * dos NumberPickers.
 */
class MinuteSecondsPickerPreference(context: Context?, attrs: AttributeSet?) :
    Preference(context, attrs), View.OnClickListener {

    /* Referencia a las Shared Preferences */
    private lateinit var sharedPrefs: SharedPreferences
    /* Valores del Number Picker */
    // Minutos
    private var minutesValue: Int = 0
    private var minValueMinutes = 0
    private var maxValueMinutes = 10
    private var defaultValueMinutes = 0
    // Segundos
    private var secondsValue: Int = 0
    private var minValueSeconds = 0
    private var maxValueSeconds = 10
    private var defaultValueSeconds = 0
    private var wrapSelectorWheel = false
    /* Títulos */
    private var title = ""
    /* ID de la Shared Preference */
    private lateinit var prefId: String

    /* View Binding */
    private lateinit var binding: CustomPreferenceLayoutBinding

    init {
        // Vincular los valores de los atributos.
        context?.let{
            val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.MinuteSecondsPickerPreference)
            minValueMinutes = a.getInteger(R.styleable.MinuteSecondsPickerPreference_minValueMinutes,0)
            maxValueMinutes = a.getInteger(R.styleable.MinuteSecondsPickerPreference_maxValueMinutes, 10)
            defaultValueMinutes = a.getInteger(R.styleable.MinuteSecondsPickerPreference_defValMinutes, 0)
            minValueSeconds = a.getInteger(R.styleable.MinuteSecondsPickerPreference_minValueSeconds, 0)
            maxValueSeconds = a.getInteger(R.styleable.MinuteSecondsPickerPreference_maxValueSeconds, 10)
            defaultValueSeconds = a.getInteger(R.styleable.MinuteSecondsPickerPreference_defValSeconds, 0)
            wrapSelectorWheel = a.getBoolean(R.styleable.MinuteSecondsPickerPreference_wrapSelectorWheel, true)
            title = a.getString(R.styleable.MinuteSecondsPickerPreference_title) ?: "title"
            prefId = a.getString(R.styleable.MinuteSecondsPickerPreference_prefId) ?: "id"
            a.recycle()
        }
    }

    override fun onClick(v: View?) {
        /* Mostrar el NumberPicker */
        showPicker()
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        binding = CustomPreferenceLayoutBinding.bind(holder.itemView)
        sharedPrefs = context.getSharedPreferences(context.getString(R.string.shared_prefs_file_name), Context.MODE_PRIVATE)
        setCurrentValue()
        binding.root.setOnClickListener(this)
    }

    /**
     * Almacena el valor seleccionado con el picker
     * en las Shared Preferences.
     *
     * @param min Minutos a persistir.
     * @param sec Segundos a persistir
     */
    private fun persist(min: Int, sec: Int){
        minutesValue = min
        secondsValue = sec
        val millis = DateUtils.getMillis(min, sec)
        with(sharedPrefs.edit()){
            putLong(prefId, millis)
            apply()
        }
        // Actualizar componente de la Preference
        val text = "$min min $sec sec"
        binding.prefValue.text = text
    }

    /**
     * Crea e inicializa el NumberPicker y lo muestra en un AlertDialog.
     */
    private fun showPicker() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.minute_seconds_number_picker, binding.root, false)
        /* NumberPicker de minutos */
        val minPicker = view.findViewById<NumberPicker>(R.id.numberPickerMinutes)
        /* NumberPicker de segundos */
        val secsPicker = view.findViewById<NumberPicker>(R.id.numberPickerSeconds)
        /* Valores mínimo, máximo y actual */
        minPicker.minValue = minValueMinutes
        minPicker.maxValue = maxValueMinutes
        minPicker.value = minutesValue
        minPicker.wrapSelectorWheel = wrapSelectorWheel
        secsPicker.minValue = minValueSeconds
        secsPicker.maxValue = maxValueSeconds
        secsPicker.value = secondsValue
        secsPicker.wrapSelectorWheel = wrapSelectorWheel

        val builder = AlertDialog.Builder(context)
        builder.setView(view)
        builder.setPositiveButton(context.getString(R.string.accept)){_, _ ->
            persist(minPicker.value, secsPicker.value)
        }
        builder.setNegativeButton(context.getString(R.string.cancel)) {dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    /**
     * Recupera el valor actual de las shared preferences y lo
     * establece en la vista.
     */
    private fun setCurrentValue() {
        // Valor por defecto de milisegundos
        val defaultMillis = DateUtils.getMillis(defaultValueMinutes, defaultValueSeconds)
        // Recuperar milisegundos
        val millis = sharedPrefs.getLong(prefId, defaultMillis)
        val minSecs = DateUtils.getMinuteSecond(millis)
        minutesValue = minSecs[0]
        secondsValue = minSecs[1]
        val text = "$minutesValue min $secondsValue sec"
        binding.prefValue.text = text
        binding.prefTitle.text = title
    }
}