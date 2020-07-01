package de.ka.jamit.arch.ui.settings.detail

import androidx.fragment.app.setFragmentResult
import de.ka.jamit.arch.R
import de.ka.jamit.arch.base.BaseFragment
import de.ka.jamit.arch.databinding.FragmentDetailBinding

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