package io.alcatraz.audiohq.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Objects;

import androidx.appcompat.widget.Toolbar;
import io.alcatraz.audiohq.LogBuff;
import io.alcatraz.audiohq.R;
import io.alcatraz.audiohq.extended.CompatWithPipeActivity;

public class LogActivity extends CompatWithPipeActivity {
    Toolbar toolbar;
    TextView console_log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        initViews();
        initData();
    }

    private void findViews() {
        toolbar = findViewById(R.id.log_toolbar);
        console_log = findViewById(R.id.log_console_box);
    }

    private void initViews(){
        findViews();
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void initData(){
        console_log.setText(LogBuff.getFinalLog());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = new MenuInflater(this);
        mi.inflate(R.menu.activity_log_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishAfterTransition();
                break;
            case R.id.log_refresh:
                initData();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
