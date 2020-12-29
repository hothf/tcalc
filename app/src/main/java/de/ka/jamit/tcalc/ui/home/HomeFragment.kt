package de.ka.jamit.tcalc.ui.home

import android.os.Bundle
import androidx.fragment.app.setFragmentResultListener
import dagger.hilt.android.AndroidEntryPoint
import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.base.BaseFragment
import de.ka.jamit.tcalc.base.events.FragmentResultable
import de.ka.jamit.tcalc.base.events.NavigateTo
import de.ka.jamit.tcalc.base.navigate
import de.ka.jamit.tcalc.databinding.FragmentHomeBinding
import de.ka.jamit.tcalc.ui.home.list.HomeListAdapter
import de.ka.jamit.tcalc.ui.home.sorting.SortingDialog
import de.ka.jamit.tcalc.utils.NavigationUtils.navigateTo


// Workaround for https://github.com/google/dagger/issues/1904
abstract class BaseHomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(
        R.layout.fragment_home,
        HomeViewModel::class
)

@AndroidEntryPoint
class HomeFragment : BaseHomeFragment(), FragmentResultable {

    override fun getResultRequestKey() = SortingDialog.RESULT_KEY

    override fun onFragmentResult(resultBundle: Bundle) {
        (resultBundle.getSerializable(SortingDialog.SORTING_KEY) as? HomeListAdapter.Sorting)?.let(viewModel::sort)
    }

    override fun onHandle(element: Any?) {
        if (element is HomeViewModel.SortClick) {
            setFragmentResultListener(getResultRequestKey()) { _, bundle ->
                this.onFragmentResult(bundle)
            }
            val arguments = Bundle().apply {
                putSerializable(SortingDialog.CURRENTLY_SELECTED_KEY, element.currentSorting)
            }
            navigate(NavigateTo(R.id.dialogSorting, args = arguments))
        }
    }
}
