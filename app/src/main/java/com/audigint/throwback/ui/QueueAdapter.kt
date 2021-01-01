package com.audigint.throwback.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.audigint.throwback.BR
import com.audigint.throwback.R
import com.audigint.throwback.ui.models.QueueItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QueueAdapter @Inject constructor() :
    ListAdapter<QueueItem, QueueItemViewHolder>(QueueItemComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QueueItemViewHolder {
        return QueueItemViewHolder(
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_queue_song, parent, false)
        )
    }

    override fun onBindViewHolder(holder: QueueItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class QueueItemViewHolder(
    private val binding: ViewDataBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(queueItem: QueueItem) {
        binding.setVariable(BR.queueItem, queueItem)
        binding.executePendingBindings()
    }
}

object QueueItemComparator : DiffUtil.ItemCallback<QueueItem>() {
    override fun areItemsTheSame(oldItem: QueueItem, newItem: QueueItem): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: QueueItem, newItem: QueueItem): Boolean {
        return oldItem.id == newItem.id
    }
}