package com.arinal.ui.splash

import com.arinal.R
import com.arinal.databinding.FragmentSplashBinding
import com.arinal.ui.MainViewModel
import com.arinal.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SplashFragment : BaseFragment<FragmentSplashBinding, MainViewModel>() {
    override val viewModel: MainViewModel by sharedViewModel()
    override fun setLayout() = R.layout.fragment_splash
    override fun observeLiveData() = Unit
}
