package de.ka.jamit.tcalc.ui.home.addedit

import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.base.BaseDialogFragment
import de.ka.jamit.tcalc.base.events.FragmentResultable
import de.ka.jamit.tcalc.base.events.NavigateTo
import de.ka.jamit.tcalc.base.navigate
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
        } else if (element is HomeAddEditDialogViewModel.CategoryChosen) {
            val arguments = Bundle().apply { putInt(CategoryDialog.ID_KEY, element.position) }
            navigate(NavigateTo(R.id.dialogCategory, args = arguments))
        }
    }

    override fun onArgumentsReceived(bundle: Bundle) {
        val isUpdating = bundle.getBoolean(UPDATE_KEY, false)
        val key = bundle.getString(TITLE_KEY) ?: ""
        val value = bundle.getFloat(VALUE_KEY).toString()
        val timeSpan = bundle.getInt(TIMESPAN_KEY)
        val category = bundle.getInt(CATEGORY_KEY)
        val consideredCheck = bundle.getBoolean(CONSIDERED_KEY)
        val incomeCheck = bundle.getBoolean(INCOME_KEY)
        val id = bundle.getInt(ID_KEY)
        viewModel.updateWith(id, isUpdating, key, value, timeSpan, category, consideredCheck, incomeCheck)
    }

    override fun getResultRequestKey(): String {
        return CategoryDialog.RESULT_KEY
    }

    override fun onFragmentResult(resultBundle: Bundle) {
        val result = resultBundle.getInt(CategoryDialog.ID_KEY)
        viewModel.updateCategory(result)
    }

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
}

