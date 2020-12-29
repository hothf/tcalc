package de.ka.jamit.tcalc.ui.home.sorting

import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.base.BaseDialogFragment
import de.ka.jamit.tcalc.databinding.DialogSortingBinding
import de.ka.jamit.tcalc.ui.home.list.HomeListAdapter

/**
 * A sorting dialog.
 *
 * Created by Thomas Hofmann on 29.12.20
 **/
// Workaround for https://github.com/google/dagger/issues/1904
abstract class BaseSortingDialogFragment : BaseDialogFragment<DialogSortingBinding, SortingDialogViewModel>(
        R.layout.dialog_sorting,
        SortingDialogViewModel::class,
        DialogMode.BOTTOM_SHEET,
        cancellable = true
)

@AndroidEntryPoint
class SortingDialog : BaseSortingDialogFragment() {

    override fun onHandle(element: Any?) {
        if (element is SortingDialogViewModel.Close) {
            dismissAllowingStateLoss()
        } else if (element is SortingDialogViewModel.Choose) {
            dismissDialogWithResult(RESULT_KEY, Bundle().apply { putSerializable(SORTING_KEY, element.sorting) })
        }
    }

    override fun onArgumentsReceived(bundle: Bundle) {
        val selectedItem = bundle.getSerializable(SortingDialog.CURRENTLY_SELECTED_KEY) as? HomeListAdapter.Sorting

        selectedItem?.let(viewModel::select)
    }

    companion object {
        const val SORTING_KEY = "_k_id"
        const val RESULT_KEY = "_k_cat_res"
        const val CURRENTLY_SELECTED_KEY = "_k_selected"
    }
}