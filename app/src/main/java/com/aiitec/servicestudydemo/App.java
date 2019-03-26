package com.aiitec.servicestudydemo;

import android.app.Application;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: ailibin
 * @Time: 2019/1/8
 * @Description: 服务app
 * @Email: ailibin@qq.com
 */
public class App extends Application {

    public static ExecutorService cachedThreadPool;

    public static Application instance;

    public synchronized static Application getInstance() {

        if (instance == null) {
            instance = new Application();
        }
        return instance;
    }

    public static ExecutorService getCachedThreadPool() {
        return cachedThreadPool;
    }

    public void onCreate() {
        super.onCreate();
        cachedThreadPool = Executors.newCachedThreadPool();
    }
}
