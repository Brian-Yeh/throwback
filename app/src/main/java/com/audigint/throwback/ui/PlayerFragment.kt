package com.audigint.throwback.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.audigint.throwback.R
import com.audigint.throwback.databinding.FragmentPlayerBinding
import com.audigint.throwback.utill.EventObserver
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
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = playerViewModel

        with (playerViewModel) {
            showQueue.observe(viewLifecycleOwner, EventObserver {
                activity?.let {
                    val mainActivity = activity as MainActivity
                    mainActivity.showQueue()
                }
            })

            currentSongTitle.observe(viewLifecycleOwner, { title ->
                binding.songTitle.text = title
            })

            currentSongArtist.observe(viewLifecycleOwner, { artist ->
                binding.songArtist.text = artist
            })

            currentQueueData.observe(viewLifecycleOwner) {
                currentQueue = it//.shuffled()
            }

            isPlaying.observe(viewLifecycleOwner) { playing ->
                playBtn.setImageResource(if (playing) R.drawable.ic_pause_btn else R.drawable.ic_play_arrow_btn)
            }
        }
    }
}