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
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.util.*
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    lateinit var db: SQLiteDatabase
    var testMode = 0
    var running = true
    var auto = true
        set(value) {
            mediaPlayer.stop()
            runOnUiThread {
                mainTitle.text =
                    if (value) {
                        subject = -1
                        "自动模式\n" +
                                "铃声已经启动\n" +
                                "请勿锁定手机屏幕\n"
                    } else {
                        st_time = System.currentTimeMillis() / 1000 + 3
                        st_time += when (testMode){
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
                        Toast.makeText(this, "考试已经开始，考试过程中请勿中途修改系统时间", Toast.LENGTH_SHORT).show()
                        when (subject) {
                            0 -> "语文/综合\n" +
                                    "铃声已经启动\n" +
                                    "请勿锁定手机屏幕\n"
                            1 -> "数学/外语\n" +
                                    "铃声已经启动\n" +
                                    "请勿锁定手机屏幕\n"
                            else -> ""
                        }
                    }
            }
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
    var last_ms = System.currentTimeMillis()
    var st_time = System.currentTimeMillis() / 1000
    var pause = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
                db.execSQL("insert into mainTable(item, content) values('0','-1')")//手动开始时间
                db.execSQL("insert into mainTable(item, content) values('1','-1')")//手动科目
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
                    val now_s = (System.currentTimeMillis() / 1000).toInt() - st_time.toInt()
                    when (subject) {
                        0 -> {
                            for (it in Constant.bell_a) {
                                if (now_s == it.s && mediaPlayer.isPlaying.not()) {
                                    mediaPlayer = MediaPlayer.create(this, it.raw)
                                    mediaPlayer.start()
                                    mediaPlayer.isLooping = false
                                }
                            }
                        }
                        1 -> {
                            for (it in Constant.bell_p) {
                                if (now_s == it.s && mediaPlayer.isPlaying.not()) {
                                    mediaPlayer = MediaPlayer.create(this, it.raw)
                                    mediaPlayer.start()
                                    mediaPlayer.isLooping = false
                                }
                            }
                        }
                    }
                }
                if (abs(last_ms - System.currentTimeMillis()) >= 1000) {
                    mediaPlayer.stop()
                    AlertDialog.Builder(this)
                        .setTitle("由于您中途修改了时间，手动模式结束")
                        .setPositiveButton("关闭", null)
                        .show()
                    auto = true
                }
                last_ms = System.currentTimeMillis()
            }
        }.start()
    }

    override fun onResume() {
        super.onResume()
        if (pause) {
            AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("手机息屏可能导致此软件被Android系统强制关闭。为了保证软件正常运行，请不要锁定屏幕或切换到其它APP。")
                .setPositiveButton("好的", null)
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
                        .setTitle("提示")
                        .setMessage("您正在尝试退出手动模式，是否保存已经计算的时间？")
                        .setPositiveButton("保存") { _, _ ->
                            auto = true
                        }
                        .setNegativeButton("不保存") { _, _ ->
                            db.execSQL("update mainTable set content = -1 where item = 0")
                            db.execSQL("update mainTable set content = -1 where item = 1")
                            auto = true
                        }
                        .setNeutralButton("取消", null)
                        .show()
                }
            }
            R.id.menu_manual -> {
                var index = 0
                fun chooseSubject() {

                    fun start() {
                        subject = index
                        auto = false
                        db.execSQL("update mainTable set content = ${st_time} where item = 0")
                        db.execSQL("update mainTable set content = ${subject} where item = 1")
                    }

                    AlertDialog.Builder(this)
                        .setTitle("请选择科目")
                        .setSingleChoiceItems(
                            arrayOf("语文/综合", "数学/外语"),
                            0,
                            DialogInterface.OnClickListener { _, which ->
                                index = which
                            })
                        .setPositiveButton(
                            "继续",
                            DialogInterface.OnClickListener { _, _ ->
                                AlertDialog.Builder(this)
                                    .setTitle("请选择开始时间")
                                    .setItems(
                                        arrayOf(
                                            "完整全真模拟",
                                            "从发答题卡开始-开考前15分钟",
                                            "从发试题卷开始-开考前5分钟",
                                            "立即开始"
                                        )
                                    ) { _, which ->
                                        testMode = which
                                        start()
                                    }
                                    .show()
                            })
                        .setNeutralButton("取消", null)
                        .show()
                }

                val cursor = db.query("mainTable", null, null, null, null, null, null)
                cursor.moveToFirst()
                do {
                    if (cursor.getInt(1) == 0) {
                        if (cursor.getString(2).toLong() >= 0) {
                            AlertDialog.Builder(this)
                                .setTitle("提示")
                                .setMessage("检测到您有已保存或没有正常退出的手动模式，是否继续？")
                                .setPositiveButton("继续") { _, _ ->
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
                                    st_time = time
                                }
                                .setNegativeButton("重新开始") { _, _ ->
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
                    .setTitle("请选择音频来源")
                    .setItems(arrayOf("2018年四川高考原声", "2020年四川高考原声（默认）")) { _, which ->
                        fun setSource(){
                            Constant.setSource(which)
                            mediaPlayer.stop()
                            auto = true
                        }
                        if (auto) setSource()
                        else {
                            AlertDialog.Builder(this)
                                .setTitle("警告")
                                .setMessage("修改音频来源将结束手动模式，你是否要修改？")
                                .setPositiveButton("修改") { _, _ ->
                                    setSource()
                                }
                                .setNegativeButton("取消", null)
                                .show()
                        }
                    }
                    .show()
            }
            R.id.menu_about -> {
                AlertDialog.Builder(this)
                    .setTitle("关于APP")
                    .setMessage(
                        "[软件版本] 2.0\n" +
                                "[版权声明] 四川省成都市石室中学CTY制作\n" +
                                "[夹带私货] 欢迎报考华南理工大学！\n\n" +
                                "[更新记录]\n" +
                                "2.0 - 2020年9月28日\n添加2020年四川高考语音包\n" +
                                "1.0 - 2020年7月13日\n基于2018年四川高考语音包的首个版本"
                    )
                    .show()
            }
            R.id.menu_close -> {
                if (auto.not()) {
                    AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("您正在尝试退出手动模式，是否保存已经计算的时间？")
                        .setPositiveButton("保存") { _, _ ->
                            finish()
                        }
                        .setNegativeButton("不保存") { _, _ ->
                            db.execSQL("update mainTable set content = -1 where item = 0")
                            db.execSQL("update mainTable set content = -1 where item = 1")
                            finish()
                        }
                        .show()
                } else finish()
            }
        }
        return super.onOptionsItemSelected(item)
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
            0 -> "[音频来源]2018年四川高考\n"
            1 -> "[音频来源]2020年四川高考\n"
            else -> ""
        }
        val text = audioSource + "${year}年${month}月${day}日 $hour:$m:$s \n" + if (auto) {
            var next = ""
            for (it in Constant.bell) {
                if (it.h > hour || (it.h == hour && it.m > minute)) {
                    next = "下一次播报的铃声是\n${it.title}"
                    break
                }
            }
            if (hour >= 17) next = "最后一堂考试已经结束\n可以使用手动模式模拟考试"
            next
        } else {
            val now_s = (System.currentTimeMillis() / 1000).toInt() - st_time.toInt()
            val s2 = abs(now_s) % 60
            val m2 = (abs(now_s) - s2) / 60
            val s3 = if (s2 >= 10) s2.toString() else "0$s2"
            val m3 = if (m2 >= 10) m2.toString() else "0$m2"
            var next =
                if (now_s >= 0) "已经开考 ${m3}分钟${s3}秒 \n"
                else "距离开考 ${m3}分钟${s3}秒 \n"
            when (subject) {
                0 -> {
                    if (now_s >= Constant.bell_a[8].s) {
                        db.execSQL("update mainTable set content = -1 where item = 0")
                        db.execSQL("update mainTable set content = -1 where item = 1")
                        next = "考试结束！"
                    } else for (it in Constant.bell_a) {
                        if (it.s > now_s) {
                            next += "下一次播报的铃声是\n${it.title}"
                            break
                        }
                    }
                }
                1 -> {
                    if (now_s >= Constant.bell_p[8].s) {
                        db.execSQL("update mainTable set content = -1 where item = 0")
                        db.execSQL("update mainTable set content = -1 where item = 1")
                        next = "考试结束！"
                    } else for (it in Constant.bell_p) {
                        if (it.s > now_s) {
                            next += "下一次播报的铃声是\n${it.title}"
                            break
                        }
                    }
                }
            }
            next
        }
        runOnUiThread {
            subTitle.text = text
        }
    }

}