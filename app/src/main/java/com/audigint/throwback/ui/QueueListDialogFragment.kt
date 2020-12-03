package com.audigint.throwback.ui

import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.audigint.throwback.R
import com.audigint.throwback.data.Song
import com.audigint.throwback.databinding.FragmentQueueListDialogBinding
import dagger.hilt.android.AndroidEntryPoint


/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    QueueListDialogFragment.newInstance(30).show(supportFragmentManager, "dialo g")
 * </pre>
 */
@AndroidEntryPoint
class QueueListDialogFragment : BottomSheetDialogFragment() {
    private val queueViewModel: QueueViewModel by viewModels()
    private lateinit var queueRecyclerView: RecyclerView
    private lateinit var queueAdapter: QueueAdapter
    private lateinit var binding: FragmentQueueListDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQueueListDialogBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = queueViewModel
        }
        queueRecyclerView = binding.queue

        queueAdapter = QueueAdapter(queueViewModel)

        queueRecyclerView.apply {
            adapter = queueAdapter
        }
        initQueue()

        return binding.root
    }

    fun initQueue() {
        queueViewModel.currentQueue.observe(viewLifecycleOwner) { songs ->
            queueAdapter.submitList(songs)
        }
    }

    class SongViewHolder (
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.title)

        fun bind(text: String?) {
            title.text = text
        }

        companion object {
            fun create(parent: ViewGroup): SongViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_queue_song, parent, false)
                return SongViewHolder(view)
            }
        }
    }

    private inner class QueueAdapter(
        private val queueViewModel: QueueViewModel
    ) : ListAdapter<Song, SongViewHolder>(SongsComparator()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
            return SongViewHolder.create(parent)
        }

        override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
            val item = getItem(position)
            holder.bind(item.title)
        }
    }

    class SongsComparator : DiffUtil.ItemCallback<Song>() {
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.id == newItem.id
        }
    }
}