package es.uniovi.eii.contacttracker.fragments.settings

import android.content.Context
import android.content.SharedPreferences
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.databinding.FragmentRiskContactResultsBinding
import es.uniovi.eii.contacttracker.databinding.NumberPickerPreferenceLayoutBinding

class NumberPickerPreference(context: Context?, attrs: AttributeSet?) :
    Preference(context, attrs), View.OnClickListener {

    private lateinit var sharedPrefs: SharedPreferences
    private var currentValue: Int = 0
    private var minValue = 0
    private var maxValue = 10
    private lateinit var prefId: String

    private lateinit var binding: NumberPickerPreferenceLayoutBinding

    init {
        context?.let{
            val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.NumberPickerPreference)
            minValue = a.getInteger(R.styleable.NumberPickerPreference_minValue,0)
            maxValue = a.getInteger(R.styleable.NumberPickerPreference_maxValue, 10)
            prefId = a.getString(R.styleable.NumberPickerPreference_prefId) ?: "id"
            a.recycle()
        }
    }

    override fun onClick(v: View?) {
        Log.d("custompref", "$minValue $maxValue $prefId")
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        binding = NumberPickerPreferenceLayoutBinding.bind(holder.itemView)

        binding.root.setOnClickListener(this)
    }

}