package com.audigint.throwback.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.audigint.throwback.R
import com.audigint.throwback.databinding.FragmentPlayerBinding
import com.audigint.throwback.util.EventObserver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerFragment : Fragment() {

    companion object {
        fun newInstance() = PlayerFragment()
    }

    private val playerViewModel: PlayerViewModel by viewModels()
    private lateinit var binding: FragmentPlayerBinding
    private lateinit var playBtn: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayerBinding.inflate(layoutInflater, container, false)
        playBtn = binding.playBtn
        binding.songTitle.isSelected = true // Marquee
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = playerViewModel

        with (playerViewModel) {
            showQueue.observe(viewLifecycleOwner, EventObserver {
                activity?.let {
                    val mainActivity = activity as MainActivity
                    mainActivity.showQueue()
                }
            })

            trackTitle.observe(viewLifecycleOwner, { title ->
                binding.songTitle.text = title
            })

            trackArtist.observe(viewLifecycleOwner, { artist ->
                binding.songArtist.text = artist
            })

            trackArtwork.observe(viewLifecycleOwner, { bitmap ->
                binding.artwork.setImageBitmap(bitmap)
            })

            currentQueueData.observe(viewLifecycleOwner) {
                currentQueue = it//.shuffled()
            }

            isPlaying.observe(viewLifecycleOwner) { playing ->
                playBtn.setImageResource(if (playing) R.drawable.ic_pause_btn else R.drawable.ic_play_arrow_btn)
            }

            this.yearSpinner = binding.yearSpinner
            activity?.let {
                ArrayAdapter(it, R.layout.spinner_item, (1959..2016).toList()).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    this.yearSpinner.adapter = adapter
                    setInitialYear()
                }
            }
        }
    }
}