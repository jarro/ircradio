package com.cratorsoft.android.frag;


import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;


import com.cratorsoft.android.aamain.Fargs;
import com.cratorsoft.android.drawer.DrawerDisplayer;
import com.cratorsoft.android.ttslanguage.TTSPipeline;
import com.earthflare.android.ircradio.Globo;
import com.earthflare.android.ircradio.GloboUtil;
import com.earthflare.android.ircradio.R;

public class FragEditPreferences extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener{


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Fargs fargs = Fargs.create(this);
        addPreferencesFromResource(fargs.getPreferencesResource());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Fargs fargs = Fargs.create(this);
        //addPreferencesFromResource(fargs.getPreferencesResource());
    }


    @Override
    public void onResume() {
        super.onResume();

        ((DrawerDisplayer)this.getActivity()).setFragTitle(getString(R.string.label_editpreferences), "");


        Preference pref = null;
        pref = this.getPreferenceScreen().findPreference("pc_backuprestore");
        if (pref != null){
            pref.setOnPreferenceClickListener(this);
        }

        pref = this.getPreferenceScreen().findPreference("pc_serverdefaults");
        if (pref != null){
            pref.setOnPreferenceClickListener(this);
        }

        pref = this.getPreferenceScreen().findPreference("pc_chatpreferences");
        if (pref != null){
            pref.setOnPreferenceClickListener(this);
        }

        pref = this.getPreferenceScreen().findPreference("pc_ttspreferences");
        if (pref != null){
            pref.setOnPreferenceClickListener(this);
        }

        pref = this.getPreferenceScreen().findPreference("pc_ttslanguages");
        if (pref != null){
            pref.setOnPreferenceClickListener(this);
        }

        //sub pref
        pref = this.getPreferenceScreen().findPreference("pc_ttspreferences_ttsspeak");
        if (pref != null){
            pref.setOnPreferenceClickListener(this);
        }

        pref = this.getPreferenceScreen().findPreference("pc_notifications");
        if (pref != null){
            pref.setOnPreferenceClickListener(this);
        }

        pref = this.getPreferenceScreen().findPreference("pc_toasts");
        if (pref != null){
            pref.setOnPreferenceClickListener(this);
        }

    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();


        new Thread(new Runnable() {
            public void run() {

                GloboUtil.globalizePrefs(Globo.ctx);

                //update speed and pitch
                try {
                    TTSPipeline.getInstance().setPitch(Globo.pref_pitch);
                    TTSPipeline.getInstance().setSpeechRate(Globo.pref_speed);
                } catch (Exception e) {
                    // TODO Auto-generated catch block

                }


            }
        }).start();


    }


    @Override
    public boolean onPreferenceClick(Preference preference) {

        if (preference.getKey().equals("pc_serverdefaults")){

            Fargs fargs = Fargs.create();
            fargs.setPreferencesResource(R.xml.preferences_serverdefaults);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            FragEditPreferences fb = new FragEditPreferences();
            fb.setArguments(fargs.bundle);
            ft.replace(R.id.content_frame, fb);
            ft.addToBackStack("pc_serverdefaults");
            ft.commit();

            return true;
        }


        if (preference.getKey().equals("pc_chatpreferences")){

            Fargs fargs = Fargs.create();
            fargs.setPreferencesResource(R.xml.preferences_chat);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            FragEditPreferences fb = new FragEditPreferences();
            fb.setArguments(fargs.bundle);
            ft.replace(R.id.content_frame, fb);
            ft.addToBackStack("pc_chatpreferences");
            ft.commit();

            return true;
        }

        if (preference.getKey().equals("pc_ttspreferences")){

            Fargs fargs = Fargs.create();
            fargs.setPreferencesResource(R.xml.preferences_tts);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            FragEditPreferences fb = new FragEditPreferences();
            fb.setArguments(fargs.bundle);
            ft.replace(R.id.content_frame, fb);
            ft.addToBackStack("pc_ttspreferences");
            ft.commit();

            return true;
        }


        if (preference.getKey().equals("pc_ttslanguages")){

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            FragListTTSEngines_ fb = new FragListTTSEngines_();
            ft.replace(R.id.content_frame, fb);
            ft.addToBackStack("pc_ttspreferences");
            ft.commit();
            return true;
        }

        //sub
        if (preference.getKey().equals("pc_ttspreferences_ttsspeak")){

            Fargs fargs = Fargs.create();
            fargs.setPreferencesResource(R.xml.preferences_tts_ttsspeak);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            FragEditPreferences fb = new FragEditPreferences();
            fb.setArguments(fargs.bundle);
            ft.replace(R.id.content_frame, fb);
            ft.addToBackStack("pc_ttspreferences_ttsspeak");
            ft.commit();

            return true;
        }

        if (preference.getKey().equals("pc_notifications")){

            Fargs fargs = Fargs.create();
            fargs.setPreferencesResource(R.xml.preferences_notifications);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            FragEditPreferences fb = new FragEditPreferences();
            fb.setArguments(fargs.bundle);
            ft.replace(R.id.content_frame, fb);
            ft.addToBackStack("pc_notifications");
            ft.commit();

            return true;
        }

        if (preference.getKey().equals("pc_toasts")){

            Fargs fargs = Fargs.create();
            fargs.setPreferencesResource(R.xml.preferences_toasts);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            FragEditPreferences fb = new FragEditPreferences();
            fb.setArguments(fargs.bundle);
            ft.replace(R.id.content_frame, fb);
            ft.addToBackStack("pc_toasts");
            ft.commit();

            return true;
        }


        if (preference.getKey().equals("pc_backuprestore")){
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            FragActionBackup fb = new FragActionBackup();
            ft.replace(R.id.content_frame, fb);
            ft.addToBackStack("pc_backuprestore");
            ft.commit();

            return true;
        }


        return false;
    }
}