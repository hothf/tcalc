package de.ka.jamit.tcalc.ui.home.user.addedit

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import de.ka.jamit.tcalc.base.BaseViewModel
import de.ka.jamit.tcalc.utils.resources.ResourcesProvider
import org.koin.core.inject

/**
 * A ViewModel for updating or creating a new user entry.
 *
 * Created by Thomas Hofmann on 08.07.20
 **/
class UserAddEditDialogViewModel : BaseViewModel() {

    private val resourcesProvider: ResourcesProvider by inject()
    private var isUpdating = false
    private var isSelected = false
    private var id: Long = 0L

    val titleText = MutableLiveData<String>("")

    fun choose() {
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
        titleText.postValue(bundle.getString(UserAddEditDialog.TITLE_KEY, ""))
        id = bundle.getLong(UserAddEditDialog.ID_KEY)
    }

    class Choose
}