package com.shishi.nemt_bell

import android.content.DialogInterface
import android.database.sqlite.SQLiteDatabase
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.shishi.nemt_bell.databinding.ActivityMainBinding
import java.io.File
import java.util.*
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainViewModel
    lateinit var dataBinding: ActivityMainBinding

    lateinit var db: SQLiteDatabase
    var testMode = 0
    var running = true
    private var auto = true
        set(value) {
            onAutoSet(value)
            field = value
        }
    var subject = -1
    var calendar: Calendar = Calendar.getInstance()
    var year: Int = calendar.get(Calendar.YEAR)
    var month: Int = calendar.get(Calendar.MONTH) + 1
    var day: Int = calendar.get(Calendar.DAY_OF_MONTH)
    var hour: Int = calendar.get(Calendar.HOUR_OF_DAY)
    var minute: Int = calendar.get(Calendar.MINUTE)
    var second: Int = calendar.get(Calendar.SECOND)
    lateinit var mediaPlayer: MediaPlayer
    private var lastMs = System.currentTimeMillis()
    var stTime = System.currentTimeMillis() / 1000
    var pause = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        dataBinding.data = viewModel
        dataBinding.lifecycleOwner = this

        if (File(applicationContext.getExternalFilesDir("")?.absolutePath + "/NEMT_Bell.db").exists()
                .not()
        ) {
            db = SQLiteDatabase.openOrCreateDatabase(
                applicationContext.getExternalFilesDir("")?.absolutePath + "/NEMT_Bell.db",
                null
            )
            db.execSQL("create table mainTable(_id integer primary key autoincrement, item integer, content text)")
            val cursor = db.query("mainTable", null, null, null, null, null, null)
            if (cursor.moveToFirst().not()) {
                db.execSQL("insert into mainTable(item, content) values('0','-1')")//??????????????????
                db.execSQL("insert into mainTable(item, content) values('1','-1')")//????????????
            }
            cursor.close()
        } else db = SQLiteDatabase.openOrCreateDatabase(
            applicationContext.getExternalFilesDir("")?.absolutePath + "/NEMT_Bell.db",
            null
        )

        mediaPlayer = MediaPlayer.create(this, Constant.p1a.raw)
        Thread {
            while (running) {
                Thread.sleep(100)
                updateSub()
                if (auto) {
                    for (it in Constant.bell) {
                        if (it.h == hour && it.m == minute && it.s == second && mediaPlayer.isPlaying.not()) {
                            mediaPlayer = MediaPlayer.create(this, it.raw)
                            mediaPlayer.start()
                            mediaPlayer.isLooping = false
                        }
                    }
                } else {
                    val nowS = (System.currentTimeMillis() / 1000).toInt() - stTime.toInt()
                    when (subject) {
                        0 -> {
                            for (it in Constant.bell_a) {
                                if (nowS == it.s && mediaPlayer.isPlaying.not()) {
                                    mediaPlayer = MediaPlayer.create(this, it.raw)
                                    mediaPlayer.start()
                                    mediaPlayer.isLooping = false
                                }
                            }
                        }
                        1 -> {
                            for (it in Constant.bell_p) {
                                if (nowS == it.s && mediaPlayer.isPlaying.not()) {
                                    mediaPlayer = MediaPlayer.create(this, it.raw)
                                    mediaPlayer.start()
                                    mediaPlayer.isLooping = false
                                }
                            }
                        }
                    }
                }
                if (abs(lastMs - System.currentTimeMillis()) >= 1000) {
                    mediaPlayer.stop()
                    AlertDialog.Builder(this)
                        .setTitle("???????????????????????????????????????????????????")
                        .setPositiveButton("??????", null)
                        .show()
                    auto = true
                }
                lastMs = System.currentTimeMillis()
            }
        }.start()
    }

    override fun onResume() {
        super.onResume()
        if (pause) {
            AlertDialog.Builder(this)
                .setTitle("??????")
                .setMessage("????????????????????????????????????Android?????????????????????????????????????????????????????????????????????????????????????????????APP???")
                .setPositiveButton("??????", null)
                .show()
        }
        if (pause.not()) pause = true
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        running = false
        db.close()
        Log.d("??????", "????????????")
    }

    override fun onBackPressed() {
        fun finishAlertDialog() {
            AlertDialog.Builder(this)
                .setTitle("??????")
                .setMessage("???????????????????????????????????????????????????")
                .setPositiveButton("???") { _, _ ->
                    finish()
                }
                .setNegativeButton("???", null)
                .show()
        }
        if (auto.not()) {
            AlertDialog.Builder(this)
                .setTitle("??????")
                .setMessage("????????????????????????????????????????????????????????????????????????")
                .setPositiveButton("??????") { _, _ ->
                    auto = true
                    finishAlertDialog()
                }
                .setNegativeButton("?????????") { _, _ ->
                    db.execSQL("update mainTable set content = -1 where item = 0")
                    db.execSQL("update mainTable set content = -1 where item = 1")
                    auto = true
                    finishAlertDialog()
                }
                .setNeutralButton("??????", null)
                .show()
        } else finishAlertDialog()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_auto -> {
                if (auto.not()) {
                    AlertDialog.Builder(this)
                        .setTitle("??????")
                        .setMessage("????????????????????????????????????????????????????????????????????????")
                        .setPositiveButton("??????") { _, _ ->
                            auto = true
                        }
                        .setNegativeButton("?????????") { _, _ ->
                            db.execSQL("update mainTable set content = -1 where item = 0")
                            db.execSQL("update mainTable set content = -1 where item = 1")
                            auto = true
                        }
                        .setNeutralButton("??????", null)
                        .show()
                }
            }
            R.id.menu_manual -> {
                var index = 0
                fun chooseSubject() {

                    fun start() {
                        subject = index
                        auto = false
                        db.execSQL("update mainTable set content = ${stTime} where item = 0")
                        db.execSQL("update mainTable set content = ${subject} where item = 1")
                    }

                    AlertDialog.Builder(this)
                        .setTitle("???????????????")
                        .setSingleChoiceItems(
                            arrayOf("??????/??????", "??????/??????"),
                            0,
                            DialogInterface.OnClickListener { _, which ->
                                index = which
                            })
                        .setPositiveButton(
                            "??????",
                            DialogInterface.OnClickListener { _, _ ->
                                AlertDialog.Builder(this)
                                    .setTitle("?????????????????????")
                                    .setItems(
                                        arrayOf(
                                            "??????????????????",
                                            "?????????????????????-?????????15??????",
                                            "?????????????????????-?????????5??????",
                                            "????????????"
                                        )
                                    ) { _, which ->
                                        testMode = which
                                        start()
                                    }
                                    .show()
                            })
                        .setNeutralButton("??????", null)
                        .show()
                }

                val cursor = db.query("mainTable", null, null, null, null, null, null)
                cursor.moveToFirst()
                do {
                    if (cursor.getInt(1) == 0) {
                        if (cursor.getString(2).toLong() >= 0) {
                            AlertDialog.Builder(this)
                                .setTitle("??????")
                                .setMessage("??????????????????????????????????????????????????????????????????????????????")
                                .setPositiveButton("??????") { _, _ ->
                                    cursor.moveToFirst()
                                    var time = System.currentTimeMillis()
                                    do {
                                        if (cursor.getInt(1) == 0)
                                            time = cursor.getString(2).toLong()
                                        if (cursor.getInt(1) == 1)
                                            subject = cursor.getString(2).toInt()
                                    } while (cursor.moveToNext())
                                    cursor.close()
                                    auto = false
                                    stTime = time
                                }
                                .setNegativeButton("????????????") { _, _ ->
                                    chooseSubject()
                                    cursor.close()
                                }
                                .show()
                            break
                        } else {
                            chooseSubject()
                            break
                        }
                    }
                } while (cursor.moveToNext())
            }
            R.id.menu_audio -> {
                AlertDialog.Builder(this)
                    .setTitle("?????????????????????")
                    .setItems(arrayOf("2018????????????????????????", "2020????????????????????????????????????")) { _, which ->
                        fun setSource() {
                            Constant.setSource(which)
                            mediaPlayer.stop()
                            auto = true
                        }
                        if (auto) setSource()
                        else {
                            AlertDialog.Builder(this)
                                .setTitle("??????")
                                .setMessage("???????????????????????????????????????????????????????????????")
                                .setPositiveButton("??????") { _, _ ->
                                    setSource()
                                }
                                .setNegativeButton("??????", null)
                                .show()
                        }
                    }
                    .show()
            }
            R.id.menu_about -> {
                AlertDialog.Builder(this)
                    .setTitle("??????APP")
                    .setMessage(
                        "[????????????] ${BuildConfig.VERSION_NAME}\n" +
                                "[????????????] ?????????????????? ???????????? Summer-lights"
//                        "[????????????] 2.1\n" +
//                                "[????????????] ?????????????????? ???????????? Summer-lights\n\n" +
//                                "[????????????]\n" +
//                                "2.1 - 2021???5???2???\n??????Android-Jetpack????????????\n" +
//                                "2.0 - 2020???9???28???\n??????2020????????????????????????\n" +
//                                "1.0 - 2020???7???13???\n??????2018???????????????????????????????????????"
                    )
                    .show()
            }
            R.id.menu_close -> {
                if (auto.not()) {
                    AlertDialog.Builder(this)
                        .setTitle("??????")
                        .setMessage("????????????????????????????????????????????????????????????????????????")
                        .setPositiveButton("??????") { _, _ ->
                            finish()
                        }
                        .setNegativeButton("?????????") { _, _ ->
                            db.execSQL("update mainTable set content = -1 where item = 0")
                            db.execSQL("update mainTable set content = -1 where item = 1")
                            finish()
                        }
                        .setNeutralButton("??????", null)
                        .show()
                } else finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onAutoSet(value: Boolean) {
        mediaPlayer.stop()
        viewModel.mainTitle.postValue(
            if (value) {
                subject = -1
                "????????????\n" +
                        "??????????????????\n" +
                        "????????????????????????\n"
            } else {
                stTime = System.currentTimeMillis() / 1000 + 3
                stTime += when (testMode) {
                    0 -> when (Constant.sourceIndex) {
                        0 -> 45 * 60
                        1 -> 50 * 60
                        else -> 0
                    }
                    1 -> 15 * 60
                    2 -> 5 * 60
                    3 -> 0
                    else -> 0
                }
                Toast.makeText(this, "??????????????????????????????????????????????????????????????????", Toast.LENGTH_SHORT).show()
                when (subject) {
                    0 -> "??????/??????\n" +
                            "??????????????????\n" +
                            "????????????????????????\n"
                    1 -> "??????/??????\n" +
                            "??????????????????\n" +
                            "????????????????????????\n"
                    else -> ""
                }
            }
        )
    }

    private fun updateSub() {
        calendar = Calendar.getInstance()
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH) + 1
        day = calendar.get(Calendar.DAY_OF_MONTH)
        hour = calendar.get(Calendar.HOUR_OF_DAY)
        minute = calendar.get(Calendar.MINUTE)
        second = calendar.get(Calendar.SECOND)
        val m = if (minute >= 10) minute.toString() else "0$minute"
        val s = if (second >= 10) second.toString() else "0$second"
        val audioSource = when (Constant.sourceIndex) {
            0 -> "[????????????] 2018??????????????????\n"
            1 -> "[????????????] 2020??????????????????\n"
            else -> ""
        }
        val text = audioSource + "${year}???${month}???${day}??? $hour:$m:$s \n" + if (auto) {
            var next = ""
            for (it in Constant.bell) {
                if (it.h > hour || (it.h == hour && it.m > minute)) {
                    next = "???????????????????????????\n${it.title}"
                    break
                }
            }
            if (hour >= 17) next = "??????????????????????????????\n????????????????????????????????????"
            next
        } else {
            val nowS = (System.currentTimeMillis() / 1000).toInt() - stTime.toInt()
            val s2 = abs(nowS) % 60
            val m2 = (abs(nowS) - s2) / 60
            val s3 = if (s2 >= 10) s2.toString() else "0$s2"
            val m3 = if (m2 >= 10) m2.toString() else "0$m2"
            var next =
                if (nowS >= 0) "???????????? ${m3}??????${s3}??? \n"
                else "???????????? ${m3}??????${s3}??? \n"
            when (subject) {
                0 -> {
                    if (nowS >= Constant.bell_a[8].s) {
                        db.execSQL("update mainTable set content = -1 where item = 0")
                        db.execSQL("update mainTable set content = -1 where item = 1")
                        next = "???????????????"
                    } else for (it in Constant.bell_a) {
                        if (it.s > nowS) {
                            next += "???????????????????????????\n${it.title}"
                            break
                        }
                    }
                }
                1 -> {
                    if (nowS >= Constant.bell_p[8].s) {
                        db.execSQL("update mainTable set content = -1 where item = 0")
                        db.execSQL("update mainTable set content = -1 where item = 1")
                        next = "???????????????"
                    } else for (it in Constant.bell_p) {
                        if (it.s > nowS) {
                            next += "???????????????????????????\n${it.title}"
                            break
                        }
                    }
                }
            }
            next
        }
        viewModel.subTitle.postValue(text)
    }

}