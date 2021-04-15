package com.shishi.nemt_bell

object Constant {

    val p1a = Bell(8, 15, 0, "08:15  考前45分钟", R.raw.audio_2018_p1)
    val p2a = Bell(8, 45, 0, "08:45  考前15分钟", R.raw.audio_2018_p2)
    val p3a = Bell(8, 49, 0, "08:49  考前11分钟", R.raw.audio_2018_p3_a)
    val p4a = Bell(8, 50, 0, "08:50  考前10分钟", R.raw.audio_2018_p4)
    val p5a = Bell(8, 55, 0, "08:55  考前5分钟", R.raw.audio_2018_p5)
    val p6a = Bell(9, 0, 0, "09:00  现在开始答题", R.raw.audio_2018_p6)
    val p7a = Bell(11, 0, 0, "11:00  离考试结束还有30分钟", R.raw.audio_2018_p7)
    val p8a = Bell(11, 15, 0, "11:15  离考试结束还有15分钟", R.raw.audio_2018_p8)
    val p9a = Bell(11, 30, 0, "11:30  考试结束", R.raw.audio_2018_p9)

    val p1p = Bell(14, 15, 0, "14:15  考前45分钟", R.raw.audio_2018_p1)
    val p2p = Bell(14, 45, 0, "14:45  考前15分钟", R.raw.audio_2018_p2)
    val p3p = Bell(14, 49, 0, "14:49  考前11分钟", R.raw.audio_2018_p3_p)
    val p4p = Bell(14, 50, 0, "14:50  考前10分钟", R.raw.audio_2018_p4)
    val p5p = Bell(14, 55, 0, "14:55  考前5分钟", R.raw.audio_2018_p5)
    val p6p = Bell(15, 0, 0, "15:00  现在开始答题", R.raw.audio_2018_p6)
    val p7p = Bell(16, 30, 0, "16:30  离考试结束还有30分钟", R.raw.audio_2018_p7)
    val p8p = Bell(16, 45, 0, "16:45  离考试结束还有15分钟", R.raw.audio_2018_p8)
    val p9p = Bell(17, 0, 0, "17:00  考试结束", R.raw.audio_2018_p9)


    val p1a_2020 = Bell(8, 10, 0, "08:10 考前50分钟", R.raw.audio_2020_p1)
    val p2a_2020 = Bell(8, 24, 0, "08:24 考前36分钟", R.raw.audio_2020_p2)
    val p3a_2020 = Bell(8, 45, 0, "08:45 考前15分钟", R.raw.audio_2020_p3)
    val p4a_2020 = Bell(8, 49, 0, "08:49 考前11分钟", R.raw.audio_2020_p4)
    val p5a_2020 = Bell(8, 50, 0, "08:50 考前10分钟", R.raw.audio_2020_p5)
    val p6a_2020 = Bell(8, 55, 0, "08:55 考前5分钟", R.raw.audio_2020_p6)
    val p7a_2020 = Bell(9, 0, 0, "09:00 现在开始答题", R.raw.audio_2020_p7)
    val p8a_2020 = Bell(11, 0, 0, "11:00 离考试结束还有30分钟", R.raw.audio_2020_p8)
    val p9a_2020 = Bell(11, 15, 0, "11:15 离考试结束还有15分钟", R.raw.audio_2020_p9)
    val p10a_2020 = Bell(11, 30, 0, "11:30 考试结束", R.raw.audio_2020_p10)

    val p1p_2020 = Bell(14, 10, 0, "14:10 考前50分钟", R.raw.audio_2020_p1)
    val p2p_2020 = Bell(14, 24, 0, "14:24 考前36分钟", R.raw.audio_2020_p2)
    val p3p_2020 = Bell(14, 45, 0, "14:45 考前15分钟", R.raw.audio_2020_p3)
    val p4p_2020 = Bell(14, 49, 0, "14:49 考前11分钟", R.raw.audio_2020_p4)
    val p5p_2020 = Bell(14, 50, 0, "14:50 考前10分钟", R.raw.audio_2020_p5)
    val p6p_2020 = Bell(14, 55, 0, "14:55 考前5分钟", R.raw.audio_2020_p6)
    val p7p_2020 = Bell(15, 0, 0, "15:00 现在开始答题", R.raw.audio_2020_p7)
    val p8p_2020 = Bell(16, 30, 0, "16:30 离考试结束还有30分钟", R.raw.audio_2020_p8)
    val p9p_2020 = Bell(16, 45, 0, "16:45 离考试结束还有15分钟", R.raw.audio_2020_p9)
    val p10p_2020 = Bell(17, 0, 0, "17:00 考试结束", R.raw.audio_2020_p10)

    val bell_2020 = arrayListOf(
        p1a_2020,
        p2a_2020,
        p3a_2020,
        p4a_2020,
        p5a_2020,
        p6a_2020,
        p7a_2020,
        p8a_2020,
        p9a_2020,
        p10a_2020,
        p1p_2020,
        p2p_2020,
        p3p_2020,
        p4p_2020,
        p5p_2020,
        p6p_2020,
        p7p_2020,
        p8p_2020,
        p9p_2020,
        p10p_2020
    )
    val bell_2020_a = ArrayList<Bell_M>()
    val bell_2020_p = ArrayList<Bell_M>()

    val bell_2018 = arrayListOf(
        p1a,
        p2a,
        p3a,
        p4a,
        p5a,
        p6a,
        p7a,
        p8a,
        p9a,
        p1p,
        p2p,
        p3p,
        p4p,
        p5p,
        p6p,
        p7p,
        p8p,
        p9p
    )
    val bell_2018_a = ArrayList<Bell_M>()
    val bell_2018_p = ArrayList<Bell_M>()

    var bell = bell_2020
    var bell_a = bell_2020_a
    var bell_p = bell_2020_p

    data class Bell(val h: Int, val m: Int, val s: Int, val title: String, val raw: Int)
    data class Bell_M(val s: Int, val title: String, val raw: Int)

    var sourceIndex = 1//0表示2018版，1表示2020版

    fun setSource(index: Int) {
        sourceIndex = index
        when (index) {
            0 -> {
                bell = bell_2018
                bell_a = bell_2018_a
                bell_p = bell_2018_p
            }
            1 -> {
                bell = bell_2020
                bell_a = bell_2020_a
                bell_p = bell_2020_p
            }
        }
    }

    init {
        bell_2020.forEach {
            if (it.h <= 12) {
                bell_2020_a.add(
                    Bell_M(
                        it.h * 3600 + it.m * 60 + it.s - 9 * 3600,
                        it.title.substring(5),
                        it.raw
                    )
                )
            } else {
                bell_2020_p.add(
                    Bell_M(
                        it.h * 3600 + it.m * 60 + it.s - 15 * 3600,
                        it.title.substring(5),
                        it.raw
                    )
                )
            }
        }
        bell_2018.forEach {
            if (it.h <= 12) {
                bell_2018_a.add(
                    Bell_M(
                        it.h * 3600 + it.m * 60 + it.s - 9 * 3600,
                        it.title.substring(5),
                        it.raw
                    )
                )
            } else {
                bell_2018_p.add(
                    Bell_M(
                        it.h * 3600 + it.m * 60 + it.s - 15 * 3600,
                        it.title.substring(5),
                        it.raw
                    )
                )
            }
        }
        setSource(1)
    }

}