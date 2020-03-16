package io.alcatraz.audiohq.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import java.util.Objects;

import androidx.appcompat.widget.Toolbar;
import io.alcatraz.audiohq.R;
import io.alcatraz.audiohq.beans.PreferenceHeader;
import io.alcatraz.audiohq.extended.CompatWithPipeActivity;
import io.alcatraz.audiohq.fragments.FloatPreferenceFragment;
import io.alcatraz.audiohq.fragments.OtherPreferenceFragment;
import io.alcatraz.audiohq.fragments.SecurityPreferenceFragment;

public class PreferenceInnerActivity extends CompatWithPipeActivity {
    public static final String PREFERENCE_TRANSFER_HEADER = "PREFERENCE_HEADER";
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference_inner);
        toolbar = findViewById(R.id.preference_act_toolbar);

        Intent intent = getIntent();
        PreferenceHeader header = intent.getParcelableExtra(PREFERENCE_TRANSFER_HEADER);
        toolbar.setTitle(header.getTitle());
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        switch (header.getIcon_res()){
            case R.drawable.ic_branding_watermark_gray_24dp:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.preference_act_fragment_container, new FloatPreferenceFragment())
                        .commit();
                break;
            case R.drawable.ic_security_gray_24dp:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.preference_act_fragment_container, new SecurityPreferenceFragment())
                        .commit();
                break;
            case R.drawable.ic_settings_black_24dp:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.preference_act_fragment_container, new OtherPreferenceFragment())
                        .commit();
                break;
        }
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
}
