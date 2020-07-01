package de.ka.jamit.arch.ui.home

import android.Manifest
import de.ka.jamit.arch.R
import de.ka.jamit.arch.base.BaseFragment
import de.ka.jamit.arch.base.events.ShowSnack
import de.ka.jamit.arch.base.getBaseActivity
import de.ka.jamit.arch.databinding.FragmentHomeBinding

class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(
    R.layout.fragment_home,
    HomeViewModel::class
) {

    var backCount = 0

    override fun onHandle(element: Any?) {
        if (element is HomeViewModel.PermissionRequest) {
            requestPermission(arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE))
        }
    }

    override fun onPermissionResult(allGranted: Boolean) {
        getBaseActivity()?.onShowMessage(ShowSnack("Permissions granted: $allGranted"))
    }

    override fun onResume() {
        backCount = 0
        super.onResume()
    }

    override fun onConsumeBackPress(): Boolean {
        backCount++

        if (backCount % 2 == 0) {
            viewModel.close() // OR return false is in this case the same as it would close the app on back
        } else {
            viewModel.showMessage(ShowSnack("Click one more time ..."))
        }

        return true
    }

}
