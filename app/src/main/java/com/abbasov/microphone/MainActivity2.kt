package com.abbasov.microphone

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.abbasov.microphone.databinding.ActivityMain2Binding
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSourceFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit


class MainActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityMain2Binding
    private lateinit var simpleExoPlayer: SimpleExoPlayer
    private lateinit var mediaDataSourceFactory: DataSource.Factory
    private var position: Long = 0
    private val TAG = "LOGGERR"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun initializePlayer() {

        mediaDataSourceFactory = DefaultDataSourceFactory(
            this, Util.getUserAgent(
                this,
                "mediaPlayerSample"
            )
        )

        val mediaSource = ProgressiveMediaSource.Factory(mediaDataSourceFactory).createMediaSource(
            MediaItem.fromUri(STREAM_URL)
        )

        val mediaSourceFactory: MediaSourceFactory = DefaultMediaSourceFactory(
            mediaDataSourceFactory
        )

        simpleExoPlayer = SimpleExoPlayer.Builder(this)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()

        simpleExoPlayer.addMediaSource(mediaSource)

        simpleExoPlayer.playWhenReady = true

        binding.playerView.setShutterBackgroundColor(Color.TRANSPARENT)
        binding.playerView.player = simpleExoPlayer
        binding.playerView.requestFocus()
        binding.playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM

        var subscription: Disposable? = null
        simpleExoPlayer.addListener(object : Player.Listener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    binding.progressBar.isVisible = false
                    binding.timerText.isVisible = true
                    subscription = Observable.interval(0, 1000, TimeUnit.MILLISECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            if (simpleExoPlayer.duration > 0) {
                                val millis =
                                    simpleExoPlayer.duration - simpleExoPlayer.currentPosition
                                val minutes: Long = (millis / 1000) / 60
                                val seconds: Long = (millis / 1000) % 60
                                val minStr = if ("$minutes".length == 1) "0$minutes" else minutes
                                val secStr = if ("$seconds".length == 1) "0$seconds" else seconds
                                Log.d(TAG, "onPlayerStateChanged: $minStr:$secStr")

                                binding.timerText.text = "$minStr:$secStr"
                            }
                        }
                } else if (playbackState == Player.STATE_ENDED) {
                    subscription?.dispose()
                    initializePlayer()
                }
            }
        })

        binding.videoLayout.setOnClickListener {
            startActivity(Intent(this, VideoPlayerActivity::class.java))
        }

        simpleExoPlayer.volume = 0f
        simpleExoPlayer.prepare()
    }

    private fun releasePlayer() {
        simpleExoPlayer.release()
    }

    public override fun onResume() {
        super.onResume()
        initializePlayer()
    }

    public override fun onPause() {
        super.onPause()
        if (simpleExoPlayer.playWhenReady) {
            position = simpleExoPlayer.currentPosition;
            simpleExoPlayer.playWhenReady = false;
        }
    }

    public override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    companion object {
        const val STREAM_URL = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"
    }

    fun pxFromDp(dp: Float): Float {
        return dp * resources.displayMetrics.density
    }
}