package de.ka.jamit.tcalc.ui.home.user.addedit

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import de.ka.jamit.tcalc.base.BaseViewModel
import de.ka.jamit.tcalc.utils.InputValidator
import de.ka.jamit.tcalc.utils.ValidationRules
import de.ka.jamit.tcalc.utils.resources.ResourcesProvider
import org.koin.core.inject

/**
 * A ViewModel for updating or creating a new user entry.
 *
 * Created by Thomas Hofmann on 08.07.20
 **/
class UserAddEditDialogViewModel : BaseViewModel() {

    private val resourcesProvider: ResourcesProvider by inject()
    private val inputValidator: InputValidator by inject()
    private var isUpdating = false
    private var isSelected = false
    private var id: Long = 0L

    val titleText = MutableLiveData<String>("")
    val titleError = MutableLiveData<String?>(null)
    val titleSelection = MutableLiveData<Int>(0)

    private val titleValidator = inputValidator.Validator(
            InputValidator.ValidatorConfig(
                    titleError,
                    listOf(ValidationRules.NOT_EMPTY, ValidationRules.MIN_4)
            )
    )

    fun choose() {
        if (!titleValidator.isValid(titleText.value)) return

        val title = titleText.value ?: ""
        if (isUpdating) {
            repository.updateUser(
                    name = title,
                    selected = isSelected,
                    id = id)
        } else { // is creating a new entry!
            repository.addUser(name = title)
        }
        handle(Choose())
    }

    override fun onArgumentsReceived(bundle: Bundle) {
        super.onArgumentsReceived(bundle)
        isUpdating = bundle.getBoolean(UserAddEditDialog.UPDATE_KEY, false)
        isSelected = bundle.getBoolean(UserAddEditDialog.IS_SELECTED_KEY, false)
        val title = bundle.getString(UserAddEditDialog.TITLE_KEY, "")
        titleText.postValue(title)
        titleSelection.postValue(title.length)
        id = bundle.getLong(UserAddEditDialog.ID_KEY)
    }

    class Choose
}