package de.ka.jamit.tcalc.ui.home.dialog

import android.content.res.Resources
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import de.ka.jamit.tcalc.base.BaseViewModel
import de.ka.jamit.tcalc.repo.db.AppDatabase
import de.ka.jamit.tcalc.utils.resources.ResourcesProvider
import org.koin.core.inject

/**
 * A ViewModel for entering a value.
 *
 * Created by Thomas Hofmann on 03.07.20
 **/
class HomeEnterDialogViewModel : BaseViewModel() {

    private val resourcesProvider: ResourcesProvider by inject()

    val titleText = MutableLiveData<String>("")
    val valueText = MutableLiveData<String>("")

    private var id: Long = 0L

    fun choose() {
        valueText.value?.toFloat()?.let {
            repository.updateRecord(it, id)
            handle(Choose(it, id))
        }
    }

    override fun onArgumentsReceived(bundle: Bundle) {
        super.onArgumentsReceived(bundle)
        val key = bundle.getString(HomeEnterDialog.TITLE_KEY) ?: ""
        titleText.postValue(AppDatabase.getTranslatedStringForKey(resourcesProvider, key))
        valueText.postValue(bundle.getFloat(HomeEnterDialog.VALUE_KEY).toString())
        id = bundle.getLong(HomeEnterDialog.ID_KEY)
    }

    class Choose(val value: Float, val key: Long)
}