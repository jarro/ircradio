package com.cratorsoft.android.viewpager;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.earthflare.android.ircradio.R;


public class PagerAdapterTab extends FragmentPagerAdapter {

    public int num_pages;
    String[] tabTitles;
    int[] contentviews;

    public PagerAdapterTab(FragmentManager fm, String[] tabTitles, int[] contentviews) {
        super(fm);        
        this.num_pages = tabTitles.length;
        this.tabTitles = tabTitles;
        this.contentviews = contentviews;
    }

    public void preload(FragmentManager fm) {
        
        FragmentTransaction ft = fm.beginTransaction();

        for (int i = 0; i < contentviews.length; i++) {

            Fragment fragexists = fm.findFragmentByTag(makeFragmentName(i));
            if (fragexists == null) {

                Bundle args = new Bundle();
                args.putInt("contentview", contentviews[i]);
                FragTab fragtab = new FragTab();
                fragtab.setArguments(args);
                fragtab.setMenuVisibility(false);
                fragtab.setUserVisibleHint(false);
                ft.add(R.id.viewpager, fragtab, makeFragmentName(i));
            }
        }
        ft.commit();

    }

    public View[] getViews(FragmentManager fm) {

        View[] views = new View[num_pages];

        for (int i = 0; i < num_pages; i++) {
            views[i] = fm.findFragmentByTag(makeFragmentName(i)).getView();
        }

        return views;
    }

    @Override
    public Fragment getItem(int arg0) {
        //all pages should be preloaded to initialize views/adapters
        throw new RuntimeException();
    }

    @Override
    public int getCount() {
        return num_pages;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    private static String makeFragmentName(int position) {
        return "android:switcher:" + R.id.viewpager + ":" + position;
    }

    
    
}
