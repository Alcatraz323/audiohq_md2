package io.alcatraz.audiohq.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import androidx.cardview.widget.CardView;

import io.alcatraz.audiohq.AudioHQApplication;
import io.alcatraz.audiohq.Constants;
import io.alcatraz.audiohq.R;
import io.alcatraz.audiohq.beans.playing.Pkgs;
import io.alcatraz.audiohq.core.utils.AudioHQApis;
import io.alcatraz.audiohq.extended.ClickOverrideImageButton;
import io.alcatraz.audiohq.extended.LongClickOverrideFrameLayout;
import io.alcatraz.audiohq.services.FloatPanelService;
import io.alcatraz.audiohq.utils.AnimateUtils;
import io.alcatraz.audiohq.utils.PackageCtlUtils;
import io.alcatraz.audiohq.utils.Utils;

public class FloatAdapter extends BaseAdapter {
    private List<Pkgs> data;
    private FloatPanelService service;
    private LayoutInflater inflater;

    private Handler cleaner;
    private Runnable cleanTask;
    private ListView parent;

    private int font_color = 0;
    private int card_background = 0;
    private int card_corner = 0;
    private int card_seekbar_color = 0;

    public int getDelayed() {
        return delayed;
    }

    public void setDelayed(int delayed) {
        this.delayed = delayed;
    }

    private int delayed = 3000;

