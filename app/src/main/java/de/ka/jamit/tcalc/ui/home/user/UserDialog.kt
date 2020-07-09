package de.ka.jamit.tcalc.ui.home.user

import android.os.Bundle
import android.view.View
import android.view.View.OnTouchListener
import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.base.BaseDialogFragment
import de.ka.jamit.tcalc.databinding.DialogUserBinding
import kotlinx.android.synthetic.main.dialog_user.view.*


/**
 * A bottom sheet for adding a new value.
 *
 * Created by Thomas Hofmann on 03.07.20
 **/
class UserDialog : BaseDialogFragment<DialogUserBinding, UserDialogViewModel>(
        R.layout.dialog_user,
        UserDialogViewModel::class,
        DialogMode.BOTTOM_SHEET,
        cancellable = true
) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        view.userRecycler.setOnTouchListener { v, event ->
//            v.parent.requestDisallowInterceptTouchEvent(true)
//            v.onTouchEvent(event)
//            true
//        }
    }

    override fun onHandle(element: Any?) {
        if (element is UserDialogViewModel.Close) {
            dismissAllowingStateLoss()
        }
    }
}

