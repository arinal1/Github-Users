package com.arinal.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.arinal.R
import com.arinal.data.model.UsersModel
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.item_users.view.*

class UsersAdapter(
    private val layoutInflater: LayoutInflater,
    private val glide: RequestManager,
    private val onClickListener: (Int) -> Unit
) : ListAdapter<UsersModel, UsersAdapter.ViewHolder>(object : DiffUtil.ItemCallback<UsersModel>() {
    override fun areItemsTheSame(oldItem: UsersModel, newItem: UsersModel) = oldItem.login == newItem.login
    override fun areContentsTheSame(oldItem: UsersModel, newItem: UsersModel) = oldItem.login == newItem.login
}) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(position)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(layoutInflater.inflate(R.layout.item_users, parent, false))
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(position: Int) {
            val user = getItem(position)
            glide.load(user.getSmallAvatar()).into(view.iv_avatar)
            view.tv_user.text = user.login
            view.setOnClickListener { onClickListener(position) }
        }
    }

}
