package de.ka.jamit.arch.ui.settings.detail

import android.os.Bundle
import de.ka.jamit.arch.base.BaseViewModel

class DetailViewModel : BaseViewModel() {

    private var detailBundle: Bundle? = null

    fun onClickedResult() {
        handle(DetailResult(detailBundle))
        consumeBackPress()
    }

    override fun onArgumentsReceived(bundle: Bundle) {
        // contains the title!
        detailBundle = bundle
        super.onArgumentsReceived(bundle)
    }

    class DetailResult(val result: Bundle?)
}