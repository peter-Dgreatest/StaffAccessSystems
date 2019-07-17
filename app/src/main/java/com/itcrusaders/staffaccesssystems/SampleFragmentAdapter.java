package com.itcrusaders.staffaccesssystems;



import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.itcrusaders.staffaccesssystems.Admin.BlankFragment1;

public class SampleFragmentAdapter extends FragmentPagerAdapter {

    public SampleFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return "Fragment"+position;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        if(position==0) {
            fragment = new BlankFragment1();
            return fragment;

        }else if(position==1){
            fragment = new PlusOneFragment();
            return fragment;
        }
        else
            return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}

