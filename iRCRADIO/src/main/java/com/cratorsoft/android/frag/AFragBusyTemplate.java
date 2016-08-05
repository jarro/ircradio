package com.cratorsoft.android.frag;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cratorsoft.android.aamain.BuildManager;
import com.cratorsoft.android.taskmanager.BusyAsyncTask;
import com.cratorsoft.android.taskmanager.BusyTaskFragment;
import com.earthflare.android.ircradio.R;

public class AFragBusyTemplate extends BusyTaskFragment {

    int mBusyStyle = com.cratorsoft.android.taskmanager.BUSY.NONMODAL_CONTENT;
    private boolean mInitialized = false;

    // Misc

    // Data
    

    // Views

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.parseIntent();
        loadRefreshDataTask();
        loadCreateDataTask();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.frag_drawer, container, false);
        return v;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        configFrag();
    }

    @Override
    public void onStart() {
        super.onStart();
        // views bound
    }

    @Override
    public void onResume() {
        setTitle();
        if (mInitialized) {
            loadRefreshDataTask();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mInitialized) {
            stashData();
        }
    }

    // ======================== Fragment Data Lifecycle ====
    
    public void parseIntent(){
        
    }
    
    private void configViews() {

    }

    private void configFrag() {

    }

    private void stashData() {

    }

    private void unstashData() {
        attachAdapters();
    }

    private void initAllData() {
        attachAdapters();
    }

    // ======================== Fragment Functions =====
    
    private void setTitle(){
        
    }

    // ======================== Fragment Actions =====

    // ======================== Fragment Background Tasks =====

    public void loadCreateDataTask() {

        new BusyAsyncTask<Boolean>(this, mBusyStyle, true) {

            protected Boolean doInBackground() {

                try {

                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    BuildManager.INSTANCE.sendCaughtException(e);
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {

                    // set data vars

                    initAllData();
                    configViews();
                    mInitialized = true;
                } else {
                    abort("DB Error");
                }
            }
        }.execute();
    }

    public void loadRefreshDataTask() {

        cleanupRefreshDBConnections();
        new BusyAsyncTask<Boolean>(this, mBusyStyle, true) {

            protected Boolean doInBackground() {

                try {

                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    BuildManager.INSTANCE.sendCaughtException(e);
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {

                    // set data vars

                    if (mInitialized) {
                        unstashData();
                        configViews();
                    }

                } else {
                    abort("DB Error");
                }
            }
        }.execute();
    }

    public void dummyTask() {
        
        new BusyAsyncTask<Boolean>(this, mBusyStyle, true) {

            protected Boolean doInBackground() {

                try {

                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    BuildManager.INSTANCE.sendCaughtException(e);
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {

                    

                } else {
                    abort("Error");
                }
            }
        }.execute();
    }

    // ======================== Fragment Attach/Populate =====


    public void attachAdapters() {

    }

    //========================= Stash Data =====================
    
    //========================= Fragment Listeners ====================
    
    
    
}
