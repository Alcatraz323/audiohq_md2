package io.alcatraz.audiohq.extended;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;

import java.util.Locale;

public class MyContextWrapper extends ContextWrapper {

    public MyContextWrapper(Context base) {
        super(base);
    }

    public static ContextWrapper wrap(Context context, Locale language) {
        Configuration config = context.getResources().getConfiguration();
        Locale.setDefault(language);
        setSystemLocale(config, language);
        context = context.createConfigurationContext(config);
        return new MyContextWrapper(context);
    }

    public static Locale getSystemLocale(Configuration config) {
        return config.getLocales().get(0);
    }

    public static void setSystemLocale(Configuration config, Locale locale) {
        config.setLocale(locale);
    }
}