package com.arinal.ui.home

import androidx.core.content.ContextCompat.getColor
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arinal.R
import com.arinal.databinding.FragmentHomeBinding
import com.arinal.ui.MainViewModel
import com.arinal.ui.base.BaseFragment
import com.arinal.ui.home.adapter.ShimmerAdapter
import com.arinal.ui.home.adapter.UsersAdapter
import com.arinal.utils.EndlessScrollListener
import com.arinal.utils.EventObserver
import com.bumptech.glide.Glide
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class HomeFragment : BaseFragment<FragmentHomeBinding, MainViewModel>() {

    private var queryJob: Job = Job()
    private val endlessScroll = object : EndlessScrollListener() {
        override fun onLoadMore() = viewModel.loadData()
    }
    private val shimmerAdapter by lazy { ShimmerAdapter(layoutInflater, 20) }
    private val usersAdapter by lazy { UsersAdapter(layoutInflater, Glide.with(requireContext()), ::onItemClick) }
    override val viewModel: MainViewModel by sharedViewModel()

    override fun setLayout() = R.layout.fragment_home

    override fun initViews() = with(viewModel) {
        with(binding) {
            collapsingToolbar.setExpandedTitleColor(getColor(requireContext(), android.R.color.transparent))
            collapsingToolbar.setCollapsedTitleTextColor(getColor(requireContext(), R.color.gray_500))
            rvShimmer.adapter = shimmerAdapter
            rvUsers.adapter = usersAdapter
            rvUsers.addOnScrollListener(endlessScroll)
            rvUsers.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val firstVisibleItem = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    if (firstVisibleItem == 0) fabScroll.hide()
                    else fabScroll.show()
                    super.onScrolled(recyclerView, dx, dy)
                }
            })
            swipeLayout.setOnRefreshListener {
                swipeLayout.isRefreshing = false
                clearList()
                endlessScroll.resetData()
                loadData()
            }
        }
        initData()
    }

    override fun observeLiveData() {
        viewModel.userList.observe(viewLifecycleOwner, {
            if (!viewModel.isOnSearch()) usersAdapter.submitList(it)
        })
        viewModel.searchList.observe(viewLifecycleOwner, {
            if (viewModel.isOnSearch()) usersAdapter.submitList(it)
        })
        viewModel.searchQuery.observe(viewLifecycleOwner, {
            lifecycleScope.launch {
                queryJob.cancelAndJoin()
                queryJob = launch {
                    delay(500)
                    if (!it?.toString().isNullOrEmpty()) viewModel.startSearch()
                    else if (viewModel.isOnSearch()) {
                        viewModel.stopSearch()
                        if (viewModel.userList.value?.size ?: 0 <= 0) viewModel.loadData()
                        else usersAdapter.submitList(viewModel.userList.value)
                        binding.etSearch.clearFocus()
                        binding.btnClearSearch.requestFocus()
                        goToTop()
                    }
                    endlessScroll.resetData()
                }
            }
        })
        viewModel.goToTop.observe(viewLifecycleOwner, EventObserver { goToTop() })
    }

    private fun goToTop() {
        binding.rvUsers.scrollToPosition(0)
        binding.appBar.setExpanded(true, true)
    }

    private fun onItemClick(position: Int) {
        viewModel.selectedIndex = position
        findNavController().navigate(R.id.action_homeFragment_to_detailFragment)
    }

}
