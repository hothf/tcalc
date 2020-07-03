package de.ka.jamit.tcalc.ui.home.dialog

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import de.ka.jamit.tcalc.base.BaseViewModel

/**
 * A ViewModel for entering a value.
 *
 * Created by Thomas Hofmann on 03.07.20
 **/
class HomeEnterDialogViewModel : BaseViewModel() {

    val titleText = MutableLiveData<String>("")
    private var key: Long = 0L

    fun choose() {
        handle(Choose(42, key))
    }

    override fun onArgumentsReceived(bundle: Bundle) {
        super.onArgumentsReceived(bundle)
        titleText.postValue(bundle.getString(HomeEnterDialog.TITLE_KEY) ?: "")
        key = bundle.getLong(HomeEnterDialog.RESULT_KEY)
    }

    class Choose(val value: Int, val key: Long)
}