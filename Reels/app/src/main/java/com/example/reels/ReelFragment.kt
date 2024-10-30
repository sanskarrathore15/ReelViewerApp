package com.example.reels

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.fragment.app.Fragment

class ReelFragment : Fragment() {
    private lateinit var videoView: VideoView
    private lateinit var videoUri: Uri
    private var mediaPlayer: MediaPlayer? = null // Declare mediaPlayer as a nullable variable

    class ReelFragment : Fragment() {

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.video_layout, container, false)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize VideoView
        videoView = view.findViewById(R.id.video_view)

        // Set video URI and prepare the video
        videoView.setVideoURI(videoUri)
        videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = true // Loop the video
            videoView.start() // Start playing the video
            this.mediaPlayer = mediaPlayer // Store the reference to mediaPlayer
        }
    }

    fun setVideoUri(uri: Uri) {
        videoUri = uri
    }

    override fun onStart() {
        super.onStart()
        Log.d("ReelFragment", "onStart called")
        // Start playback if video is loaded
        mediaPlayer?.start()
    }

    override fun onPause() {
        super.onPause()
        Log.d("ReelFragment", "onPause called")
        // Pause video playback
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d("ReelFragment", "onStop called")
        // Stop video playback
        mediaPlayer?.stop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("ReelFragment", "onDestroyView called")
        // Release media player resources
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Only save if videoUri is not null
        videoUri?.let { uri ->
            outState.putString("videoPath", uri.toString())
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        // Restore the video URI if available
        savedInstanceState?.let {
            val videoPath = it.getString("videoPath")
            videoPath?.let { path ->
                videoUri = Uri.parse(path)
            }
        }
    }
}
