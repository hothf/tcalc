package de.ka.jamit.tcalc.base

import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import de.ka.jamit.tcalc.BR
import de.ka.jamit.tcalc.base.events.*
import timber.log.Timber
import kotlin.reflect.KClass

/**
 * Represents a base activity. Extending activities should always be combined with a viewModel,
 * that's why offering the activity layout resource id and viewModel is mandatory.
 *
 * The viewModel updates the ui via `Databinding`.
 *
 * To use the binding of the ui after inflating in sub classes, use [getBinding].
 *
 * As the base activity is capable of observing events fired in sub classes of [BaseViewModel]s attached to the
 * activity, there are several stub methods which can be overridden to make use of these events. This way, the
 * viewModel does not have to directly call Android Apis but can invoke event callbacks in the activity to separate
 * all concerns.
 *
 * @param bindingLayoutId the layout resource id of the binding which binds the layout for data changes to this activity
 * @param clazz the viewModel intended to update the layout of this activity with data mappings through databinding
 *
 * Created by Thomas Hofmann
 */
abstract class BaseActivity<out T : ViewDataBinding, E : BaseViewModel>(
    @LayoutRes private val bindingLayoutId: Int,
    private val clazz: KClass<E>
) : AppCompatActivity(), BaseComposable {

    private var binding: ViewDataBinding? = null

    val viewModel: E
        get() = ViewModelProvider(this).get(clazz.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.inflate(layoutInflater, bindingLayoutId, null, true)
        binding?.apply {
            setVariable(BR.viewModel, viewModel)
            lifecycleOwner = this@BaseActivity
            executePendingBindings()
            onComposed(this, savedInstanceState)
            setContentView(this.root)
        }

        viewModel.events.observe(
            this,
            Observer {
                Timber.i("Event observed in Activity: $it")
                consumeActivityEvents(it)
            }
        )

        intent?.extras?.let {
            onArgumentsReceived(it)
        }
    }

    /**
     * Called when new arguments in a [bundle] have been received.
     */
    open fun onArgumentsReceived(bundle: Bundle){
        // handled by children
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    /**
     * Consumes an [event] emitted by a viewModel on activity level.
     */
    fun consumeActivityEvents(event: Event) {
        when (event) {
            is Handle<*> -> onHandle(event.element)
            is ShowSnack -> onShowMessage(event)
            is Close -> onFinish()
            is ConsumeBackPress -> super.onBackPressed()
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getBinding(): T? {
        return binding as? T
    }

    override fun <T> onComposed(binding: T, savedInstanceState: Bundle?) {
        // to be implemented in sub classes
    }

    override fun onHandle(element: Any?) {
        // to be implemented in sub classes
    }

    /**
     * Called when a message with the contents [showSnack] should be shown.
     */
    open fun onShowMessage(showSnack: ShowSnack) {
        // to be implemented in sub class
    }

    /**
     * Called when the activity should be finished.
     */
    open fun onFinish() {
        // to be implemented in sub class
    }
}

