package de.ka.jamit.tcalc.ui.home.add

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
class HomeAddDialogViewModel : BaseViewModel() {

    private val resourcesProvider: ResourcesProvider by inject()

    val keyText = MutableLiveData<String>("")
    val valueText = MutableLiveData<String>("")

    fun choose() {
        valueText.value?.toFloat()?.let {
            repository.addRecord(
                    key = keyText.value ?: "",
                    value = valueText.value?.toFloat() ?: 0.0f)
            handle(Choose())
        }
    }

    class Choose
}