package com.aiitec.servicestudydemo

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.widget.ProgressBar
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_service_demo.*


/**
 * @Author: ailibin
 * @Time: 2019/02/20
 * @Description: 服务demo
 * @Email: ailibin@qq.com
 */
class ServiceTestActivity : AppCompatActivity() {

    private var msgService: MsgService? = null
    private var mProgressBar: ProgressBar? = null
    private var tv_progress: TextView? = null
    private var CODE = 0x110
    private var mProgress: Int = -1
    private var isDownLoad = false


    val handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            if (msg?.what == CODE) {
                val progress = msg.arg1
                if (progress == 100) {
                    //完成之后，重置
                    tv_progress?.text = "0"
                    mProgressBar?.progress = 0
                } else {
                    tv_progress?.text = progress.toString()
                    mProgressBar?.progress = progress
                }
            }
        }
    }

    val conn = object : ServiceConnection {
        override fun onServiceDisconnected(p0: ComponentName?) {

        }

        override fun onServiceConnected(p0: ComponentName?, sevice: IBinder?) {
            //连接上了服务,返回一个服务对象
            msgService = (sevice as (MsgService.MsgBinder)).service
            //注册回调接口来接收下载进度的变化
            msgService?.setOnProgressListener { progress ->
                val message = handler.obtainMessage()
                message.what = CODE
                message.arg1 = progress
                handler.sendMessage(message)
            }

        }
    }

    /** 查看服务是否开启 */
    fun isServiceRunning(context: Context, serviceName: String): Boolean {
        //获取服务方
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val infos = am.getRunningServices(100)
        for (info in infos) {
            val className = info.service.className
            if (serviceName == className)
                return true
        }
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_demo)
        initListener()
        val intent = Intent(this, MsgService::class.java)
        //测试开始
        mProgressBar = findViewById(R.id.progressBar)
        tv_progress = findViewById(R.id.tv_progress)
        button1.setOnClickListener {
            //开始下载
            isDownLoad = if (!isDownLoad) {
                msgService?.startDownLoad()
                true
            } else {
                msgService?.pauseDownLoad()
                false
            }
        }

        button2.setOnClickListener {
            //暂停服务
//            if (!isServiceRunning(this, MsgService::class.java.simpleName)) {
//                //服务没有在运行了，就不执行这个方法
//                return@setOnClickListener
//            }
            when (type) {
                0 -> unbindService(conn)
                1 -> stopService(intent)
                2 -> {
                    stopService(intent)
                    unbindService(conn)
                }
            }
        }

        button3.setOnClickListener {
            //开始服务
            when (type) {
                0 -> bindService(intent, conn, Context.BIND_AUTO_CREATE)
                1 -> {
                    startService(intent)
                }
                2 -> {
                    //android5.0要显性声明,不然会报Service Intent must be explicit: Intent
                    startService(intent)
                    bindService(intent, conn, Context.BIND_AUTO_CREATE)
                }
            }
        }

    }

    private var type: Int = 0

    private fun initListener() {

        rg_select.setOnCheckedChangeListener { radioGroup, i ->
            val checkedId = radioGroup.checkedRadioButtonId
            when (checkedId) {
                R.id.rb_left -> {
                    type = 0
                    //绑定方式启动service
                }
                R.id.rb_right -> {
                    type = 1
                    //非绑定方式启动service
                }
                R.id.rb_behind -> {
                    type = 2
                    //先启动后绑定方式启动service
                }
            }
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(conn)
    }

}