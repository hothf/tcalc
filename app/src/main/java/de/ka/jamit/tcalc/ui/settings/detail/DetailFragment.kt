package de.ka.jamit.tcalc.ui.settings.detail

import androidx.fragment.app.setFragmentResult
import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.base.BaseFragment
import de.ka.jamit.tcalc.databinding.FragmentDetailBinding

class DetailFragment :
    BaseFragment<FragmentDetailBinding, DetailViewModel>(R.layout.fragment_detail, DetailViewModel::class) {

    override fun onHandle(element: Any?) {
        super.onHandle(element)

        if (element is DetailViewModel.DetailResult) {
            element.result?.let {
                setFragmentResult("SettingsResultRequest", it)
            }

        }
    }
}