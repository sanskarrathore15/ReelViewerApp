package com.example.reels

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.reels.adapter.VideoAdapter
import com.example.reels.model.VideoModel
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    lateinit var viewPager2: ViewPager2
    lateinit var adapter: VideoAdapter
    private var isDownloaded: Long = 0
    private val REQUEST_WRITE_STORAGE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set full screen
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        // Initialize ViewPager2
        viewPager2 = findViewById(R.id.view_pager)

        // Set up Firebase database reference
        val dataBase = Firebase.database.getReference("videos")
        val options = FirebaseRecyclerOptions.Builder<VideoModel>()
            .setQuery(dataBase, VideoModel::class.java)
            .build()

        // Initialize adapter
        adapter = VideoAdapter(options)
        viewPager2.adapter = adapter

        // If this is the first launch, add the ReelFragment
        // Fragment setup
        val fragment = ReelFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.view_pager, fragment) // Ensure this container has the correct ID
            .commit()
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    private fun checkStoragePermission(videoModel: VideoModel) {  // Pass the video model here
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_WRITE_STORAGE
            )
        } else {
            // Permission granted, start download
            startDownload(videoModel)  // Pass the video model to startDownload
        }
    }

    private fun startDownload(obj: VideoModel) {
        val request = DownloadManager.Request(Uri.parse(obj.url))
            .setTitle("${obj.title}.mp4")
            .setDescription("${obj.title} is downloading...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "${obj.title}.mp4")

        val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        isDownloaded = dm.enqueue(request)
    }

    // Handle the permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_WRITE_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // You might want to handle starting download again if you have a reference to the VideoModel
            } else {
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }
}
