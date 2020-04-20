package io.alcatraz.audiohq.utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.alcatraz.audiohq.R;

public class Utils {
    public static int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static boolean isStringNotEmpty(String target) {
        return target != null && !target.equals("") && !target.equals("null");
    }

    public static String extractStringArr(String[] arr) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            out.append(arr[i]);
            if (i != arr.length - 1)
                out.append(",");
        }
        return out.toString();
    }

    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
        }
        return hasNavigationBar;
    }

    public static List<String> getStrList(String inputString, int length) {
        int size = inputString.length() / length;
        if (inputString.length() % length != 0) {
            size += 1;
        }
        return getStrList(inputString, length, size);
    }

    public static List<String> getStrList(String inputString, int length,
                                          int size) {
        List<String> list = new ArrayList<>();
        for (int index = 0; index < size; index++) {
            String childStr = substring(inputString, index * length,
                    (index + 1) * length);
            list.add(childStr);
        }
        return list;
    }

    public static String substring(String str, int f, int t) {
        if (f > str.length())
            return null;
        if (t > str.length()) {
            return str.substring(f, str.length());
        } else {
            return str.substring(f, t);
        }
    }

    public static int getStatusBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    @SuppressLint("InflateParams")
    public static void setupAdapterView(AdapterView adapterView) {
        View parent = (View) adapterView.getParent().getParent();
        if (parent == null) {
            parent = adapterView;
        }
        parent.setPadding(parent.getPaddingLeft(), parent.getPaddingTop(), parent.getPaddingRight(), getNavigationBarHeight(parent.getContext()));
        LayoutInflater inflater = (LayoutInflater) adapterView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View emptyView = inflater.inflate(R.layout.panel_empty_view, null);
        ((ViewGroup) adapterView.getParent()).addView(emptyView);
        adapterView.setEmptyView(emptyView);
    }

    public static int getNavigationBarHeight(Context context) {
        if (!checkDeviceHasNavigationBar(context))
            return 0;
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    public static AlertDialog getProcessingDialog(Context ctx, List<View> out, boolean cancelable, boolean showProgressBar, boolean needAsync, Runnable async) {
        LayoutInflater lf = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View root = lf.inflate(R.layout.dialog_processing, null);
        TextView content = root.findViewById(R.id.ad_content);
        ProgressBar pb = root.findViewById(R.id.ad_processing_progress);
        if (!showProgressBar) {
            pb.setVisibility(View.GONE);
        }
        out.add(content);
        out.add(pb);
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx)
                .setTitle(R.string.ad_processing)
                .setView(root);

        if (cancelable) {
            builder.setNegativeButton(R.string.ad_nb3, null);
        }
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(cancelable);

        if (needAsync) {
            new Thread(async).start();
        }
        return alertDialog;
    }

    public static void setSeekBarColor(SeekBar seekBar, int color){
        try {
            LayerDrawable layerDrawable = (LayerDrawable)
                    seekBar.getProgressDrawable();
            Drawable dra = layerDrawable.getDrawable(2);
            dra.setColorFilter(color, PorterDuff.Mode.SRC);
            Drawable dra2 = seekBar.getThumb();
            seekBar.getThumb().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            seekBar.invalidate();
        }catch (Exception e){

        }
    }

    public static AlertDialog getProcessingDialog(Context ctx, List<View> out, boolean cancelable, boolean showProgressBar) {
        return getProcessingDialog(ctx, out, cancelable, showProgressBar, false, null);
    }

    public static void setSpinnerItemSelectedByValue(Spinner spinner, String value) {
        SpinnerAdapter apsAdapter = spinner.getAdapter();
        int k = apsAdapter.getCount();
        for (int i = 0; i < k; i++) {
            if (value.equals(apsAdapter.getItem(i).toString())) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    public static void setupSRL(SwipeRefreshLayout target) {
        target.setColorSchemeResources(R.color.colorPrimary, R.color.green_colorPrimary, R.color.orange_colorPrimary, R.color.pink_colorPrimary);
    }

    public static void copyToClipboard(String content, Context c) {
        android.content.ClipboardManager clipboardManager = (android.content.ClipboardManager) c.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData myClip = ClipData.newPlainText("text", content);
        assert clipboardManager != null;
        clipboardManager.setPrimaryClip(myClip);
        Toast.makeText(c, R.string.toast_copied, Toast.LENGTH_SHORT).show();
    }

    public static void setImageWithTint(ImageView imgv, int resId, int color) {
        imgv.setImageDrawable(getTintedDrawable(imgv.getContext(), resId, color));
    }

    public static void setImageWithTint(ImageButton imgv, int resId, int color) {
        imgv.setImageDrawable(getTintedDrawable(imgv.getContext(), resId, color));
    }

    public static void setViewsEnabled(List<View> views,boolean enabled){
        for(View v : views){
            v.setEnabled(enabled);
        }
    }

    public static void retintImage(ImageView imgv,int color){
        Drawable drawable = imgv.getDrawable();
        Drawable compat = DrawableCompat.wrap(drawable);
        compat.setBounds(0, 0, compat.getMinimumWidth(), compat.getMinimumHeight());
        DrawableCompat.setTint(compat, color);
        imgv.setImageDrawable(compat);
    }

    public static Drawable getTintedDrawable(Context context, int resId, int color) {
        Drawable up = ContextCompat.getDrawable(context, resId);
        Drawable drawableUp = DrawableCompat.wrap(up);
        drawableUp.setBounds(0, 0, drawableUp.getMinimumWidth(), drawableUp.getMinimumHeight());
        DrawableCompat.setTint(drawableUp, color);
        return drawableUp;
    }

    public static boolean checkAndSetErr(TextInputLayout textInputLayout) {
        String raw = Objects.requireNonNull(textInputLayout.getEditText()).getText().toString();
        try {
            float num = Float.parseFloat(raw);
            if (num >= 0 && num <= 1) {
                return true;
            }
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError(textInputLayout.getContext().getResources().getString(R.string.til_err_ofr));
            return false;
        } catch (Exception e) {
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError(textInputLayout.getContext().getResources().getString(R.string.til_err_parse));
            return false;
        }
    }

    public static Boolean copyAssetsFile(Context context, String filename, String des) {
        Boolean isSuccess = true;
        AssetManager assetManager = context.getAssets();

        InputStream in;
        OutputStream out;
        try {
            in = assetManager.open(filename);
            String newFileName = des + "/" + filename;
            out = new FileOutputStream(newFileName);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            isSuccess = false;
        }
        return isSuccess;
    }

    public static <T> T json2Object(String s, Class<T> type) {
        return new Gson().fromJson(s, type);
    }

    public static String extractPackageName(String process){
        if(process.contains(":")){
            process = process.split(":")[0];
        }
        return process;
    }

    public static String getFinalProcessName(boolean isweakkey,String process){
        if(isweakkey)
            process = extractPackageName(process);
        return process;
    }


    public static void ignoreBatteryOptimization(Context context) {

        PowerManager powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);

        boolean hasIgnored = powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
        if(!hasIgnored) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:"+context.getPackageName()));
            context.startActivity(intent);
        }
    }

    public static void setViewSize(View view,int width,int height){
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);
    }

    public static void setListViewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

}
