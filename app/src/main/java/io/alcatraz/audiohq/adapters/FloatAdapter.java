package io.alcatraz.audiohq.adapters;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;

import androidx.cardview.widget.CardView;
import io.alcatraz.audiohq.AudioHQApplication;
import io.alcatraz.audiohq.Constants;
import io.alcatraz.audiohq.R;
import io.alcatraz.audiohq.beans.playing.Pkgs;
import io.alcatraz.audiohq.core.utils.AudioHQApis;
import io.alcatraz.audiohq.utils.PackageCtlUtils;
import io.alcatraz.audiohq.utils.Utils;

public class FloatAdapter extends BaseAdapter {
    private List<Pkgs> data;
    private Service context;
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

    public FloatAdapter(Service context, List<Pkgs> data, Handler cleaner, Runnable cleanTask, ListView parent) {
        this.data = data;
        this.context = context;
        this.parent = parent;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.item_float_list, null);
        }
        //Current Data Node
        Pkgs pkgs = data.get(i);

        ImageView aplc_icon = view.findViewById(R.id.float_list_item_icon);
        TextView aplc_label = view.findViewById(R.id.float_list_item_label);
        SeekBar seekBar = view.findViewById(R.id.float_list_item_general_seek);
        CardView cardView = view.findViewById(R.id.float_list_item_card);
        LinearLayout back_tint = view.findViewById(R.id.float_item_back_tint);

        FrameLayout pin = view.findViewById(R.id.float_list_item_pin);
        ImageView pinImage = view.findViewById(R.id.float_list_item_pin_image);

        LinearLayout full_seek_indicator = view.findViewById(R.id.float_list_item_full_seekbar_indicator);
        LinearLayout full_seek_listener = view.findViewById(R.id.float_list_item_full_seek_listener);

        full_seek_listener.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        parent.requestDisallowInterceptTouchEvent(true);
                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_SCROLL:
                        cleaner.removeCallbacks(cleanTask);
                        ViewGroup.LayoutParams params = full_seek_indicator.getLayoutParams();
                        params.width = (int) (event.getX() - full_seek_indicator.getX());
                        full_seek_indicator.setLayoutParams(params);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                AudioHQApis.setProfile(context, pkgs.getPkg(), params.width / v.getWidth() * 0.0001f, (float) pkgs.getLeft(),
                                        (float) pkgs.getRight(), true, true);
                            }
                        }).start();

                        break;
                    case MotionEvent.ACTION_UP:
                        cleaner.postDelayed(cleanTask, delayed);
                        break;
                }
                return true;
            }
        });

        if (pkgs.isSticky()) {
            Utils.setImageWithTint(pinImage, R.drawable.ic_pin, AudioHQApplication.color);
        } else {
            pinImage.setImageResource(R.drawable.ic_pin_off);
        }

        pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioHQApplication application = (AudioHQApplication) context.getApplication();
                if (pkgs.isSticky()) {
                    application.getPlayingSystem().removeStickyApp(pkgs.getPkg());
                    pinImage.setImageResource(R.drawable.ic_pin_off);
                    pkgs.setSticky(false);
                } else {
                    application.getPlayingSystem().addStickyApp(pkgs.getPkg());
                    Utils.setImageWithTint(pinImage, R.drawable.ic_pin, AudioHQApplication.color);
                    pkgs.setSticky(true);
                }
            }
        });

        cardView.setRadius(getCardRadius());
        back_tint.setBackgroundColor(getCardBackground());

        aplc_label.setTextColor(getFontColor());
        aplc_icon.setImageDrawable(PackageCtlUtils.getIcon(context, pkgs.getPkg()));
        aplc_label.setText(PackageCtlUtils.getLabel(context, pkgs.getPkg()));

        seekBar.setProgress((int) (pkgs.getGeneral() * 10000));
        Utils.setSeekBarColor(seekBar, getCardSeekBarColor());

//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                if (b) {
//                    AudioHQApis.setProfile(context, pkgs.getPkg(), i * 0.0001f, (float) pkgs.getLeft(),
//                            (float) pkgs.getRight(), true, true);
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                cleaner.removeCallbacks(cleanTask);
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                cleaner.postDelayed(cleanTask, delayed);
//            }
//        });

        return view;
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
