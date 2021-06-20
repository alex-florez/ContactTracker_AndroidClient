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

/**
 * Preference personalizada para seleccionar números con un NumberPicker
 */
class NumberPickerPreference(context: Context?, attrs: AttributeSet?) :
    Preference(context, attrs), View.OnClickListener {

    /* Referencia a las Shared Preferences */
    private lateinit var sharedPrefs: SharedPreferences
    /* Valores del Number Picker */
    private var value: Int = 0
    private var minValue = 0
    private var maxValue = 10
    private var defValue = 0
    private var wrapSelectorWheel = false
    /* Títulos */
    private var title = ""
    private var pickerTitle = ""
    private var valueUnit = "" // Unidad de medida del valor.
    /* ID de la Shared Preference */
    private lateinit var prefId: String

    /* View Binding */
    private lateinit var binding: CustomPreferenceLayoutBinding

    init {
        // Vincular los valores de los atributos.
        context?.let{
            val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.NumberPickerPreference)
            minValue = a.getInteger(R.styleable.NumberPickerPreference_minValue,0)
            maxValue = a.getInteger(R.styleable.NumberPickerPreference_maxValue, 10)
            defValue = a.getInteger(R.styleable.NumberPickerPreference_defVal, 0)
            title = a.getString(R.styleable.NumberPickerPreference_prefTitle) ?: "title"
            pickerTitle = a.getString(R.styleable.NumberPickerPreference_pickerTitle) ?: "pickerTitle"
            valueUnit = a.getString(R.styleable.NumberPickerPreference_valueUnit) ?: ""
            prefId = a.getString(R.styleable.NumberPickerPreference_prefKey) ?: "id"
            wrapSelectorWheel = a.getBoolean(R.styleable.NumberPickerPreference_wrapSelectorWheelNp, true)
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
     * @param value Valor a persistir.
     */
    private fun persist(value: Int){
        this.value = value
        with(sharedPrefs.edit()){
            putInt(prefId, value)
            apply()
        }
        // Actualizar componente de la Preference
        val textValue = "$value $valueUnit"
        binding.prefValue.text = textValue
    }

    /**
     * Crea e inicializa el NumberPicker y lo muestra en un AlertDialog.
     */
    private fun showPicker() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.number_picker_layout, binding.root, false)
        /* Number Picker */
        val picker = view.findViewById<NumberPicker>(R.id.numberPicker)
        /* Valores mínimo, máximo y actual */
        picker.minValue = minValue
        picker.maxValue = maxValue
        picker.value = value
        picker.wrapSelectorWheel = wrapSelectorWheel
        val builder = AlertDialog.Builder(context)
        builder.setView(view)
        builder.setPositiveButton(context.getString(R.string.accept)){_, _ ->
            persist(picker.value)
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
        this.value = sharedPrefs.getInt(prefId, defValue)
        val valueText = "$value $valueUnit"
        binding.prefValue.text = valueText
        binding.prefTitle.text = title
    }
}