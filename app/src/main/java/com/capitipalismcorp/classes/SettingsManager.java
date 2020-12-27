package com.capitipalismcorp.classes;

import java.util.HashMap;
import java.util.Map;

public class SettingsManager {
    private static Map<String, Object> appSettings = new HashMap<>();

    public static Map<String, Object> getAppSettings() {
        return appSettings;
    }

    public static void setAppSettings(Map<String, Object> appSettings) {
        SettingsManager.appSettings = appSettings;
    }
}
