package com.nist.washintondc;

import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;

public class ThreadHandler extends HandlerThread {

    private Handler handler;

    public ThreadHandler(String name) {
        super(name);
    }

    public void postTask(Runnable task){
        handler.post(task);
    }

    public void prepareHandler(){
        handler = new Handler(getLooper());
    }

}