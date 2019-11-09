package com.rocky.moreapps.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.util.Set;
import java.util.TreeSet;

class MoreAppsPrefHelper {
    private static final String SHARED_PREFS_NAME = "MORE_APPS";

    private static MoreAppsPrefHelper instance;

    private SharedPreferences sharedPreferences;

    private MoreAppsPrefHelper(@NonNull Context context) {
        instance = this;
        sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized MoreAppsPrefHelper getInstance(@NonNull Context context) {
        if (instance == null) {
            instance = new MoreAppsPrefHelper(context);
        }

        return instance;
    }

    public void delete(String key) {
        if (sharedPreferences.contains(key)) {
            getEditor().remove(key).apply();
        }
    }

    public void deleteAll() {
        getEditor().clear().apply();
    }

    public void save(String key, Object value) {
        SharedPreferences.Editor editor = getEditor();
        if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Enum) {
            editor.putString(key, value.toString());
        } else if (value != null) {
            throw new RuntimeException("Attempting to save non-supported preference");
        }

        editor.apply();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) sharedPreferences.getAll().get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, T defValue) {
        T returnValue = (T) sharedPreferences.getAll().get(key);
        return returnValue == null ? defValue : returnValue;
    }

    public boolean has(String key) {
        return sharedPreferences.contains(key);
    }

    private SharedPreferences.Editor getEditor() {
        return sharedPreferences.edit();
    }

    public Set<String> getSet(String key, Set<String> set) {
        return sharedPreferences.getStringSet(key, set);
    }

    public void saveSet(String key, TreeSet<String> set) {
        getEditor().putStringSet(key, set).commit();
    }
}
