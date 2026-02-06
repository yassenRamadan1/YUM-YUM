package com.example.yum_yum.presentation.utils;

import android.content.Context;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FlagManger {

    private static FlagManger instance;
    private final Map<String, String> manualAreaCodes = new HashMap<>();

    private FlagManger() {
        // Initialize common Food API mismatched names
        manualAreaCodes.put("American", "us");
        manualAreaCodes.put("British", "gb");
        manualAreaCodes.put("Canadian", "ca");
        manualAreaCodes.put("Chinese", "cn");
        manualAreaCodes.put("Croatian", "hr");
        manualAreaCodes.put("Dutch", "nl");
        manualAreaCodes.put("Egyptian", "eg");
        manualAreaCodes.put("Filipino", "ph");
        manualAreaCodes.put("French", "fr");
        manualAreaCodes.put("Greek", "gr");
        manualAreaCodes.put("Indian", "in");
        manualAreaCodes.put("Irish", "ie");
        manualAreaCodes.put("Italian", "it");
        manualAreaCodes.put("Jamaican", "jm");
        manualAreaCodes.put("Japanese", "jp");
        manualAreaCodes.put("Kenyan", "ke");
        manualAreaCodes.put("Malaysian", "my");
        manualAreaCodes.put("Mexican", "mx");
        manualAreaCodes.put("Moroccan", "ma");
        manualAreaCodes.put("Polish", "pl");
        manualAreaCodes.put("Portuguese", "pt");
        manualAreaCodes.put("Russian", "ru");
        manualAreaCodes.put("Spanish", "es");
        manualAreaCodes.put("Thai", "th");
        manualAreaCodes.put("Tunisian", "tn");
        manualAreaCodes.put("Turkish", "tr");
        manualAreaCodes.put("Unknown", "xk"); // Fallback
        manualAreaCodes.put("Vietnamese", "vn");
    }

    public static synchronized FlagManger getInstance() {
        if (instance == null) {
            instance = new FlagManger();
        }
        return instance;
    }

    public String getFlagUrl(String areaName) {
        if (areaName == null || areaName.isEmpty()) return null;

        String isoCode = getIsoCode(areaName);
        return "https://flagcdn.com/w80/" + isoCode.toLowerCase() + ".png";
    }

    private String getIsoCode(String areaName) {
        if (manualAreaCodes.containsKey(areaName)) {
            return manualAreaCodes.get(areaName);
        }

        for (Locale locale : Locale.getAvailableLocales()) {
            if (areaName.equalsIgnoreCase(locale.getDisplayCountry(Locale.ENGLISH))) {
                return locale.getCountry().toLowerCase();
            }
        }
        return "xk";
    }
}
