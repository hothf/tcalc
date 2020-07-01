package de.ka.jamit.arch.base

import android.content.pm.PackageManager
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import de.ka.jamit.arch.BR

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import de.ka.jamit.arch.base.events.*
import org.koin.androidx.viewmodel.ext.android.getSharedViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import timber.log.Timber
import kotlin.reflect.KClass

/**
 * Represents a base fragment. Extending fragments should always be combined with a viewModel,
 * that's why offering the fragment layout resource id and viewModel is mandatory.
 * The viewModel updates the ui via `Databinding`.
 *
 * To use the binding of the ui after inflating in sub classes, use [getBinding].
 *
 * As the base fragment is capable of observing events fired in sub classes of [BaseViewModel]s attached to the
 * fragment, there are several stub methods which can be overridden to make use of these events. This way, the
 * viewModel does not have to directly call Android Apis but can invoke event callbacks in the activity to separate
 * all concerns.
 *
 * @param bindingLayoutId the layout resource id of the binding which binds the layout for data changes to this fragment
 * @param clazz the viewModel intended to update the layout of this fragment with data mappings through databinding
 * @param useActivityPool Determines if this viewModel should be stored in the activity scope or not. If set to false will store the
 * viewModel in the scope of the current fragment or activity (depends on what it is attached to) and release it,
 * if the fragment or activity is no longer needed. This is the default setting.
 * If set to true, it uses the scope of a activity, meaning that it will only become useless, if the activity
 * containing the fragment or the activity attached)  with this viewModel is no longer needed.
 * This may be useful for sharing the viewModel data across screens.
 *
 * @author Thomas Hofmann
 */
abstract class BaseFragment<out T : ViewDataBinding, E : BaseViewModel>(
    @LayoutRes private val bindingLayoutId: Int,
    clazz: KClass<E>,
    private val useActivityPool: Boolean = false
) : Fragment(), BaseComposable {

    private var binding: ViewDataBinding? = null

    val viewModel: E by lazy {
        if (useActivityPool) getSharedViewModel(clazz)
        else getViewModel(clazz)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (this is FragmentResultable) {
            setFragmentResultListener(getResultRequestKey()) { _, bundle ->
                this.onFragmentResult(bundle)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = DataBindingUtil.inflate(layoutInflater, bindingLayoutId, null, true)
        binding?.apply {
            setVariable(BR.viewModel, viewModel)
            lifecycleOwner = viewLifecycleOwner
            executePendingBindings()
            onComposed(this, savedInstanceState)
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!onConsumeBackPress()) {
                    isEnabled = false // don't allow for recursions of back press
                    requireActivity().onBackPressed()
                    isEnabled = true
                }
            }
        })

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.events.observe(
            viewLifecycleOwner,
            Observer {
                Timber.i("Event observed in Fragment: $it")
                consumeFragmentEvents(it)
            }
        )

        arguments?.let {
            viewModel.onArgumentsReceived(it)
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    /**
     * Consumes an [event] emitted by a viewModel on fragment level.
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
     * Requests permissions like fragments normally do, but can immediately trigger [onPermissionResult] for the given
     * request code. This makes it convenient to start actions from [onPermissionResult].
     *
     * Add multiple [permissions] if needed.
     */
    fun requestPermission(permissions: Array<out String>) {
        val permissionsNotGranted =
            permissions.filterNot {
                ContextCompat.checkSelfPermission(this.requireContext(), it) == PackageManager.PERMISSION_GRANTED
            }
        if (permissionsNotGranted.isEmpty()) {
            onPermissionResult(true)
        } else {
            val contract = ActivityResultContracts.RequestMultiplePermissions()
            registerForActivityResult(contract) { result ->
                // _ contains all granted and not granted permissions
                onPermissionResult(result.all { it.value })
            }.launch(permissions)
        }
    }

    /**
     * Called on a  permission request result.
     *
     * [allGranted] is set to true if all permissions previously requested with [requestPermission] are granted,
     * otherwise will be set to false.
     */
    open fun onPermissionResult(allGranted: Boolean) {
        // to be implemented in sub class
    }
}