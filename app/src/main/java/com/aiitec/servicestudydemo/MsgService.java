package com.aiitec.servicestudydemo;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * @Author: ailibin
 * @Time: 2019/1/8
 * @Description: 服务
 * @Email: ailibin@qq.com
 */
public class MsgService extends Service {

    public static final String TAG = "ailibin";

    /**
     * 进度条的最大值
     */
    public static final int MAX_PROGRESS = 100;
    /**
     * 进度条的进度值
     */
    private int progress = 0;

    /**
     * status=0 未开始 status=1 正在下载中 status=2暂停下载 status=3 完成下载
     */
    private int status = 0;

    /**
     * 更新进度的回调接口
     */
    private OnProgressListener onProgressListener;


    /**
     * 注册回调接口的方法，供外部调用
     *
     * @param onProgressListener
     */
    public void setOnProgressListener(OnProgressListener onProgressListener) {
        this.onProgressListener = onProgressListener;
    }

    /**
     * 增加get()方法，供Activity调用
     *
     * @return 下载进度
     */
    public int getProgress() {
        return progress;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    /**
     * 无法打断一直执行完毕为止
     */
    class MyTask extends AsyncTask<Void, Integer, Void> {

        /**
         * 子线程操作
         *
         * @param voids
         * @return
         */
        @Override
        protected Void doInBackground(Void... voids) {
            while (progress < MAX_PROGRESS) {
                status = 1;
                progress += 5;
                //进度发生变化通知调用方
                if (onProgressListener != null) {
                    onProgressListener.onProgress(progress);
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            //任务开始前执行
            super.onPreExecute();
        }

        /**
         * 作用：接收线程任务执行结果、将执行结果显示到UI组件
         * 注：必须复写，从而自定义UI操作
         *
         * @param aVoid
         */
        @Override
        protected void onPostExecute(Void aVoid) {
            //UI操作,加载完毕
            status = 3;
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // 作用：在主线程 显示线程任务执行的进度
            super.onProgressUpdate(values);
        }
    }

    public void pauseDownLoad() {
        if (status != 3) {
            //还没有完成
            status = 2;
        } else {
            //完成之后就清零
            status = 0;
            progress = 0;
        }

        if (myTask != null) {
            myTask.cancel(true);
        }

    }


    MyTask myTask;

    /**
     * 模拟下载任务，每100毫秒更新一次
     */
    public void startDownLoad() {

        myTask = new MyTask();
        myTask.execute();

    }

    /**
     * 返回一个Binder对象
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind");
        return new MsgBinder();
    }


    public class MsgBinder extends Binder {
        /**
         * 获取当前Service的实例
         *
         * @return
         */
        public MsgService getService() {
            return MsgService.this;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e(TAG, "onUnbind");
        return super.onUnbind(intent);
    }


}
