package com.example.reels.adapter

import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.reels.MainActivity
import com.example.reels.R
import com.example.reels.model.VideoModel
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions


class VideoAdapter(options: FirebaseRecyclerOptions<VideoModel?>) :

    FirebaseRecyclerAdapter<VideoModel?, VideoAdapter.MyViewHolder?>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.video_layout,
            parent,
            false
        )
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int, model: VideoModel) {
        holder.setData(model,holder.itemView.context)
    }
    public val REQUEST_WRITE_STORAGE = 100

    //myviewholder
    inner class MyViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        lateinit var videoView: VideoView
        lateinit var title: TextView
        lateinit var desc: TextView
        lateinit var progressBar: ProgressBar
        lateinit var favourite: ImageView
        lateinit var download: ImageView
        lateinit var shareButton: ImageView
        var isFav = false
        var isDownloaded: Long = 0

        // Call setData to populate data for each item
        fun setData(obj: VideoModel, context: Context) {
            videoView.setVideoPath(obj.url)
            title.text = obj.title
            desc.text = obj.desc

            videoView.setOnPreparedListener { mediaPlayer ->
                progressBar.visibility = View.GONE
                mediaPlayer.start()
            }
            videoView.setOnCompletionListener { mediaPlayer -> mediaPlayer.start() }

            favourite.setOnClickListener {
                if (!isFav) {
                    favourite.setImageResource(R.drawable.fav_fill)
                    favourite.setColorFilter(ContextCompat.getColor(context, R.color.red))
                    isFav = true
                } else {
                    favourite.setImageResource(R.drawable.fav_border)
                    isFav = false
                }
            }

            // Share button setup in MyViewHolder
            shareButton.setOnClickListener {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "Check out this video!")
                    putExtra(Intent.EXTRA_TEXT, obj.url) // URL or file path of the video
                }
                itemView.context.startActivity(Intent.createChooser(shareIntent, "Share via"))
            }

            // Download button click listener
            download.setOnClickListener {
                checkStoragePermission(context, obj)
            }

            videoView.setOnCompletionListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION && position < itemCount - 1) {
                    // Move to the next reel
                    (itemView.context as MainActivity).viewPager2.currentItem = position + 1
                } else {
                    // If it's the last item, loop back to the first item
                    (itemView.context as MainActivity).viewPager2.currentItem = 0
                }
            }



            // BroadcastReceiver to show a toast after download is complete
            val br = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                    if (id == isDownloaded) {
                        Toast.makeText(itemView.context, "${obj.title} is downloaded", Toast.LENGTH_LONG).show()
                    }
                }
            }
            itemView.context.registerReceiver(br, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        }

        // Check and request storage permission
        private fun checkStoragePermission(context: Context, obj: VideoModel) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_WRITE_STORAGE
                )
            } else {
                // Permission granted, start download
                startDownload(context, obj)
            }
        }

        // Start the download process
        private fun startDownload(context: Context, obj: VideoModel) {
            val request = DownloadManager.Request(Uri.parse(obj.url))
                .setTitle("${obj.title}.mp4")
                .setDescription("${obj.title} is downloading...")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedOverMetered(true)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "${obj.title}.mp4")

            val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            isDownloaded = dm.enqueue(request)

            // Update the download icon after initiating download
            download.setImageResource(R.drawable.download_sucess)
            download.setColorFilter(ContextCompat.getColor(context, R.color.red))
        }

        init {
            videoView = v.findViewById(R.id.video_view)
            title = v.findViewById(R.id.video_title)
            desc = v.findViewById(R.id.video_description)
            progressBar = v.findViewById(R.id.video_progress_bar)
            favourite = v.findViewById(R.id.fav)
            download = v.findViewById(R.id.dowload)
            shareButton = v.findViewById(R.id.share_button)

        }
    }


}