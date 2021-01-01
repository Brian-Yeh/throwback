package com.audigint.throwback.ui

import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.viewModels
import com.audigint.throwback.R
import com.audigint.throwback.databinding.FragmentQueueListDialogBinding
import com.audigint.throwback.ui.models.QueueItem
import com.bumptech.glide.Glide
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
class QueueListDialogFragment :
    BottomSheetDialogFragment() {
    private val queueViewModel: QueueViewModel by viewModels()
    private lateinit var binding: FragmentQueueListDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQueueListDialogBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = queueViewModel
    }
}

@BindingAdapter("queueItems")
fun queueItems(recyclerView: RecyclerView, list: List<QueueItem>?) {
    list ?: return
    if (recyclerView.adapter == null) {
        recyclerView.adapter = QueueAdapter()
    }
    (recyclerView.adapter as QueueAdapter).apply {
        submitList(list)
    }
}

@BindingAdapter("imageUrl")
fun imageUrl(view: ImageView, url: String?) {
    url?.let {
        Glide.with(view).load(it).into(view)
    }
}