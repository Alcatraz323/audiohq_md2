package io.alcatraz.audiohq.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.widget.Toolbar;
import io.alcatraz.audiohq.R;
import io.alcatraz.audiohq.beans.preset.ControlElement;
import io.alcatraz.audiohq.core.utils.AudioHQApis;
import io.alcatraz.audiohq.extended.CompatWithPipeActivity;
import io.alcatraz.audiohq.fragments.ControlPanelFragment;
import io.alcatraz.audiohq.utils.PackageCtlUtils;
import io.alcatraz.audiohq.utils.Utils;

public class PresetInnerActivity extends CompatWithPipeActivity {
    public static final String KEY_INNER_PRESET = "inner_preset";
    Toolbar toolbar;
    ImageView icon;
    TextView label;
    TextView pkg;
    FloatingActionButton deleteAction;

    ControlElement element;
    ControlPanelFragment control_panel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preset_inner);
        initialize();
        attachControlPanel();
    }

    private void findViews() {
        toolbar = findViewById(R.id.preset_inner_toolbar);
        icon = findViewById(R.id.preset_inner_icon);
        label = findViewById(R.id.preset_inner_label);
        pkg = findViewById(R.id.preset_inner_package);
        deleteAction = findViewById(R.id.preset_inner_delete_fab);
    }

    private void initialize(){
        findViews();
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        element = intent.getParcelableExtra(KEY_INNER_PRESET);
        String pack = Utils.extractPackageName(element.getProcess());
        pkg.setText(pack);
        element.setIcon(PackageCtlUtils.getIcon(this,pack));
        Drawable drawable = PackageCtlUtils.getIcon(this,pack);
        if(drawable !=null) {
            icon.setImageDrawable(drawable);
        }else{
            Utils.retintImage(icon,getColor(R.color.base_gray_tint));
        }
        label.setText(element.getLabel());

        deleteAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                control_panel.disablePanel();
                AudioHQApis.unsetProfile(PresetInnerActivity.this,element.getProcess(),element.isIsweakkey());
                AudioHQApis.unmuteProcess(PresetInnerActivity.this,element.getProcess(),element.isIsweakkey());
                finishAfterTransition();
            }
        });
    }

    private void attachControlPanel(){
        control_panel = ControlPanelFragment.newInstance(element,false);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.preset_inner_control_container,control_panel)
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
