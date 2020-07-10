package de.ka.jamit.tcalc.ui.home.category

import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import de.ka.jamit.tcalc.base.BaseViewModel
import de.ka.jamit.tcalc.repo.db.RecordDao
import de.ka.jamit.tcalc.utils.resources.ResourcesProvider
import org.koin.core.inject

/**
 * A ViewModel for importing records.
 *
 * Created by Thomas Hofmann on 09.07.20
 **/
class CategoryDialogViewModel : BaseViewModel() {

    private val resourcesProvider: ResourcesProvider by inject()
    private var id: Int? = null

    fun layoutManager() = GridLayoutManager(resourcesProvider.getApplicationContext(), 2)
    val adapter = CategoryListAdapter()

    override fun onArgumentsReceived(bundle: Bundle) {
        super.onArgumentsReceived(bundle)

        id = bundle.getInt(CategoryDialog.ID_KEY)
        populateList()
    }

    private val listener: (CategoryListItemViewModel) -> Unit = {
        handle(Choose(id = it.item.id))
    }

    private fun populateList() {
        val items = RecordDao.Category.values().map {
            CategoryListItemViewModel(it, it.id == id, listener)
        }
        adapter.setItems(items)
    }

    fun onCancel() {
        handle(Choose())
    }

    class Choose(val id: Int? = null)
}