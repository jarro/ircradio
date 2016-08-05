package com.cratorsoft.android.frag;


import android.os.Bundle;
import android.support.v4.preference.PreferenceFragment;

import com.cratorsoft.android.drawer.DrawerDisplayer;
import com.earthflare.android.ircradio.Globo;
import com.earthflare.android.ircradio.GloboUtil;
import com.earthflare.android.ircradio.R;

public class FragEditChannelPreferences extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.channelpreferences);
    }

    @Override
    public void onResume() {
        super.onResume();

        ((DrawerDisplayer)this.getActivity()).setFragTitle(getString(R.string.label_editpreferences), "");
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();


        GloboUtil.globalizeChannelPrefs(Globo.ctx);


    }


}