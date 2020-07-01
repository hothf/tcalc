package de.ka.jamit.arch.ui.profile

import android.os.Bundle
import de.ka.jamit.arch.R
import de.ka.jamit.arch.base.BaseFragment
import de.ka.jamit.arch.base.events.FragmentResultable
import de.ka.jamit.arch.base.events.Open
import de.ka.jamit.arch.databinding.FragmentProfileBinding
import de.ka.jamit.arch.ui.profile.dialogs.ProfileDialog
import timber.log.Timber

class ProfileFragment : BaseFragment<FragmentProfileBinding, ProfileViewModel>(
    R.layout.fragment_profile,
    ProfileViewModel::class,
    useActivityPool = true
), FragmentResultable {
    // showcases the usage of a viewModel which is stored in the activity scope,
    // the viewModel will be reused, if the fragment becomes unavailable and
    // available again, instead of abandoning the viewModel (calling onCleared())
    // and creating a new one.
    // This can be a handy option but is turned off by default. Turn it on via
    // setting this useActivityPool to true

    override fun onHandle(element: Any?) {
        if (element is ProfileViewModel.ASuperEvent) {
            Timber.i("Handling the event in Profile - ${element.title}")
        }
    }

    // Note: This fragment is "FragmentResultable", that means it CAN receive a resultBundle of another fragment,
    // if it sets the result via `setFragmentResult()` with the same request key as defined here in `getResultRequestKey()`.
    override fun onFragmentResult(resultBundle: Bundle) {
        val chosen = resultBundle.getInt(ProfileDialog.DIALOG_CHOOSE_RESULT)
        Timber.d("Chosen value = $chosen")
    }

    override fun getResultRequestKey() = "dialogResult"
}
