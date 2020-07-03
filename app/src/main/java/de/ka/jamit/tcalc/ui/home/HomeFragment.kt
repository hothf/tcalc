package de.ka.jamit.tcalc.ui.home


import android.os.Bundle
import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.base.BaseFragment
import de.ka.jamit.tcalc.base.events.FragmentResultable
import de.ka.jamit.tcalc.databinding.FragmentHomeBinding
import de.ka.jamit.tcalc.ui.home.dialog.HomeEnterDialog
import timber.log.Timber

class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(
    R.layout.fragment_home,
    HomeViewModel::class
), FragmentResultable {

    override fun onFragmentResult(resultBundle: Bundle) {
        val value = resultBundle.getInt(HomeEnterDialog.DIALOG_CHOOSE_RESULT)
        val key = resultBundle.getLong(HomeEnterDialog.DIALOG_CHOOSE_KEY)
        Timber.d("Chosen value = $value, for key: $key")
        viewModel.updateItem(value, key)
    }

    override fun getResultRequestKey() = HomeEnterDialog.RESULT_KEY
}