    public FloatAdapter(FloatPanelService service, List<Pkgs> data, Handler cleaner, Runnable cleanTask, ListView parent) {
        this.data = data;
        this.service = service;
        this.parent = parent;
        inflater = (LayoutInflater) service.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.cleaner = cleaner;
        this.cleanTask = cleanTask;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.item_float_list, null);
        }
        //Current Data Node
        Pkgs pkgs = data.get(i);
        String label = PackageCtlUtils.getLabel(service, pkgs.getPkg());

        ImageView aplc_icon = view.findViewById(R.id.float_list_item_icon);
        TextView aplc_label = view.findViewById(R.id.float_list_item_label);
        CardView cardView = view.findViewById(R.id.float_list_item_card);
        FrameLayout back_tint = view.findViewById(R.id.float_item_back_tint);

        LongClickOverrideFrameLayout pin = view.findViewById(R.id.float_list_item_pin);
        ImageView pinImage = view.findViewById(R.id.float_list_item_pin_image);

        LinearLayout full_seek_indicator = view.findViewById(R.id.float_list_item_full_seekbar_indicator);
        CardView options_overlay = view.findViewById(R.id.float_list_item_card_overlay_options);
        CardView ignore_confirm = view.findViewById(R.id.float_list_item_card_overlay_ignore_confirm);

        ClickOverrideImageButton options_back = options_overlay.findViewById(R.id.float_list_item_card_overlay_options_back);
        ClickOverrideImageButton options_ignore = options_overlay.findViewById(R.id.float_list_item_card_overlay_options_opreation_ignore);
        ImageButton options_mute = options_overlay.findViewById(R.id.float_list_item_card_overlay_options_operation_mute);

        ClickOverrideImageButton ignore_cancel = ignore_confirm.findViewById(R.id.float_list_item_card_overlay_ignore_confirm_cancel);

        back_tint.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (options_overlay.getVisibility() != View.VISIBLE) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            parent.requestDisallowInterceptTouchEvent(true);
                        case MotionEvent.ACTION_MOVE:
                        case MotionEvent.ACTION_SCROLL:
                            cleaner.removeCallbacks(cleanTask);
                            ViewGroup.LayoutParams params = full_seek_indicator.getLayoutParams();
                            params.width = (int) (event.getX() - full_seek_indicator.getX());
                            full_seek_indicator.setLayoutParams(params);

                            float ratio = (event.getX() - full_seek_indicator.getX()) / back_tint.getWidth();
                            if (ratio < 0) {
                                ratio = 0.0f;
                            } else if (ratio > 1) {
                                ratio = 1.0f;
                            }
                            aplc_label.setText("(" + (int) (ratio * 100) + "%)" + label);

                            AudioHQApis.setProfile(service, pkgs.getPkg(), ratio, (float) pkgs.getLeft(),
                                    (float) pkgs.getRight(), true, true);

                            break;
                        case MotionEvent.ACTION_UP:
                            cleaner.postDelayed(cleanTask, delayed);
                            break;
                    }
                }
                return true;
            }
        });

        full_seek_indicator.post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams params = full_seek_indicator.getLayoutParams();
                params.width = (int) (service.DP_240 * pkgs.getGeneral());
                full_seek_indicator.setLayoutParams(params);
            }
        });

        full_seek_indicator.setBackgroundColor(getCardSeekBarColor());

        if (pkgs.isSticky()) {
            Utils.setImageWithTint(pinImage, R.drawable.ic_pin, AudioHQApplication.color);
        } else {
            pinImage.setImageResource(R.drawable.ic_pin_off);
        }

        if (pkgs.getMuted()) {
            options_mute.setImageResource(R.drawable.ic_volume_off_black_24dp);
        } else {
            options_mute.setImageResource(R.drawable.volume_high);
        }

        pin.setOnClickListener(v -> {
            AudioHQApplication application = (AudioHQApplication) service.getApplication();
            if (pkgs.isSticky()) {
                application.getPlayingSystem().removeStickyApp(pkgs.getPkg());
                pinImage.setImageResource(R.drawable.ic_pin_off);
                pkgs.setSticky(false);
            } else {
                application.getPlayingSystem().addStickyApp(pkgs.getPkg());
                Utils.setImageWithTint(pinImage, R.drawable.ic_pin, AudioHQApplication.color);
                pkgs.setSticky(true);
            }
        });

        pin.setLongClickListener(new LongClickOverrideFrameLayout.LongClickListener() {
            @Override
            public boolean onLongClick(LongClickOverrideFrameLayout view, int x, int y) {
                cleaner.removeCallbacks(cleanTask);
                if (options_overlay.getVisibility() == View.GONE) {
                    options_overlay.setVisibility(View.VISIBLE);
                    options_overlay.post(new Runnable() {
                        @Override
                        public void run() {
                            AnimateUtils.playStart(options_overlay, x, y, 480, () -> {
                                cleaner.postDelayed(cleanTask, delayed + 200);
                            });
                        }
                    });
                }
                return false;
            }
        });

        options_back.setClickListener(new ClickOverrideImageButton.ClickListener() {
            @Override
            public boolean onClick(ClickOverrideImageButton view, int x, int y) {
                if (options_overlay.getVisibility() == View.VISIBLE) {
                    resetTimer();
                    AnimateUtils.playEnd(options_overlay, x, y, 430);
                }
                return false;
            }
        });

        options_ignore.setClickListener(new ClickOverrideImageButton.ClickListener() {
            @Override
            public boolean onClick(ClickOverrideImageButton view, int x, int y) {
                cleaner.removeCallbacks(cleanTask);
                if (ignore_confirm.getVisibility() == View.GONE) {
                    ignore_confirm.setVisibility(View.VISIBLE);
                    AudioHQApplication application = (AudioHQApplication) service.getApplication();
                    application.getPlayingSystem().addIgnoredApp(pkgs.getPkg());
                    ignore_confirm.post(new Runnable() {
                        @Override
                        public void run() {
                            AnimateUtils.playStart(ignore_confirm, back_tint.getWidth() - x - service.DP_16 / 2, y, 430, () -> {
                                cleaner.postDelayed(cleanTask, delayed + 200);
                            });
                        }
                    });
                }
                return false;
            }
        });

        options_mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
                if (pkgs.getMuted()) {
                    options_mute.setImageResource(R.drawable.volume_high);
                    AudioHQApis.unmuteProcess(service, pkgs.getPkg(), true);
                    pkgs.setMuted(false);
                } else {
                    options_mute.setImageResource(R.drawable.ic_volume_off_black_24dp);
                    AudioHQApis.muteProcess(service, pkgs.getPkg(), true);
                    pkgs.setMuted(true);
                }
            }
        });

        ignore_cancel.setClickListener(new ClickOverrideImageButton.ClickListener() {
            @Override
            public boolean onClick(ClickOverrideImageButton view, int x, int y) {
                if (options_overlay.getVisibility() == View.VISIBLE) {
                    resetTimer();
                    AudioHQApplication application = (AudioHQApplication) service.getApplication();
                    application.getPlayingSystem().removeIgnoredApp(pkgs.getPkg());
                    AnimateUtils.playEnd(ignore_confirm, back_tint.getWidth() - x - service.DP_16 / 2, y, 480);
                }
                return false;
            }
        });



        cardView.setRadius(getCardRadius());
        back_tint.setBackgroundColor(getCardBackground());

        aplc_label.setTextColor(getFontColor());
        aplc_icon.setImageDrawable(PackageCtlUtils.getIcon(service, pkgs.getPkg()));
        aplc_label.setText("(" + (int) (pkgs.getGeneral() * 100) + "%)" + label);

        return view;
    }

    public void resetTimer() {
        cleaner.removeCallbacks(cleanTask);
        cleaner.postDelayed(cleanTask, delayed);
    }

    public int getFontColor() {
        if (font_color == 0) {
            font_color = Color.DKGRAY;
        }
        return font_color;
    }

    public void setFontColor(String color) {
        font_color = Color.parseColor(color);
    }

    public int getCardBackground() {
        if (card_background == 0) {
            card_background = Color.DKGRAY;
        }
        return card_background;
    }

    public void setCardBackground(String color) {
        card_background = Color.parseColor(color);
    }

    public int getCardSeekBarColor() {
        if (card_seekbar_color == 0) {
            card_seekbar_color = Color.parseColor(Constants.DEFAULT_VALUE_PREF_FLOAT_WINDOW_FONT_COLOR);
        }
        return card_seekbar_color;
    }

    public void setCardSeekBarColor(String color) {
        card_seekbar_color = Color.parseColor(color);
    }

    public int getCardRadius() {
        if (card_corner == 0) {
            card_corner = Integer.parseInt(Constants.DEFAULT_VALUE_PREF_FLOAT_WINDOW_TOGGLE_CORNER_RADIUS);
        }
        return card_corner;
    }

    public void setCardRadius(int radius) {
        card_corner = radius;
    }
}
