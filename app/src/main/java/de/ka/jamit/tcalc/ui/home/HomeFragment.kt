package de.ka.jamit.tcalc.ui.home


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
class HomeFragment : BaseHomeFragment()
