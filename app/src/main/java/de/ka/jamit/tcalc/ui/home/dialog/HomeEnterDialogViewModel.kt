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
    val valueText = MutableLiveData<String>("")

    private var key: Long = 0L

    fun choose() {
        valueText.value?.toFloat()?.let {
            repository.updateRecord(it, key)
            handle(Choose(it, key))
        }
    }

    override fun onArgumentsReceived(bundle: Bundle) {
        super.onArgumentsReceived(bundle)
        titleText.postValue(bundle.getString(HomeEnterDialog.TITLE_KEY) ?: "")
        valueText.postValue(bundle.getFloat(HomeEnterDialog.VALUE_KEY).toString())
        key = bundle.getLong(HomeEnterDialog.RESULT_KEY)
    }

    class Choose(val value: Float, val key: Long)
}