package com.rocky.moreapps.example;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class SectionsPagerAdapter extends FragmentStateAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2};

    private final Context context;

    SectionsPagerAdapter(Context context, LifecycleOwner lifecycle, FragmentManager fm) {
        super(fm, lifecycle.getLifecycle());
        this.context = context;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return UpdaterExampleFragment.getInstance();
            case 0:
            default:
                return MoreAppsExampleFragment.getInstance();
        }
    }

    public String getItemTitle(int position) {
        return context.getString(TAB_TITLES[position]);
    }

    @Override
    public int getItemCount() {
        return TAB_TITLES.length;
    }
}