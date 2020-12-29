package de.ka.jamit.tcalc.ui.home.addedit

import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.base.BaseDialogFragment
import de.ka.jamit.tcalc.base.events.FragmentResultable
import de.ka.jamit.tcalc.databinding.DialogHomeAddeditBinding
import de.ka.jamit.tcalc.ui.home.category.CategoryDialog

/**
 * A bottom sheet for adding a new value.
 *
 * Created by Thomas Hofmann on 03.07.20
 **/
// Workaround for https://github.com/google/dagger/issues/1904
abstract class BaseAddEditDialogFragment : BaseDialogFragment<DialogHomeAddeditBinding, HomeAddEditDialogViewModel>(
        R.layout.dialog_home_addedit,
        HomeAddEditDialogViewModel::class,
        DialogMode.BOTTOM_SHEET,
        cancellable = true
)

@AndroidEntryPoint
class HomeAddEditDialog : BaseAddEditDialogFragment(), FragmentResultable {

    override fun onHandle(element: Any?) {
        if (element is HomeAddEditDialogViewModel.Choose) {
            dismissAllowingStateLoss()
        }
    }

    // TODO check keys and move them away so that viewModels no longer depend on dialogs and fragments ...
    companion object {
        const val TITLE_KEY = "_k_title_"
        const val ID_KEY = "_k_id_"
        const val VALUE_KEY = "_k_val_"
        const val TIMESPAN_KEY = "_k_timespan_"
        const val CATEGORY_KEY = "_k_category"
        const val UPDATE_KEY = "_k_isupdate_"
        const val CONSIDERED_KEY = "_k_consid"
        const val INCOME_KEY = "_k_income"
    }

    override fun getResultRequestKey(): String {
        return CategoryDialog.RESULT_KEY
    }

    override fun onFragmentResult(resultBundle: Bundle) {
        val result = resultBundle.getInt(CategoryDialog.ID_KEY)
        viewModel.updateCategory(result)
    }
}

