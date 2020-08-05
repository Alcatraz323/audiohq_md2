package io.alcatraz.audiohq.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import io.alcatraz.audiohq.AudioHQApplication;
import io.alcatraz.audiohq.Constants;
import io.alcatraz.audiohq.R;
import io.alcatraz.audiohq.adapters.FloatAdapter;
import io.alcatraz.audiohq.beans.AudioHQNativeInterface;
import io.alcatraz.audiohq.beans.playing.Pkgs;
import io.alcatraz.audiohq.beans.playing.PlayingSystem;
import io.alcatraz.audiohq.core.utils.KeyListener;
import io.alcatraz.audiohq.utils.SharedPreferenceUtil;
import io.alcatraz.audiohq.utils.Utils;

public class FloatPanelService extends Service {
    public static final String AHQ_FLOAT_TRIGGER_ACTION = "ahq_float_trigger";
    public static final String AHQ_FLOAT_DISCHAGE_ACTION = "ahq_float_discharge";
    UpdatePreferenceReceiver updatePreferenceReceiver;
    FloatWindowTrigger trigger;
    private String notificationId = "audiohq_floater";

    //Controllers
    WindowManager windowManager;
    LayoutInflater layoutInflater;
    WindowManager.LayoutParams layoutParams;
    VolumeChangeObserver observer;
    Handler handler = new Handler();

    //Widgets
    RelativeLayout root;
    CardView toggle;
    ListView listView;
    ImageView toggle_icon;
    Animation slide_in_animation;
    Animation slide_out_animation;
    Animation list_side_out_anim;

    //Listener
    Thread listener_thread;
    KeyListener listener;

    FloatAdapter adapter;
    List<Pkgs> data = new ArrayList<>();
    PlayingSystem playingSystem;

    boolean hasShownPanel = false;

    //Preference
    String gravity;
    String background;
    String background_dark;
    boolean foreground_service;
    String dismiss_delay;
    String margin_top;
    String margin_top_landscape;
    String icon_tint;
    String icon_tint_dark;
    String toggle_size;
    String font_color;
    String font_color_dark;
    String side_margin;
    String side_margin_landscape;
    String toggle_corner_radius;
    String card_radius;
    String seek_color;
    boolean direct_react;
    boolean no_empty_window;
    List<String> filter = new ArrayList<>();

