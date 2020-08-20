package io.alcatraz.audiohq.services;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

import io.alcatraz.audiohq.LogBuff;

public class Discharger extends AccessibilityService {
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_HOME || event.getKeyCode() == KeyEvent.KEYCODE_BACK || event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
            LogBuff.E("1");
            Log.e("ALCDisc","1");
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                LogBuff.E("2");
                Log.e("ALCDisc","1");
                sendBroadcast(new Intent().setAction(FloatPanelService.AHQ_FLOAT_DISCHAGE_ACTION));
            }
        }
        return false;
    }
}
