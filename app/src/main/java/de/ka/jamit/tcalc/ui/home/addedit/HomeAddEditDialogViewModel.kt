package de.ka.jamit.tcalc.ui.home.addedit

import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.base.BaseViewModel
import de.ka.jamit.tcalc.repo.Repository
import de.ka.jamit.tcalc.repo.db.*
import de.ka.jamit.tcalc.utils.InputValidator
import de.ka.jamit.tcalc.utils.ValidationRules
import de.ka.jamit.tcalc.utils.resources.ResourcesProvider

/**
 * A ViewModel for updating or creating a new home entry.
 *
 * Created by Thomas Hofmann on 03.07.20
 **/
class HomeAddEditDialogViewModel
@ViewModelInject constructor(
        @Assisted private val stateHandle: SavedStateHandle,
        val repository: Repository,
        val inputValidator: InputValidator,
        val resourcesProvider: ResourcesProvider
) : BaseViewModel() {

    private var isUpdating = false
    private var id: Int = 0

    val keyText = MutableLiveData<String>("")
    val valueText = MutableLiveData<String>("")
    val timeSpanPosition = MutableLiveData(0)
    val categoryPosition = MutableLiveData(0)
    val keyError = MutableLiveData<String?>(null)
    val valueError = MutableLiveData<String?>(null)
    val valueSelection = MutableLiveData<Int>(0)
    val keySelection = MutableLiveData<Int>(0)
    val categoryImage = MutableLiveData<Int>(Category.COMMON.resId)
    val categoryShade = MutableLiveData<Int>(resourcesProvider.getColor(Category.COMMON.shadeRes))
    val consideredCheck = MutableLiveData<Boolean>(true)
    val incomeCheck = MutableLiveData<Boolean>(false)
    val editOrNewText = MutableLiveData<String>(resourcesProvider.getString(R.string.home_addedit_title_add))

    private val keyValidator = inputValidator.Validator(
            InputValidator.ValidatorConfig(
                    keyError,
                    listOf(ValidationRules.NOT_EMPTY, ValidationRules.MIN_3)
            )
    )
    private val valueValidator = inputValidator.Validator(
            InputValidator.ValidatorConfig(
                    valueError,
                    listOf(ValidationRules.NOT_EMPTY, ValidationRules.IS_FLOAT)
            )
    )

    fun timeSpanAdapter(): SpinnerAdapter {
        val names = TimeSpan.values().map {
            resourcesProvider.getString(it.translationRes)
        }.toTypedArray()
        return ArrayAdapter<String>(
                resourcesProvider.getApplicationContext(),
                R.layout.spinner_item,
                names).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }

    fun updateCategory(id: Int) {
        categoryPosition.postValue(id)
        val category = Category.values().find { id == it.id }
                ?: Category.COMMON
        categoryImage.postValue(category.resId)
        categoryShade.postValue(resourcesProvider.getColor(category.shadeRes))

    }

    fun onClose() {
        handle(Choose())
    }

    fun choose() {
        val isValid = keyValidator.isValid(keyText.value) and valueValidator.isValid(valueText.value)
        if (!isValid) return

        val value = valueText.value?.toFloat() ?: 0.0f
        val key = keyText.value ?: ""
        val timeSpan = timeSpanPosition.value?.let { v ->
            TimeSpan.values().find { v == it.id }
        } ?: TimeSpan.MONTHLY
        val category = categoryPosition.value?.let { v ->
            Category.values().find { v == it.id }
        } ?: Category.COMMON
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
            handle(CategoryChosen(it))
        }
    }

    /**
     * Update the viewModel with new data.
     */
    fun updateWith(updatedId: Int,
                   updating: Boolean,
                   key: String,
                   value: String,
                   timeSpan: Int,
                   category: Int,
                   considered: Boolean,
                   income: Boolean) {
        isUpdating = updating
        id = updatedId
        keyText.postValue(AppDatabase.getTranslatedStringForKey(resourcesProvider, key))
        valueText.postValue(value)
        timeSpanPosition.postValue(timeSpan)
        consideredCheck.postValue(considered)
        incomeCheck.postValue(income)
        editOrNewText.postValue(if (isUpdating) resourcesProvider.getString(R.string.home_addedit_title_edit) else resourcesProvider.getString(R.string.home_addedit_title_add))
        updateCategory(category)
    }

    class Choose
    class CategoryChosen(val position: Int)
}