package de.ka.jamit.arch.ui.settings.list

import de.ka.jamit.arch.base.BaseItemViewModel
import de.ka.jamit.arch.repo.api.BasePeople
import de.ka.jamit.arch.utils.AndroidSchedulerProvider
import de.ka.jamit.arch.utils.with
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import retrofit2.Response
import timber.log.Timber

class SettingsItemViewModel(val item: SettingsItem,
                            private val listener: (SettingsItemViewModel) -> Unit) : BaseItemViewModel() {

    val title = item.id.toString()

    fun onClick() {
        listener(this)

        // the following is just a showcase of loading within an item. Please add loading and error handling in
        // productive apps!!!
        // see if the call is fired it should get immediately cancelled, because the viewmodel is no longer in use as
        // a new view places itself above it.

        compositeDisposable?.plusAssign(
                repository.getPeople()
                        .with(AndroidSchedulerProvider())
                        .subscribeBy(onSuccess = { result: Response<BasePeople?> -> Timber.e("Success: $result") })
        )
    }
}
