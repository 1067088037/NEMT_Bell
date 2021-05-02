package com.shishi.nemt_bell

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    val mainTitle = MutableLiveData("自动模式\n铃声已经启动\n请勿锁定手机屏幕")
    val subTitle = MutableLiveData("")

}