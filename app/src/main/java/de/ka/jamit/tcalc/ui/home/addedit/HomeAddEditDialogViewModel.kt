package de.ka.jamit.tcalc.ui.home.addedit

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import androidx.lifecycle.MutableLiveData
import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.base.BaseViewModel
import de.ka.jamit.tcalc.repo.db.AppDatabase
import de.ka.jamit.tcalc.repo.db.RecordDao
import de.ka.jamit.tcalc.ui.home.category.CategoryDialog
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
    val categoryPosition = MutableLiveData(0)
    val categoryImage = MutableLiveData<Int>(RecordDao.Category.COMMON.resId)
    val consideredCheck = MutableLiveData<Boolean>(true)
    val incomeCheck = MutableLiveData<Boolean>(false)

    fun timeSpanAdapter(): SpinnerAdapter {
        val names = RecordDao.TimeSpan.values().map { it.name }.toTypedArray()
        return ArrayAdapter<String>(
                resourcesProvider.getApplicationContext(),
                android.R.layout.simple_spinner_item,
                names).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }

    fun updateCategory(id: Int) {
        categoryPosition.postValue(id)
        val categoryImageRes = RecordDao.Category.values().find { id == it.id }
                ?: RecordDao.Category.COMMON
        categoryImage.postValue(categoryImageRes.resId)
    }

    fun choose() {
        val value = valueText.value?.toFloat() ?: 0.0f
        val key = keyText.value ?: ""
        val timeSpan = timeSpanPosition.value?.let { v ->
            RecordDao.TimeSpan.values().find { v == it.id }
        } ?: RecordDao.TimeSpan.MONTHLY
        val category = categoryPosition.value?.let { v ->
            RecordDao.Category.values().find { v == it.id }
        } ?: RecordDao.Category.COMMON
        val isConsidered = consideredCheck.value ?: true
        val isIncome = incomeCheck.value ?: false

        if (isUpdating) {
            repository.updateRecord(
                    value = value,
                    key = key,
                    timeSpan = timeSpan,
                    category = category,
                    isConsidered = isConsidered,
                    isIncome = isIncome,
                    id = id)
        } else { // is creating a new entry!
            repository.addRecord(
                    key = key,
                    value = value,
                    timeSpan = timeSpan,
                    category = category,
                    isConsidered = isConsidered,
                    isIncome = isIncome)
        }
        handle(Choose())
    }

    fun onCategory() {
        categoryPosition.value?.let {
            val arguments = Bundle().apply { putInt(CategoryDialog.ID_KEY, it) }
            navigateTo(R.id.dialogCategory, args = arguments)
        }
    }

    override fun onArgumentsReceived(bundle: Bundle) {
        super.onArgumentsReceived(bundle)
        isUpdating = bundle.getBoolean(HomeAddEditDialog.UPDATE_KEY, false)
        val key = bundle.getString(HomeAddEditDialog.TITLE_KEY) ?: ""
        keyText.postValue(AppDatabase.getTranslatedStringForKey(resourcesProvider, key))
        valueText.postValue(bundle.getFloat(HomeAddEditDialog.VALUE_KEY).toString())
        timeSpanPosition.postValue(bundle.getInt(HomeAddEditDialog.TIMESPAN_KEY))
        val category = bundle.getInt(HomeAddEditDialog.CATEGORY_KEY)
        updateCategory(category)
        consideredCheck.postValue(bundle.getBoolean(HomeAddEditDialog.CONSIDERED_KEY))
        incomeCheck.postValue(bundle.getBoolean(HomeAddEditDialog.INCOME_KEY))
        id = bundle.getLong(HomeAddEditDialog.ID_KEY)
    }


    class Choose
}