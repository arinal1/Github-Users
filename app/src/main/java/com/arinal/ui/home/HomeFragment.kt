package com.arinal.ui.home

import androidx.core.content.ContextCompat.getColor
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
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class HomeFragment : BaseFragment<FragmentHomeBinding, MainViewModel>() {

    private val endlessScroll = object : EndlessScrollListener() {
        override fun onLoadMore() = viewModel.getUsers()
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
                    if (firstVisibleItem == 0) binding.fabScroll.hide()
                    else binding.fabScroll.show()
                    super.onScrolled(recyclerView, dx, dy)
                }
            })
            swipeLayout.setOnRefreshListener {
                swipeLayout.isRefreshing = false
                clearUserList()
                endlessScroll.resetData()
                getUsers()
            }
        }
        getUsers()
    }

    override fun observeLiveData() {
        viewModel.userList.observe(viewLifecycleOwner, {
            usersAdapter.submitList(it)
        })
        viewModel.goToTop.observe(viewLifecycleOwner, EventObserver {
            binding.rvUsers.smoothScrollToPosition(0)
            binding.appBar.setExpanded(true, true)
        })
    }

    private fun onItemClick(position: Int) = Unit

}
