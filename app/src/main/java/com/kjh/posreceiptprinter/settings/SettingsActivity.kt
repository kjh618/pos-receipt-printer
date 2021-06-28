package com.kjh.posreceiptprinter.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kjh.posreceiptprinter.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarSettings)
    }
}