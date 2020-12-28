package de.ka.jamit.tcalc.ui.home


import android.os.Bundle
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.base.BaseFragment
import de.ka.jamit.tcalc.databinding.FragmentHomeBinding


// Workaround for https://github.com/google/dagger/issues/1904
abstract class BaseHomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(
        R.layout.fragment_home,
        HomeViewModel::class
)

@AndroidEntryPoint
class HomeFragment : BaseHomeFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getBinding<FragmentHomeBinding>()?.sortingContainer?.let { container ->
            registerForContextMenu(container)
            container.setOnClickListener {
                container.showContextMenu()
            }
        }
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)

        requireActivity().menuInflater.inflate(R.menu.menu_sorting, menu)
    }
}
