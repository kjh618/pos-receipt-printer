package com.kjh.posreceiptprinter.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.kjh.posreceiptprinter.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
    }
}