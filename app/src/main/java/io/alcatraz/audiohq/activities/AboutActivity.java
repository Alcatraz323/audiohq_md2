package io.alcatraz.audiohq.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.alcatraz.audiohq.Constants;
import io.alcatraz.audiohq.Easter;
import io.alcatraz.audiohq.R;
import io.alcatraz.audiohq.adapters.AuthorAdapter;
import io.alcatraz.audiohq.adapters.QueryElementAdapter;
import io.alcatraz.audiohq.beans.QueryElement;
import io.alcatraz.audiohq.extended.CompatWithPipeActivity;
import io.alcatraz.audiohq.extended.DividerItemDecoration;
import io.alcatraz.audiohq.utils.PackageCtlUtils;
import io.alcatraz.audiohq.utils.Utils;

public class AboutActivity extends CompatWithPipeActivity {
    private List<Integer> imgs = new ArrayList<Integer>();
    @SuppressLint("UseSparseArrays")
    private Map<Integer, List<String>> data = new HashMap<>();
    private ListView lv;
    private Toolbar tb;
    private Easter easter;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initData();
        initViews();
        easter = new Easter(this);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }

    public void showDetailDev() {
        AlertDialog g = new AlertDialog.Builder(this)
                .setTitle(R.string.au_l_2)
                .setMessage("Code:Alcatraz(GooglePlay)\n" +
                        "Main tester:Mr_Dennis")
                .setPositiveButton(R.string.ad_pb, null)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishAfterTransition();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void initViews() {
        tb = findViewById(R.id.about_toolbar);
        setSupportActionBar(tb);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lv = findViewById(R.id.authorcontentListView1);
        AuthorAdapter aa = new AuthorAdapter(this, data, imgs);
        lv.setAdapter(aa);
        lv.setOnItemClickListener((p1, p2, p3, p4) -> {
            if (p1.getItemAtPosition(p3).toString().equals(getString(R.string.au_l_3))) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Alcatraz323/audiohq_md2")));
            } else if (p1.getItemAtPosition(p3).toString().equals(getString(R.string.au_l_4))) {
                showOSPDialog();
            } else if (p1.getItemAtPosition(p3).toString().equals(getString(R.string.au_l_2))) {
                showDetailDev();
            }else {
                if(vibrator.hasVibrator()){
                    p2.post(() -> {
                        vibrator.cancel();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(VibrationEffect.createOneShot(100,VibrationEffect.DEFAULT_AMPLITUDE));
                        }else {
                            vibrator.vibrate(100);
                        }
                    });

                }
                easter.shortClick();
            }
        });

        lv.setOnItemLongClickListener((adapterView, view, i, l) -> {
            if(i == 0){
                view.post(() -> {
                    vibrator.cancel();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        vibrator.vibrate(200);
                    }
                });
            }
            return true;
        });
    }

    public void showOSPDialog() {
        @SuppressLint("InflateParams") View v = getLayoutInflater().inflate(R.layout.dialog_ops, null);
        new AlertDialog.Builder(this)
                .setTitle(R.string.au_osp)
                .setView(v)
                .setNegativeButton(R.string.ad_nb3, null).show();
        RecyclerView rv = v.findViewById(R.id.opRc1);
        List<QueryElement> dat = Constants.getOpenSourceProjects();
        QueryElementAdapter mra = new QueryElementAdapter(this, dat);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(mra);
        rv.addItemDecoration(new DividerItemDecoration(this, LinearLayout.HORIZONTAL,
                Utils.Dp2Px(this, 8), Color.parseColor("#eeeeee")));
    }


    public void initData() {
        imgs.add(R.drawable.ic_info_outline_black_24dp);
        imgs.add(R.drawable.ic_account_circle_black_24dp);
        imgs.add(R.drawable.ic_code_black_24dp);
        imgs.add(R.drawable.ic_open_in_new_black_24dp);
        List<String> l1 = new ArrayList<>();
        l1.add(getString(R.string.au_l_1));
        l1.add(PackageCtlUtils.getVersionName(this));
        List<String> l2 = new ArrayList<>();
        l2.add(getString(R.string.au_l_2));
        l2.add(getString(R.string.au_l_2_1));
        List<String> l3 = new ArrayList<>();
        l3.add(getString(R.string.au_l_3));
        l3.add("");
        List<String> l4 = new ArrayList<>();
        l4.add(getString(R.string.au_l_4));
        l4.add(getString(R.string.au_l_4_1));
        data.put(0, l1);
        data.put(1, l2);
        data.put(2, l3);
        data.put(3, l4);

    }
}