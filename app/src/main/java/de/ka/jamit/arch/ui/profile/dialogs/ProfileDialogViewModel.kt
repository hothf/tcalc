package de.ka.jamit.arch.ui.profile.dialogs

import android.os.Bundle
import de.ka.jamit.arch.base.BaseViewModel

/**
 *
 * Created by Thomas Hofmann on 23.04.20
 **/
class ProfileDialogViewModel : BaseViewModel() {

    class Choose(val value: Int)

    fun choose() {
        handle(Choose(42))
    }

    override fun onArgumentsReceived(bundle: Bundle) {
        // contains "hello"
        super.onArgumentsReceived(bundle)
    }
}