package de.ka.jamit.arch.ui.profile

import android.os.Bundle
import de.ka.jamit.arch.R
import de.ka.jamit.arch.base.BaseViewModel
import de.ka.jamit.arch.base.events.ShowSnack
import de.ka.jamit.arch.utils.Snacker
import timber.log.Timber

class ProfileViewModel : BaseViewModel() {

    fun snackit() {

        // two callbacks handled nearly immediately through different events in different views: ASuperEvent handled
        // in the viewModel and forwarded to the fragment, the messageListener is only subscribed in the main ViewModel,
        // giving info to the main Activity.

        handle(ASuperEvent("Yay"))

        messageListener.publishMessage(
            ShowSnack(
                "Shows a Snacker!",
                Snacker.SnackType.WARNING,
                "Does something"
            ) { Timber.i("Action of snacker.") })
    }

    fun dialogit() {
        navigateTo(R.id.dialogFragment)
    }

    fun sheetit() {
        val sheetArgs = Bundle().apply { putString("Argument", "Hello!") } // showcases giving arguments
        navigateTo(R.id.dialogBottomSheet, args = sheetArgs)
    }

    data class ASuperEvent(val title: String)
}


