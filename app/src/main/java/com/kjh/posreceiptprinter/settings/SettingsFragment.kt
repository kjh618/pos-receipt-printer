package com.kjh.posreceiptprinter.settings

import android.os.Bundle
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

        val productsPreference: EditTextPreference = findPreference("products")!!
        productsPreference.summaryProvider =
            Preference.SummaryProvider<EditTextPreference> { preference ->
                val products = parseProductsPreference(preference.text)
                "상품 ${products.size}개 등록됨"
            }
    }
}