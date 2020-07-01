package de.ka.jamit.tcalc.base

import android.content.DialogInterface
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import android.view.KeyEvent
import de.ka.jamit.tcalc.BR
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import de.ka.jamit.tcalc.base.events.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import timber.log.Timber
import kotlin.reflect.KClass

/**
 * Represents a base dialog fragment. Extending fragments should always be combined with a viewModel,
 * that's why offering the fragment layout resource id and viewModel is mandatory.
 * The viewModel updates the ui via `Databinding`.
 *
 * Dialogs can have different appearing types, choose it via setting [dialogMode].
 *
 * To use the binding of the ui after inflating in sub classes, use [getBinding].
 *
 * As the base fragment is capable of observing events fired in sub classes of [BaseViewModel]s attached to the
 * fragment, there are several stub methods which can be overridden to make use of these events. This way, the
 * viewModel does not have to directly call Android Apis but can invoke event callbacks in the activity to separate
 * all concerns.
 *
 *  @author Thomas Hofmann
 */
abstract class BaseDialogFragment<out T : ViewDataBinding, E : BaseViewModel>(
    @LayoutRes private val bindingLayoutId: Int,
    clazz: KClass<E>,
    private val dialogMode: DialogMode = DialogMode.DIALOG,
    private val cancellable: Boolean = true
) : BottomSheetDialogFragment(), BaseComposable {

    private var binding: ViewDataBinding? = null

    val viewModel: E by lazy { getViewModel(clazz) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (dialogMode == DialogMode.DIALOG) return null

        super.onCreateView(inflater, container, savedInstanceState)

        return createView(inflater, container, savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?) = when (dialogMode) {
        DialogMode.DIALOG -> setupDialog(savedInstanceState)
        DialogMode.BOTTOM_SHEET -> setupBottomSheet(savedInstanceState)
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun setupBottomSheet(savedInstanceState: Bundle?) = super.onCreateDialog(savedInstanceState)
        .also { dialog ->
            //Fix to expand the bottomsheet when it is shown
            dialog.setOnShowListener { showingDialog ->
                if (showingDialog is BottomSheetDialog) {
                    val bottomSheet: FrameLayout = showingDialog
                        .findViewById(com.google.android.material.R.id.design_bottom_sheet)
                        ?: return@setOnShowListener

                    val behaviour = BottomSheetBehavior.from(bottomSheet)

                    bottomSheet.post {
                        behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                        behaviour.skipCollapsed = true
                    }
                }
            }
            dialog.setOnKeyListener(keyListener)
        }

    private val keyListener = { _: DialogInterface, keyCode: Int, event: KeyEvent ->
        var consumed = false
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
            if (onConsumeBackPress()) {
                consumed = true
            }
        }
        consumed
    }

    private fun setupDialog(savedInstanceState: Bundle?) = AlertDialog.Builder(requireContext())
        .setView(createView(requireActivity().layoutInflater, null, savedInstanceState))
        .setOnKeyListener(keyListener)
        .create()
        .apply {
            window?.requestFeature(Window.FEATURE_NO_TITLE)
        }

    private fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        isCancelable = cancellable
        binding = DataBindingUtil.inflate<T>(inflater, bindingLayoutId, container, false)
            .apply {
                setVariable(BR.viewModel, viewModel)
                lifecycleOwner = this@BaseDialogFragment
                executePendingBindings()
                onComposed(this, savedInstanceState)
            }

        viewModel.events.observe(
            this,
            Observer {
                Timber.i("Event observed in Dialog: $it")
                consumeFragmentEvents(it)
            }
        )

        arguments?.let {
            viewModel.onArgumentsReceived(it)
        }

        return binding?.root
    }

    /**
     * Consumes event emitted by a viewModel on fragment level.
     */
    private fun consumeFragmentEvents(event: Event) {
        when (event) {
            is NavigateTo -> navigate(event)
            is Open -> openUp(event)
            is Handle<*> -> onHandle(event.element)
            is ShowSnack, is Close, is ConsumeBackPress -> getBaseActivity()?.consumeActivityEvents(event)
        }
    }

    /**
     * Called when a back press occurred. If the back press should be consumed, return true, else return false and the
     * back press will be handled by the activity as usual.
     */
    open fun onConsumeBackPress(): Boolean {
        return false
    }

    private fun getBaseActivity(): BaseActivity<*, *>? {
        return (requireActivity() as? BaseActivity<*, *>)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getBinding(): T? {
        return binding as? T
    }

    override fun <T> onComposed(binding: T, savedInstanceState: Bundle?) {
        // to be implemented in sub classes
    }

    override fun onHandle(element: Any?) {
        // to be implemented in sub class
    }

    /**
     * Dismisses the dialog and saves the given bundle as a result.
     *
     * The [de.ka.jamit.tcalc.base.BaseFragment] upon which this dialog is shown will then retrieve the result.
     * Please note that this will only work in these fragments and that if the dialog is started elsewhere the
     * retrieving logic has to be implemented there as well.
     *
     * Receiving fragments have to implement [FragmentResultable] for this to share a key where the request of the
     * result will be matched, that's why a [resultRequestKey] here should match the one which wants to receive it.
     */
    fun dismissDialogWithResult(resultRequestKey: String, resultBundle: Bundle) {
        setFragmentResult(resultRequestKey, resultBundle)
        dismiss()
    }

    /**
     * Lists all possible dialog modes
     */
    enum class DialogMode {

        /**
         * A bottom sheet opens from the bottom and may allow for picking.
         */
        BOTTOM_SHEET,

        /**
         * A dialog interrupts the current flow and is displayed in the center.
         */
        DIALOG
    }
}