package io.alcatraz.audiohq.core.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import io.alcatraz.audiohq.services.FloatPanelService;

public class KeyListener implements Runnable {
    private boolean alive = true;
    private boolean direct_react = false;
    private Context context;
    private KeyListenInterface listenInterface;
    private Handler cleaner;
    private Runnable cleanTask;

    public int getDelayed() {
        return delayed;
    }

    public void setDelayed(int delayed) {
        this.delayed = delayed;
    }

    private int delayed = 3000;

    public KeyListener(Context context, KeyListenInterface callback, Handler cleaner, Runnable cleanTask) {
        listenInterface = callback;
        this.context = context;
        this.cleaner = cleaner;
        this.cleanTask = cleanTask;
    }

    @Override
    public void run() {
        Looper.prepare();
        try {
            String readLine;
            Process exec = Runtime.getRuntime().exec("su");
            DataOutputStream dataOutputStream = new DataOutputStream(exec.getOutputStream());
            dataOutputStream.writeBytes("getevent -l\n");
            dataOutputStream.flush();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
            while (alive) {
                readLine = bufferedReader.readLine();
                if (readLine == null) {
                    continue;
                }
                if (readLine.contains("KEY_VOLUME")) {
                    if (direct_react) {
                        cleaner.removeCallbacks(cleanTask);
                        context.sendBroadcast(new Intent().setAction(FloatPanelService.AHQ_FLOAT_TRIGGER_ACTION));
                        cleaner.postDelayed(cleanTask,delayed);
                    }
                }
            }
            dataOutputStream.close();
            listenInterface.onKilled();
        } catch (IOException io) {

        }
    }

    public void shutDown() {
        alive = false;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setDirect_react(boolean direct_react){
        this.direct_react = direct_react;
    }

    public interface KeyListenInterface {
        void onKilled();
    }
}