    Runnable cleaner = new Runnable() {
        @Override
        public void run() {
            toggle.startAnimation(slide_out_animation);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initialize();
        loadPreference();
        initializeWindow();
        registReceivers();
    }

    @SuppressLint("SetTextI18n")
    private void initialize() {
        observer = new VolumeChangeObserver(this);
        observer.registerVolumeReceiver();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        AudioHQApplication application = (AudioHQApplication) getApplication();
        playingSystem = application.getPlayingSystem();

        observer.setOnVolumeChangeListener(new VolumeChangeObserver.OnVolumeChangeListener() {
            @Override
            public void onVolumeChange() {
                if (!direct_react) {
                    handler.removeCallbacks(cleaner);
                    showPanel();
                    handler.postDelayed(cleaner, Integer.parseInt(dismiss_delay));
                }
            }
        });


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String notificationName = "AudioHQ Foreground";
            NotificationChannel channel = new NotificationChannel(notificationId, notificationName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        initKeyListener();
    }

    public synchronized void showPanel() {
        if (!hasShownPanel) {
            showFloatingWindow();
            hasShownPanel = true;
        }
    }

    private void initKeyListener() {
        listener = new KeyListener(this, new KeyListener.KeyListenInterface() {
            @Override
            public void onKilled() {
                try {
                    listener_thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, handler, cleaner);
        listener_thread = new Thread(listener);
        listener_thread.start();
    }

    private void killKeyListener() {
        listener.shutDown();
    }

    private void initializeWindow() {
        initViews();

        layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        if (gravity.equals("start_top"))
            layoutParams.gravity = Gravity.START | Gravity.TOP;
        else if (gravity.equals("end_top"))
            layoutParams.gravity = Gravity.END | Gravity.TOP;
        layoutParams.x = Utils.Dp2Px(this, Integer.parseInt(side_margin));
        layoutParams.y = Utils.Dp2Px(this, Integer.parseInt(margin_top));
        if (foreground_service)
            startForeground(1, getNotification());
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initViews() {
        if (gravity.equals("start_top")) {
            root = (RelativeLayout) layoutInflater.inflate(R.layout.panel_float, null);

        } else if (gravity.equals("end_top")) {
            root = (RelativeLayout) layoutInflater.inflate(R.layout.panel_float_right, null);
        }
        listView = root.findViewById(R.id.audiohq_float_list);
        toggle = root.findViewById(R.id.audiohq_float_trigger);
        toggle_icon = root.findViewById(R.id.audiohq_float_toggle_icon);
        adapter = new FloatAdapter(this, data, handler, cleaner);

        if (gravity.equals("start_top")) {
//            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) toggle.getLayoutParams();
//            lp.setMarginStart(Utils.Dp2Px(this, Integer.parseInt(side_margin)));
//            toggle.setLayoutParams(lp);
            slide_in_animation = AnimationUtils.loadAnimation(this, R.anim.slide_left);
            slide_out_animation = AnimationUtils.loadAnimation(this, R.anim.slide_left_back);
            list_side_out_anim = AnimationUtils.loadAnimation(this, R.anim.slide_left_back);
        } else if (gravity.equals("end_top")) {
//            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) toggle.getLayoutParams();
//            lp.setMarginEnd(Utils.Dp2Px(this, Integer.parseInt(side_margin)));
//            toggle.setLayoutParams(lp);
            slide_in_animation = AnimationUtils.loadAnimation(this, R.anim.slide_right);
            slide_out_animation = AnimationUtils.loadAnimation(this, R.anim.slide_right_back);
            list_side_out_anim = AnimationUtils.loadAnimation(this, R.anim.slide_right_back);
        }

        slide_out_animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                listView.startAnimation(list_side_out_anim);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                try {
                    windowManager.removeViewImmediate(root);
                } catch (Exception e) {

                }
                hasShownPanel = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        list_side_out_anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                clearList();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        toggle.setRadius(Utils.Dp2Px(this, Float.parseFloat(toggle_corner_radius)));
        adapter.setCardRadius(Utils.Dp2Px(this, Float.parseFloat(card_radius)));

        adapter.setCardSeekBarColor(seek_color);
        adapter.setDelayed(Integer.parseInt(dismiss_delay));
        listener.setDelayed(Integer.parseInt(dismiss_delay));

        int tg_size_integer = Utils.Dp2Px(this, Integer.parseInt(toggle_size));

        Utils.setViewSize(toggle, tg_size_integer, tg_size_integer);


        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_fall_down);
        listView.setAdapter(adapter);
        listView.setLayoutAnimation(controller);

        toggle.setOnClickListener(view -> {
            if (adapter.getCount() != 0) {
                listView.startAnimation(list_side_out_anim);
            } else {
                updateList();
            }
        });
        toggle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        handler.removeCallbacks(cleaner);
                        break;
                    case MotionEvent.ACTION_UP:
                        handler.postDelayed(cleaner, Integer.parseInt(dismiss_delay));
                        break;
                    case MotionEvent.ACTION_OUTSIDE:
                        handler.removeCallbacks(cleaner);
                        toggle.startAnimation(slide_out_animation);
                        break;
                }
                return false;
            }
        });
    }

    private void checkAndAdjustHeight() {
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        if (totalHeight >= Utils.Dp2Px(this, 320))
            layoutParams.height = Utils.Dp2Px(this, 320);
        else
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        Configuration mConfiguration = this.getResources().getConfiguration();
        int ori = mConfiguration.orientation;

        if (ori == Configuration.ORIENTATION_LANDSCAPE) {
            layoutParams.y = Utils.Dp2Px(this, Integer.parseInt(margin_top_landscape));
            layoutParams.x = Utils.Dp2Px(this, Integer.parseInt(side_margin_landscape));
        } else {
            layoutParams.y = Utils.Dp2Px(this, Integer.parseInt(margin_top));
            layoutParams.x = Utils.Dp2Px(this, Integer.parseInt(side_margin));
        }
        switch (mConfiguration.uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                adapter.setCardBackground(background_dark);
                toggle.setCardBackgroundColor(Color.parseColor(background_dark));
                Utils.setImageWithTint(toggle_icon, R.drawable.ic_volume_source, Color.parseColor(icon_tint_dark));
                adapter.setFontColor(font_color_dark);
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                adapter.setCardBackground(background);
                toggle.setCardBackgroundColor(Color.parseColor(background));
                Utils.setImageWithTint(toggle_icon, R.drawable.ic_volume_source, Color.parseColor(icon_tint));
                adapter.setFontColor(font_color);
                break;
        }
    }

    private void showFloatingWindow() {
        if (Settings.canDrawOverlays(this)) {
            if (no_empty_window) {
                playingSystem.update(new AudioHQNativeInterface<PlayingSystem>() {
                    @Override
                    public void onSuccess(PlayingSystem result) {
                        List<Pkgs> current_result = result.getData();
                        List<Pkgs> new_list = new ArrayList<>();
                        for (Pkgs i : current_result) {
                            if (!filter.contains(i.getPkg())) {
                                new_list.add(i);
                            }
                        }

                        if (new_list.size() > 0) {
                            showFloatWindow_Impl();
                        }
                    }

                    @Override
                    public void onFailure(String reason) {

                    }
                });
            } else {
                showFloatWindow_Impl();
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void showFloatWindow_Impl() {
        checkAndAdjustHeight();

        windowManager.addView(root, layoutParams);
        toggle.startAnimation(slide_in_animation);

        int dismiss_mills = Integer.parseInt(dismiss_delay);

        //noinspection Convert2Lambda
        root.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        handler.removeCallbacks(cleaner);
                        break;
                    case MotionEvent.ACTION_UP:
                        handler.postDelayed(cleaner, dismiss_mills);
                        break;
                    case MotionEvent.ACTION_OUTSIDE:
                        handler.removeCallbacks(cleaner);
                        toggle.startAnimation(slide_out_animation);
                        break;
                }
                return false;
            }
        });

        handler.postDelayed(cleaner, dismiss_mills);
    }

