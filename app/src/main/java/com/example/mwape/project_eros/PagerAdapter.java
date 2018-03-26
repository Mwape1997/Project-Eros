package com.example.mwape.project_eros;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Mwape on 26.03.2018.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {

    int numberoftabs;
    public PagerAdapter(FragmentManager fm, int numberoftabs) {
        super(fm);
        this.numberoftabs = numberoftabs;
    }


    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                ClockFragment clock = new ClockFragment();
                return clock;
            case 1:
                LED led = new LED();
                return  led;
            case 2:
                MainScreen screen = new MainScreen();
                return  screen;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numberoftabs;
    }
}