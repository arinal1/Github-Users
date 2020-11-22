package com.arinal.utils

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton

@BindingAdapter("bindLoading")
fun ShimmerFrameLayout.bindLoading(isLoading: Boolean) {
    visibility = if (isLoading) View.VISIBLE else View.GONE
    if (isLoading) startShimmer() else stopShimmer()
}

@BindingAdapter("bindIsEnabled")
fun SwipeRefreshLayout.bindIsEnabled(isEnabled: Boolean) = setEnabled(isEnabled)

@BindingAdapter("bindShowing")
fun FloatingActionButton.bindShowing(show: Boolean) = if (show) show() else hide()
