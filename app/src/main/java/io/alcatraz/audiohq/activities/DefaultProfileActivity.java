package io.alcatraz.audiohq.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import io.alcatraz.audiohq.R;
import io.alcatraz.audiohq.extended.CompatWithPipeActivity;
import io.alcatraz.audiohq.fragments.ControlPanelFragment;

public class DefaultProfileActivity extends CompatWithPipeActivity {
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_def_profile);
        toolbar = findViewById(R.id.def_profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.def_profile_replace,ControlPanelFragment.newInstance(null,true))
                .commit();
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
