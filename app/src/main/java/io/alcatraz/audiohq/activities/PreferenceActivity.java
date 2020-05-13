package io.alcatraz.audiohq.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import io.alcatraz.audiohq.Constants;
import io.alcatraz.audiohq.R;
import io.alcatraz.audiohq.adapters.PreferenceListAdapter;
import io.alcatraz.audiohq.beans.PreferenceHeader;
import io.alcatraz.audiohq.extended.CompatWithPipeActivity;

public class PreferenceActivity extends CompatWithPipeActivity {
    Toolbar toolbar;
    ListView listView;

    List<PreferenceHeader> headers;
    PreferenceListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        prepareHeader();
        initViews();
    }

    public void initViews() {
        toolbar = findViewById(R.id.preference_act_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listView = findViewById(R.id.preference_act_recycler);
        adapter = new PreferenceListAdapter(this, headers);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(PreferenceActivity.this,PreferenceInnerActivity.class);
            intent.putExtra(PreferenceInnerActivity.PREFERENCE_TRANSFER_HEADER, (PreferenceHeader) adapterView.getItemAtPosition(i));
            startActivity(intent);
        });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sendBroadcast(new Intent().setAction(Constants.BROADCAST_ACTION_UPDATE_PREFERENCES));
    }

    private void prepareHeader() {
        headers = new ArrayList<>();
        PreferenceHeader header_float =
                new PreferenceHeader(getString(R.string.pref_4_t), getString(R.string.pref_4_t_summary), R.drawable.ic_branding_watermark_gray_24dp);
        PreferenceHeader header_security =
                new PreferenceHeader(getString(R.string.pref_2_t), getString(R.string.pref_2_t_summary), R.drawable.ic_security_gray_24dp);
        PreferenceHeader header_others =
                new PreferenceHeader(getString(R.string.pref_3_t), getString(R.string.pref_3_t_summary), R.drawable.ic_settings_black_24dp);
        headers.add(header_float);
        headers.add(header_security);
        headers.add(header_others);
    }
}
