package com.arinal.ui.detail

import android.content.Intent
import android.net.Uri
import com.arinal.R
import com.arinal.databinding.FragmentDetailBinding
import com.arinal.ui.MainViewModel
import com.arinal.ui.base.BaseFragment
import com.arinal.utils.EventObserver
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class DetailFragment : BaseFragment<FragmentDetailBinding, MainViewModel>() {

    override val viewModel: MainViewModel by sharedViewModel()

    override fun setLayout() = R.layout.fragment_detail

    override fun observeLiveData() {
        viewModel.goToBlog.observe(viewLifecycleOwner, EventObserver {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(viewModel.selectedUser.blog.value ?: "")
            })
        })
        viewModel.sendEmail.observe(viewLifecycleOwner, EventObserver {
            startActivity(
                Intent.createChooser(
                    Intent(
                        Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto",
                            viewModel.selectedUser.email.value ?: "",
                            null
                        )
                    ).apply {
                        putExtra(Intent.EXTRA_SUBJECT, "Subject")
                        putExtra(Intent.EXTRA_TEXT, "Body")
                    }, "Send email..."
                )
            )
        })
    }

    override fun initViews() {
        binding.toolbar.title = getString(R.string.user)
        binding.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
        binding.swipeLayout.setOnRefreshListener {
            binding.swipeLayout.isRefreshing = false
            viewModel.getUser()
        }
        if (viewModel.selectedUser.id.value == 0) viewModel.getUser()
    }

}
