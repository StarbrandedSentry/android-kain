package com.oddlid.karinderya.utils;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class SectionsPageAdapter extends FragmentPagerAdapter {
    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> fragmentTitleList = new ArrayList<>();
    private boolean byOwner;
    private String id;

    public void addFragment(Fragment fragment, String title, boolean byOwner, String id)
    {
        fragmentList.add(fragment);
        fragmentTitleList.add(title);
        this.byOwner = byOwner;
        this.id = id;
    }


    public SectionsPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        //Bundle creation
        Bundle bundle = new Bundle();
        bundle.putBoolean("byOwner", this.byOwner);
        bundle.putString("id", id);
        Fragment fragment = fragmentList.get(i);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitleList.get(position);
    }
}
