package de.ka.jamit.arch.ui.settings

import android.os.Bundle
import de.ka.jamit.arch.R
import de.ka.jamit.arch.base.BaseFragment
import de.ka.jamit.arch.base.events.FragmentResultable
import de.ka.jamit.arch.databinding.FragmentSettingsBinding
import timber.log.Timber

class SettingsFragment :
    BaseFragment<FragmentSettingsBinding, SettingsViewModel>(R.layout.fragment_settings, SettingsViewModel::class),
    FragmentResultable {

    override fun onFragmentResult(resultBundle: Bundle) {
        Timber.e("I received a result with: $resultBundle")
    }

    override fun getResultRequestKey() = "SettingsResultRequest"
}


