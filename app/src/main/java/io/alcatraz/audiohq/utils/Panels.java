package io.alcatraz.audiohq.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;

import com.google.android.material.textfield.TextInputEditText;

import io.alcatraz.audiohq.R;
import io.alcatraz.audiohq.activities.PresetInnerActivity;
import io.alcatraz.audiohq.beans.preset.ControlElement;
import io.alcatraz.audiohq.extended.CompatWithPipeActivity;

public class Panels {
    public static AlertDialog getNotInstalledPanel(Context context) {
        return new AlertDialog.Builder(context)
                .setTitle(R.string.install_title)
                .setMessage(R.string.install_not_installed)
                .setPositiveButton(R.string.ad_pb, null).create();
    }

    public static AlertDialog getManualAddPanel(CompatWithPipeActivity activity){
        View root = activity.getLayoutInflater().inflate(R.layout.panel_manual_add,null);
        TextInputEditText editText = root.findViewById(R.id.manual_add_edittext);
        return new AlertDialog.Builder(activity)
                .setTitle(R.string.preset_manual_add)
                .setView(root)
                .setNegativeButton(R.string.ad_nb,null)
                .setPositiveButton(R.string.ad_pb, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String process = editText.getText().toString();
                        if(Utils.isStringNotEmpty(process)){
                            ControlElement controlElement = new ControlElement(process,null,process,false,
                                    null,0);
                            Intent intent = new Intent(activity, PresetInnerActivity.class);
                            intent.putExtra(PresetInnerActivity.KEY_INNER_PRESET,controlElement);
                            activity.startTransition(intent);
                        }else {
                            editText.setError(activity.getString(R.string.preset_manual_error));
                        }
                    }
                }).create();
    }
}
