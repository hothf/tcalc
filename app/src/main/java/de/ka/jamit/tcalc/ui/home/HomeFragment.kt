package de.ka.jamit.tcalc.ui.home


import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.base.BaseFragment
import de.ka.jamit.tcalc.databinding.FragmentHomeBinding

class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(
    R.layout.fragment_home,
    HomeViewModel::class
)
