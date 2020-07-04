package de.ka.jamit.tcalc.ui.home.add

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import androidx.lifecycle.MutableLiveData
import de.ka.jamit.tcalc.base.BaseViewModel
import de.ka.jamit.tcalc.repo.db.AppDatabase
import de.ka.jamit.tcalc.repo.db.RecordDao
import de.ka.jamit.tcalc.utils.resources.ResourcesProvider
import org.koin.core.inject

/**
 * A ViewModel for updating or creating a new home entry.
 *
 * Created by Thomas Hofmann on 03.07.20
 **/
class HomeAddEditDialogViewModel : BaseViewModel() {

    private val resourcesProvider: ResourcesProvider by inject()
    private var isUpdating = false
    private var id: Long = 0L

    val keyText = MutableLiveData<String>("")
    val valueText = MutableLiveData<String>("")
    val timeSpanPosition = MutableLiveData(0)

    fun timeSpanAdapter(): SpinnerAdapter {
        val names = RecordDao.TimeSpan.values().map { it.name }.toTypedArray()
        return ArrayAdapter<String>(
                resourcesProvider.getApplicationContext(),
                android.R.layout.simple_spinner_item,
                names).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }

    fun choose() {
        val value = valueText.value?.toFloat() ?: 0.0f
        val key = keyText.value ?: ""
        val timeSpan = timeSpanPosition.value?.let {
            RecordDao.TimeSpan.values()[it]
        } ?: RecordDao.TimeSpan.MONTHLY

        if (isUpdating) {
            repository.updateRecord(
                    value = value,
                    key = key,
                    timeSpan = timeSpan,
                    id = id)
        } else { // is creating a new entry!
            repository.addRecord(
                    key = key,
                    value = value,
                    timeSpan = timeSpan)
        }
        handle(Choose())
    }

    override fun onArgumentsReceived(bundle: Bundle) {
        super.onArgumentsReceived(bundle)
        isUpdating = bundle.getBoolean(HomeAddEditDialog.UPDATE_KEY, false)
        val key = bundle.getString(HomeAddEditDialog.TITLE_KEY) ?: ""
        keyText.postValue(AppDatabase.getTranslatedStringForKey(resourcesProvider, key))
        valueText.postValue(bundle.getFloat(HomeAddEditDialog.VALUE_KEY).toString())
        timeSpanPosition.postValue(bundle.getInt(HomeAddEditDialog.TIMESPAN_KEY))
        id = bundle.getLong(HomeAddEditDialog.ID_KEY)
    }

    class Choose
}