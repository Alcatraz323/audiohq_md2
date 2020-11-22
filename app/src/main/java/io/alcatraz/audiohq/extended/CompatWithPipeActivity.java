package io.alcatraz.audiohq.extended;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.alcatraz.support.v4.appcompat.StatusBarUtil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import io.alcatraz.audiohq.Constants;
import io.alcatraz.audiohq.R;
import io.alcatraz.audiohq.utils.PermissionInterface;
import io.alcatraz.audiohq.utils.SharedPreferenceUtil;
import io.alcatraz.audiohq.utils.Utils;

@SuppressLint("Registered")
public class CompatWithPipeActivity extends AppCompatActivity {
    PermissionInterface pi;
    UpdatePreferenceReceiver updatePreferenceReceiver;

    int requestQueue = 0;

    public boolean doneFirstInitialize = false;

    //=========PREFERENCES==============
    public boolean default_silent;
    public boolean boot;
    public boolean float_service;
    public String language = "";
    public boolean exclude_from_recent;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (pi != null && requestCode == requestQueue - 1) {
            pi.onResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadPreference();
        setupStatusBarAntiColor();
        setupTransition();
        registerReceivers();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermissionWithCallback(PermissionInterface pi, String[] permissions, int requestCode) {
        this.pi = pi;
        requestPermissions(permissions, requestCode);
    }

    public void requestPermissionWithCallback(PermissionInterface pi, String[] permissions) {
        requestPermissionWithCallback(pi, permissions, requestQueue);
        requestQueue++;
    }

    public void onReloadPreferenceDone() {
    }

    public void loadPreference() {
        SharedPreferenceUtil spf = SharedPreferenceUtil.getInstance();
        boot = (boolean) spf.get(this, Constants.PREF_BOOT, Constants.DEFAULT_VALUE_PREF_BOOT);
        default_silent = (boolean) spf.get(this, Constants.PREF_DEFAULT_SILENT, Constants.DEFAULT_VALUE_PREF_DEFAULT_SILENT);
        float_service = (boolean) spf.get(this, Constants.PREF_FLOAT_SERVICE, Constants.DEFAULT_VALUE_PREF_FLOAT_SERVICE);
        exclude_from_recent = (boolean) spf.get(this, Constants.PREF_EXCLUDE_FROM_RECENT, Constants.DEFAULT_VALUE_PREF_EXCLUDE_FROM_RECENT);
    }

    public void registerReceivers() {
        IntentFilter ifil = new IntentFilter();
        ifil.addAction(Constants.BROADCAST_ACTION_UPDATE_PREFERENCES);
        updatePreferenceReceiver = new UpdatePreferenceReceiver();
        registerReceiver(updatePreferenceReceiver, ifil);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(updatePreferenceReceiver);
        super.onDestroy();
    }

    public void threadSleep() {
        try {
            Thread.sleep(500L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setupStatusBarAntiColor() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        View decorView = getWindow().getDecorView();
        Configuration mConfiguration = this.getResources().getConfiguration();
        int vis = decorView.getSystemUiVisibility();
        switch (mConfiguration.uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                StatusBarUtil.setColor(this, Color.parseColor("#212121"),0);
                vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                decorView.setSystemUiVisibility(vis);
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                StatusBarUtil.setColor(this, Color.WHITE);
                vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                decorView.setSystemUiVisibility(vis);
                break;
        }
    }

    public void setupTransition() {
        Transition slide_right = TransitionInflater.from(this).inflateTransition(android.R.transition.slide_right);
        Transition slide_left = TransitionInflater.from(this).inflateTransition(android.R.transition.slide_right);
        getWindow().setEnterTransition(slide_right);
        getWindow().setExitTransition(slide_left);
        getWindow().setReturnTransition(slide_right);
    }

    public void setupExplodeTransition() {
        Transition explode = TransitionInflater.from(this).inflateTransition(android.R.transition.explode);
        getWindow().setEnterTransition(explode);
        getWindow().setExitTransition(explode);
        getWindow().setReturnTransition(explode);
    }

    public void toast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    public void toast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    @SuppressWarnings("unchecked")
    public void startTransition(Intent intent, View... elements) {
        Pair<View, String>[] pairs = new Pair[elements.length];
        for (int i = 0; i < elements.length; i++) {
            Pair<View, String> pair = new Pair<>(elements[i], elements[i].getTransitionName());
            pairs[i] = pair;
        }
        ActivityOptionsCompat optionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(this, pairs);
        startActivity(intent, optionsCompat.toBundle());
    }

    class UpdatePreferenceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            loadPreference();
            onReloadPreferenceDone();
        }
    }
}