    public void updateList() {
        playingSystem.update(new AudioHQNativeInterface<PlayingSystem>() {
            @Override
            public void onSuccess(PlayingSystem result) {
                data.clear();
                List<Pkgs> current_result = result.getData();
                for (Pkgs i : current_result) {
                    if (!filter.contains(i.getPkg())) {
                        data.add(i);
                    }
                }
                adapter.notifyDataSetChanged();
                listView.scheduleLayoutAnimation();
            }

            @Override
            public void onFailure(String reason) {

            }
        });
    }

    public void clearList() {
        data.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        observer.unregisterVolumeReceiver();
        unregisterReceiver(updatePreferenceReceiver);
        unregisterReceiver(trigger);
        killKeyListener();
        super.onDestroy();
    }

    private void loadPreference() {
        SharedPreferenceUtil spf = SharedPreferenceUtil.getInstance();
        gravity = (String) spf.get(this, Constants.PREF_FLOAT_WINDOW_GRAVITY,
                Constants.DEFAULT_VALUE_PREF_FLOAT_WINDOW_GRAVITY);
        background = (String) spf.get(this, Constants.PREF_FLOAT_WINDOW_BACKGROUND,
                Constants.DEFAULT_VALUE_PREF_FLOAT_WINDOW_BACKGROUND);
        background_dark = (String) spf.get(this, Constants.PREF_FLOAT_WINDOW_BACKGROUND_DARK,
                Constants.DEFAULT_VALUE_PREF_FLOAT_WINDOW_BACKGROUND_DARK);
        foreground_service = (boolean) spf.get(this, Constants.PREF_FLOAT_FOREGROUND_SERVICE,
                Constants.DEFAULT_VALUE_PREF_FLOAT_FOREGROUND_SERVICE);
        dismiss_delay = (String) spf.get(this, Constants.PREF_FLOAT_WINDOW_DISMISS_DELAY,
                Constants.DEFAULT_VALUE_PREF_FLOAT_WINDOW_DISMISS_DELAY);
        margin_top = (String) spf.get(this, Constants.PREF_FLOAT_WINDOW_MARGIN_TOP,
                Constants.DEFAULT_VALUE_PREF_FLOAT_WINDOW_MARGIN_TOP);
        margin_top_landscape = (String) spf.get(this, Constants.PREF_FLOAT_WINDOW_MARGIN_TOP_LANDSCAPE,
                Constants.DEFAULT_VALUE_PREF_FLOAT_WINDOW_MARGIN_TOP_LANDSCAPE);
        icon_tint = (String) spf.get(this, Constants.PREF_FLOAT_WINDOW_ICON_TINT,
                Constants.DEFAULT_VALUE_PREF_FLOAT_WINDOW_ICON_TINT);
        icon_tint_dark = (String) spf.get(this, Constants.PREF_FLOAT_WINDOW_ICON_TINT_DARK,
                Constants.DEFAULT_VALUE_PREF_FLOAT_WINDOW_ICON_TINT_DARK);
        toggle_size = (String) spf.get(this, Constants.PREF_FLOAT_WINDOW_TOGGLE_SIZE,
                Constants.DEFAULT_VALUE_PREF_FLOAT_WINDOW_TOGGLE_SIZE);
        font_color = (String) spf.get(this, Constants.PREF_FLOAT_WINDOW_FONT_COLOR,
                Constants.DEFAULT_VALUE_PREF_FLOAT_WINDOW_FONT_COLOR);
        font_color_dark = (String) spf.get(this, Constants.PREF_FLOAT_WINDOW_FONT_COLOR_DARK,
                Constants.DEFAULT_VALUE_PREF_FLOAT_WINDOW_FONT_COLOR_DARK);
        side_margin = (String) spf.get(this, Constants.PREF_FLOAT_WINDOW_SIDE_MARGIN,
                Constants.DEFAULT_VALUE_PREF_FLOAT_WINDOW_SIDE_MARGIN);
        toggle_corner_radius = (String) spf.get(this, Constants.PREF_FLOAT_WINDOW_TOGGLE_CORNER_RADIUS,
                Constants.DEFAULT_VALUE_PREF_FLOAT_WINDOW_TOGGLE_CORNER_RADIUS);
        card_radius = (String) spf.get(this, Constants.PREF_FLOAT_WINDOW_CARD_RADIUS,
                Constants.DEFAULT_VALUE_PREF_FLOAT_WINDOW_CARD_RADIUS);
        seek_color = (String) spf.get(this, Constants.PREF_FLOAT_WINDOW_SEEK_COLOR,
                Constants.DEFAULT_VALUE_PREF_FLOAT_WINDOW_SEEK_COLOR);
        direct_react = (boolean) spf.get(this, Constants.PREF_FLOAT_DIRECT_REACT,
                Constants.DEFAULT_VALUE_PREF_FLOAT_DIRECT_REACT);
        side_margin_landscape = (String) spf.get(this, Constants.PREF_FLOAT_WINDOW_SIDE_MARGIN_LANDSCAPE,
                Constants.DEFAULT_VALUE_PREF_FLOAT_WINDOW_SIDE_MARGIN_LANDSCAPE);
        no_empty_window = (boolean) spf.get(this, Constants.PREF_FLOAT_NO_EMPTY_WINDOW,
                Constants.DEFAULT_VALUE_PREF_NO_EMPTY_WINDOW);
        listener.setDirect_react(direct_react);
        String filter_raw = (String) spf.get(this, Constants.PREF_FLOAT_WINDOW_FILTER,
                Constants.DEFAULT_VALUE_PREF_FLOAT_WINDOW_FILTER);
        if (filter_raw != null && !filter_raw.equals("")) {
            filter.clear();
            String[] filter_cooking = filter_raw.split(",");
            Collections.addAll(filter, filter_cooking);
        }
    }

