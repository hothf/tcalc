package de.ka.jamit.tcalc.ui.home.category

import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.base.BaseDialogFragment
import de.ka.jamit.tcalc.databinding.DialogCategoryBinding

/**
 * A dialog for picking a category.
 *
 * Created by Thomas Hofmann on 10.07.20
 **/
// Workaround for https://github.com/google/dagger/issues/1904
abstract class BaseCategoryDialogFragment : BaseDialogFragment<DialogCategoryBinding, CategoryDialogViewModel>(
        R.layout.dialog_category,
        CategoryDialogViewModel::class,
        DialogMode.DIALOG,
        cancellable = true
)

@AndroidEntryPoint
class CategoryDialog : BaseCategoryDialogFragment() {

    override fun onHandle(element: Any?) {
        if (element is CategoryDialogViewModel.Choose) {
            if (element.id != null) {
                val bundle = Bundle().apply { putInt(ID_KEY, element.id) }
                dismissDialogWithResult(RESULT_KEY, bundle)
            } else {
                dismissAllowingStateLoss()
            }
        }
    }

    companion object {
        const val ID_KEY = "_k_id"
        const val RESULT_KEY = "_k_cat_res"
    }
}