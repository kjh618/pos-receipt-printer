package com.kjh.posreceiptprinter.settings

import android.os.Bundle
import android.text.InputType
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.kjh.posreceiptprinter.R

fun parseProductsPreference(products: String): List<String> {
    return products.lines().filter { it.isNotBlank() }
}

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        findPreference<EditTextPreference>("products")!!.summaryProvider =
            Preference.SummaryProvider<EditTextPreference> { preference ->
                val products = parseProductsPreference(preference.text)
                "상품 ${products.size}개 등록됨"
            }

        findPreference<EditTextPreference>("usb_interface")!!.setOnBindEditTextListener {
            it.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_NORMAL
        }
        findPreference<EditTextPreference>("usb_endpoint")!!.setOnBindEditTextListener {
            it.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_NORMAL
        }
    }
}