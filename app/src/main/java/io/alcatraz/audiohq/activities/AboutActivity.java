package io.alcatraz.audiohq.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import io.alcatraz.audiohq.BuildConfig;
import io.alcatraz.audiohq.Constants;
import io.alcatraz.audiohq.Easter2020;
import io.alcatraz.audiohq.R;
import io.alcatraz.audiohq.adapters.AuthorAdapter;
import io.alcatraz.audiohq.adapters.QueryElementAdapter;
import io.alcatraz.audiohq.beans.QueryElement;
import io.alcatraz.audiohq.extended.CompatWithPipeActivity;
import io.alcatraz.audiohq.extended.DividerItemDecoration;
import io.alcatraz.audiohq.utils.PackageCtlUtils;
import io.alcatraz.audiohq.utils.Utils;

public class AboutActivity extends CompatWithPipeActivity {
    private List<Integer> images = new ArrayList<Integer>();
    @SuppressLint("UseSparseArrays")
    private Map<Integer, List<String>> data = new HashMap<>();
    AuthorAdapter authorAdapter;
    private Easter2020 easter2020;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initData();
        initViews();
        easter2020 = new Easter2020(this);
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
        if (item.getItemId() == android.R.id.home) {
            finishAfterTransition();
        }
        return super.onOptionsItemSelected(item);
    }

    public void initViews() {
        Toolbar toolbar = findViewById(R.id.about_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ListView listView = findViewById(R.id.authorcontentListView1);
        authorAdapter = new AuthorAdapter(this, data, images);
        listView.setAdapter(authorAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        if (vibrator.hasVibrator()) {
                            view.post(() -> {
                                vibrator.cancel();
                                vibrator.vibrate(
                                        VibrationEffect.createOneShot(
                                                100,
                                                VibrationEffect.DEFAULT_AMPLITUDE
                                        )
                                );
                            });
                        }

                        authorAdapter.getAnniversary().append(".");
                        easter2020.shortClick();
                        break;
                    case 1:
                        showDetailDev();
                        break;
                    case 2:
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Alcatraz323/audiohq_md2")));
                        break;
                    case 3:
                        showOSPDialog();
                        break;
                    case 4:
                        Intent intent = new Intent(AboutActivity.this, SetupActivity.class);
                        intent.putExtra(SetupActivity.KEY_SETUP_START_UP_TYPE, SetupActivity.STARTUP_FORCE_SHOW_UPDATE);
                        startActivity(intent);
                        break;
                }
            }
        });

        listView.setOnItemLongClickListener((adapterView, view, i, l) -> {
            if (i == 0) {
                view.post(() -> {
                    vibrator.cancel();
                    vibrator.vibrate(
                            VibrationEffect.createOneShot(
                                    200,
                                    VibrationEffect.DEFAULT_AMPLITUDE
                            )
                    );
                });

                authorAdapter.getAnniversary().append("-");
                easter2020.longClick();
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
        images.add(R.drawable.ic_info_outline_black_24dp);
        images.add(R.drawable.ic_account_circle_black_24dp);
        images.add(R.drawable.ic_code_black_24dp);
        images.add(R.drawable.ic_open_in_new_black_24dp);
        images.add(R.drawable.history);
        List<String> l1 = new ArrayList<>();
        l1.add(getString(R.string.au_l_1));
        l1.add(BuildConfig.VERSION_NAME);
        List<String> l2 = new ArrayList<>();
        l2.add(getString(R.string.au_l_2));
        l2.add(getString(R.string.au_l_2_1));
        List<String> l3 = new ArrayList<>();
        l3.add(getString(R.string.au_l_3));
        l3.add("");
        List<String> l4 = new ArrayList<>();
        l4.add(getString(R.string.au_l_4));
        l4.add(getString(R.string.au_l_4_1));
        List<String> l5 = new ArrayList<>();
        l5.add(getString(R.string.au_l_5));
        l5.add(getString(R.string.update_date));
        data.put(0, l1);
        data.put(1, l2);
        data.put(2, l3);
        data.put(3, l4);
        data.put(4, l5);
    }

    public void clearAnniversaryCode() {
        authorAdapter.getAnniversary().setText(R.string.au_l_1st_anniversary_version);
        authorAdapter.getAnniversary().append(" ");
    }
}