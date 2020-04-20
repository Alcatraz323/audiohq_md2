package io.alcatraz.audiohq;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import io.alcatraz.audiohq.services.FloatPanelService;
import io.alcatraz.audiohq.utils.SharedPreferenceUtil;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            SharedPreferenceUtil spfu = SharedPreferenceUtil.getInstance();
            boolean float_service =
                    (boolean) spfu.get(context, Constants.PREF_FLOAT_SERVICE, Constants.DEFAULT_VALUE_PREF_FLOAT_SERVICE);
            if (float_service) {
                context.startService(new Intent(context, FloatPanelService.class));
            }
        }
    }
}
