package com.example.yum_yum.presentation.utils;

import android.content.Context;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FlagManger {

    private static FlagManger instance;
    private final Map<String, Integer> flagCache = new HashMap<>();
    private final Map<String, String> isoCodeCache = new HashMap<>();

    private FlagManger() {

    }

    public static synchronized FlagManger getInstance() {
        if (instance == null) {
            instance = new FlagManger();
        }
        return instance;
    }

    public int getFlagDrawableId(Context context, String countryName) {
        if (TextUtils.isEmpty(countryName)) return 0;

        if (flagCache.containsKey(countryName)) {
            return flagCache.get(countryName);
        }

        String isoCode = getIsoCodeFromName(countryName);

        if (isoCode == null) {
            return 0;
        }
        String resourceName = "flag_" + isoCode.toLowerCase();

        int resourceId = context.getResources().getIdentifier(
                resourceName,
                "drawable",
                context.getPackageName()
        );

        flagCache.put(countryName, resourceId);

        return resourceId;
    }

    private String getIsoCodeFromName(String countryName) {
        if (isoCodeCache.containsKey(countryName)) {
            return isoCodeCache.get(countryName);
        }

        for (Locale locale : Locale.getAvailableLocales()) {
            if (countryName.equalsIgnoreCase(locale.getDisplayCountry(Locale.ENGLISH))) {
                String code = locale.getCountry();
                isoCodeCache.put(countryName, code);
                return code;
            }
        }
        return null;
    }
}