    private Notification getNotification() {
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_volume_source)
                .setContentTitle(getString(R.string.notification_overlay));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(notificationId);
        }
        return builder.build();
    }

    public void registReceivers() {
        IntentFilter ifil = new IntentFilter();
        ifil.addAction(Constants.BROADCAST_ACTION_UPDATE_PREFERENCES);
        updatePreferenceReceiver = new UpdatePreferenceReceiver();
        registerReceiver(updatePreferenceReceiver, ifil);
        IntentFilter tr_fil = new IntentFilter();
        tr_fil.addAction(AHQ_FLOAT_TRIGGER_ACTION);
        trigger = new FloatWindowTrigger();
        registerReceiver(trigger, tr_fil);
    }

    class UpdatePreferenceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            loadPreference();
            initializeWindow();
        }
    }

    class FloatWindowTrigger extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            showPanel();
        }
    }
}

class VolumeChangeObserver {
    private static final String ACTION_VOLUME_CHANGED = "android.media.VOLUME_CHANGED_ACTION";

    private Context mContext;
    private OnVolumeChangeListener mOnVolumeChangeListener;
    private VolumeReceiver mVolumeReceiver;

    public VolumeChangeObserver(Context context) {
        mContext = context;
    }

    public void registerVolumeReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_VOLUME_CHANGED);
        mVolumeReceiver = new VolumeReceiver(this);
        mContext.registerReceiver(mVolumeReceiver, intentFilter);
    }

    public void unregisterVolumeReceiver() {
        if (mVolumeReceiver != null) mContext.unregisterReceiver(mVolumeReceiver);
        mOnVolumeChangeListener = null;
    }

    public void setOnVolumeChangeListener(OnVolumeChangeListener listener) {
        this.mOnVolumeChangeListener = listener;
    }

    public interface OnVolumeChangeListener {
        void onVolumeChange();
    }

    private static class VolumeReceiver extends BroadcastReceiver {
        private WeakReference<VolumeChangeObserver> mObserver;

        VolumeReceiver(VolumeChangeObserver observer) {
            mObserver = new WeakReference<>(observer);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mObserver == null) return;
            if (mObserver.get().mOnVolumeChangeListener == null) return;
            if (isReceiveVolumeChange(intent)) {
                OnVolumeChangeListener listener = mObserver.get().mOnVolumeChangeListener;
                listener.onVolumeChange();
            }
        }

        private boolean isReceiveVolumeChange(Intent intent) {
            return intent.getAction() != null
                    && intent.getAction().equals(ACTION_VOLUME_CHANGED);
        }
    }
}

