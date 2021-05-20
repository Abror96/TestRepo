package com.abbasov.microphone

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.*
import android.os.Build.VERSION.SDK_INT
import android.provider.Settings
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.abbasov.microphone.databinding.ActivityMainBinding
import com.google.gson.Gson
import com.masoudss.lib.SeekBarOnProgressChanged
import com.masoudss.lib.WaveformSeekBar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable.fromCallable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.*
import java.net.URL
import java.util.*
import java.util.concurrent.Callable


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private var intArr =
        intArrayOf(
            2,
            61,
            87,
            90,
            101,
            99,
            106,
            106,
            112,
            134,
            121,
            130,
            118,
            128,
            142,
            129,
            154,
            213,
            204,
            177,
            185,
            180,
            150,
            136,
            203,
            207,
            172,
            147,
            127,
            136,
            123,
            171,
            204,
            166,
            129,
            129,
            109,
            90,
            91,
            128,
            84,
            93,
            84,
            67,
            43,
            28,
            106,
            195,
            162,
            123,
            80,
            82,
            83,
            70,
            104,
            92,
            63,
            78,
            69,
            47,
            24,
            6,
            9,
            28,
            45,
            48,
            37,
            46,
            68,
            83,
            63,
            16,
            0,
            0,
            0,
            0,
            0,
            7,
            39,
            57,
            62,
            76,
            91,
            96,
            93,
            66,
            16,
            1,
            0,
            0,
            0,
            0,
            1,
            30,
            68,
            64,
            63,
            81,
            82,
            67,
            51,
            24,
            4,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            46,
            90,
            89,
            84,
            97,
            109,
            110,
            116,
            155,
            156,
            165,
            178,
            159,
            153,
            158,
            171,
            235,
            211,
            207,
            197,
            173,
            145,
            133,
            362,
            242,
            176,
            154,
            129,
            103,
            105,
            151,
            200,
            245,
            246,
            198,
            140,
            106,
            95,
            164,
            167,
            97,
            77,
            56,
            47,
            29,
            23,
            150,
            140,
            131,
            118,
            111,
            109,
            77,
            75,
            115,
            71,
            58,
            37,
            37,
            27,
            10,
            7,
            45,
            70,
            65,
            70,
            71,
            73,
            80,
            72,
            24,
            1,
            0,
            0,
            0,
            0,
            2,
            35,
            74,
            54,
            53,
            83,
            90,
            81,
            67,
            34,
            6,
            0,
            0,
            0,
            0,
            0,
            13,
            35,
            46,
            56,
            55,
            48,
            70,
            83,
            58,
            14,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            15,
            92,
            129,
            121,
            117,
            124,
            129,
            111,
            116,
            165,
            141,
            133,
            130,
            123,
            128,
            126,
            173,
            201,
            182,
            152,
            153,
            144,
            125,
            199,
            168,
            135,
            153,
            147,
            125,
            106,
            115,
            190,
            192,
            161,
            117,
            106,
            110,
            100,
            155,
            311,
            307,
            235,
            167,
            99,
            48,
            25,
            117,
            135,
            120,
            110,
            100,
            90,
            73,
            66,
            188,
            151,
            101,
            45,
            30,
            16,
            7,
            2,
            23,
            47,
            55,
            62,
            76,
            86,
            90,
            83,
            34,
            4,
            0,
            0,
            0,
            0,
            0,
            15,
            60,
            77,
            62,
            72,
            86,
            78,
            60,
            35,
            9,
            0,
            0,
            0,
            0,
            0,
            10,
            44,
            69,
            66,
            63,
            70,
            75,
            80,
            68,
            22,
            1,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0
        )

    private var player: MediaPlayer? = null
    private var isPlaying = false
    private var recorder: MediaRecorder? = null

    private var recordStart = 0L
    private var recordDuration = 0L
    private lateinit var tempRecordedFile: File

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handler = Handler(Looper.getMainLooper())
        WaveformOptions.init(this)

        binding.waveformSeekBar.sample = intArr
        binding.waveformSeekBar.isVisible = true

        if (!checkStoragePermission()) {
            requestPermission()
        }

        binding.playPause.setOnClickListener {
            if (!checkStoragePermission()) {
                requestPermission()
            } else {
                isPlaying = if (isPlaying) {
                    player?.pause()
                    binding.playPause.setImageResource(R.drawable.ic_frame__11_)
                    false
                } else {
                    if (player == null) {
                        sendBackgroundRequest()
                    } else {
                        play()
                    }
                    true
                }
            }
        }

        binding.record.setOnTouchListener { view, motionEvent ->
            if (checkAudioRecordPermission()) {
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        startRecording()
                        binding.record.setColorFilter(binding.record.context.getColor(R.color.red))
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        stopRecording()
                        binding.record.setColorFilter(binding.record.context.getColor(R.color.white))
                        true
                    }
                    else -> true
                }
            } else {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(RECORD_AUDIO),
                    2297
                )
                return@setOnTouchListener true
            }
        }

        startActivity(Intent(this, MainActivity2::class.java))
    }

    private fun sendBackgroundRequest() {
        binding.playPause.isVisible = false
        binding.progress.isVisible = true
        Single.fromCallable {
            downloadUrl(URL("https://getsamplefiles.com/storage/m4a/default.m4a"))
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    binding.progress.isVisible = false
                    binding.playPause.isVisible = true

                    player = MediaPlayer()
                    var tempFile: File? = null
                    if (it != null) {
                        tempFile = File.createTempFile("downloaded", "file", null)
                        val fos = FileOutputStream(tempFile)
                        fos.write(it)

                        WaveformOptions.getSampleFrom(tempFile) {
                            Log.d("LOGGERR", "onPostExecute: ${Gson().toJson(it)}")
                            binding.waveformSeekBar.sample = it
                        }
                    }

                    binding.waveformSeekBar.onProgressChanged = object : SeekBarOnProgressChanged {
                        override fun onProgressChanged(
                            waveformSeekBar: WaveformSeekBar,
                            progress: Int,
                            fromUser: Boolean
                        ) {
                            if (fromUser) {
                                player?.seekTo(progress)
                            }
                            val remainingTime: Long =
                                (((player?.duration ?: 0)) - progress).toLong()
                            val seconds = (remainingTime / 1000) % 60
                            val minutes = (remainingTime / (1000 * 60) % 60)
                            val minStr = if ("$minutes".length == 1) "0$minutes" else minutes
                            val secStr = if ("$seconds".length == 1) "0$seconds" else seconds
                            val time = "$minStr:$secStr"

                            binding.remainingTime.text = time
                        }
                    }

                    stopPlaying()

                    player?.apply {
                        try {
                            setDataSource(tempFile?.path)
                            prepareAsync()
                        } catch (e: IOException) {
                            println("ChatFragment.startPlaying:prepare failed")
                        }

                        setOnPreparedListener {
                            play()
                        }

                        setOnCompletionListener {
                            binding.playPause.setImageResource(R.drawable.ic_frame__11_)
                            binding.waveformSeekBar.progress = 0
                            handler.removeCallbacks(runnable)
                        }
                    }
                },
                {
                    Log.d("LOGGERR", "Something went wrong: ${it.localizedMessage}")
                }
            )
    }

    private fun stopPlaying() {
        player?.reset()
    }

    private fun play() {
        binding.playPause.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24)
        player?.start()
        playCycle()
        binding.waveformSeekBar.maxProgress = player?.duration?.toFloat() ?: 0f
    }

    private fun playCycle() {
        binding.waveformSeekBar.progress = player?.currentPosition ?: 0

        if (player?.isPlaying == true) {
            runnable = Runnable { playCycle() }
            handler.postDelayed(runnable, 100)
        }
    }

    private fun downloadUrl(toDownload: URL): ByteArray {
        val outputStream = ByteArrayOutputStream()
        try {
            val chunk = ByteArray(4096)
            var bytesRead: Int
            val stream: InputStream = toDownload.openStream()
            while (stream.read(chunk).also { bytesRead = it } > 0) {
                outputStream.write(chunk, 0, bytesRead)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return outputStream.toByteArray()
    }


    private fun startRecording() {

        //name of the file where record will be stored
        tempRecordedFile =
            File.createTempFile("recorded${System.currentTimeMillis()}", "file.m4a", null)

        Log.d("LOGGERR", "startRecording: ${tempRecordedFile.path}")

        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setOutputFile(tempRecordedFile.path)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            try {
                prepare()

            } catch (e: IOException) {
                println("ChatFragment.startRecording${e.message}")
            }

            start()
            recordStart = Date().time
        }
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
            recorder = null
        }
        recordDuration = Date().time - recordStart

        if (recordDuration > 1000) {
            player = MediaPlayer()
            Log.d("LOGGERR", "stopRecording: ${tempRecordedFile.path}")

//            WaveformOptions.getSampleFrom(tempRecordedFile) {
//                Log.d("LOGGERR", "onPostExecute: ${Gson().toJson(it)}")
//                binding.waveformSeekBar.sample = it
//            }

            binding.waveformSeekBar.setSampleFrom(tempRecordedFile)

            binding.waveformSeekBar.onProgressChanged = object : SeekBarOnProgressChanged {
                override fun onProgressChanged(
                    waveformSeekBar: WaveformSeekBar,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        player?.seekTo(progress)
                    }
                    val remainingTime: Long = (((player?.duration ?: 0)) - progress).toLong()
                    val seconds = (remainingTime / 1000) % 60
                    val minutes = (remainingTime / (1000 * 60) % 60)
                    val minStr = if ("$minutes".length == 1) "0$minutes" else minutes
                    val secStr = if ("$seconds".length == 1) "0$seconds" else seconds
                    val time = "$minStr:$secStr"

                    binding.remainingTime.text = time
                }
            }

            stopPlaying()

            player?.apply {
                try {
                    setDataSource(tempRecordedFile.path)
                    prepareAsync()
                } catch (e: IOException) {
                    println("ChatFragment.startPlaying:prepare failed")
                }

                setOnPreparedListener {
                    play()
                }

                setOnCompletionListener {
                    binding.playPause.setImageResource(R.drawable.ic_frame__11_)
                    binding.waveformSeekBar.progress = 0
                    handler.removeCallbacks(runnable)
                }
            }
        } else {
            Toast.makeText(
                applicationContext,
                "Слишком короткое аудиосообщение",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun checkStoragePermission(): Boolean {
        return if (SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val result =
                ContextCompat.checkSelfPermission(this@MainActivity, READ_EXTERNAL_STORAGE)
            val result1 =
                ContextCompat.checkSelfPermission(this@MainActivity, WRITE_EXTERNAL_STORAGE)
            result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun checkAudioRecordPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(this, RECORD_AUDIO)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse(
                    String.format(
                        "package:%s",
                        applicationContext.packageName
                    )
                )
                startActivityForResult(intent, 2296)
            } catch (e: Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                startActivityForResult(intent, 2296)
            }
        } else {
            //below android 11
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE),
                2296
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2296) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    Log.d("LOGGERR", "storage granted: ")
                } else {
                    Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            2296 ->
                if (grantResults.isNotEmpty()) {
                    val readExternalStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val writeExternalStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED
                    if (readExternalStorage && writeExternalStorage) {
                        Log.d("LOGGERR", "storage granted: ")
                    } else {
                        Toast.makeText(
                            this,
                            "Allow permission for storage access!",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            2297 ->
                if (grantResults.isNotEmpty()) {
                    val recordAudio = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    if (recordAudio) {
                        Log.d("LOGGERR", "audio record granted: ")
                    } else {
                        Toast.makeText(
                            this,
                            "Allow permission for record audio!",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
        }
    }
}