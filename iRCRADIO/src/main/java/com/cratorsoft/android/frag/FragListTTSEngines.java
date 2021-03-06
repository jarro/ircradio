package com.cratorsoft.android.frag;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.cratorsoft.android.aamain.BuildManager;
import com.cratorsoft.android.aamain.Fargs;
import com.cratorsoft.android.adapter.TTSEngineAdapter;
import com.cratorsoft.android.dialog.DlgInfo;
import com.cratorsoft.android.dialog.DlgTTSEngine;
import com.cratorsoft.android.taskmanager.BUSY;
import com.cratorsoft.android.taskmanager.BusyAsyncTask;
import com.cratorsoft.android.taskmanager.BusyTaskFragment;
import com.cratorsoft.android.ttslanguage.TTSEngine;
import com.earthflare.android.ircradio.Globo;
import com.earthflare.android.ircradio.NavItem;
import com.earthflare.android.ircradio.R;
import com.earthflare.android.notifications.ListNoticeAdapter;
import com.earthflare.android.notifications.Notice;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.lang.ref.WeakReference;

@EFragment
public class FragListTTSEngines extends BusyTaskFragment implements AdapterView.OnItemClickListener {

    int mBusyStyle = BUSY.NONMODAL_CONTENT;
    private boolean mInitialized = false;


    // Misc

    // Data

    // Views
    @ViewById(R.id.noticeListView)
    ListView vListView;

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

        View v = inflater.inflate(R.layout.frag_listttsengines, container, false);
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

        vListView.setOnItemClickListener(this);


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
        getDrawerDisplayer().setFragTitle(getString(R.string.label_tts), "");
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



    // ======================== Fragment Attach/Populate =====


    public void attachAdapters() {

        TTSEngineAdapter.Adapter adapter =  TTSEngineAdapter.INSTANCE.getAdapter(this.getActivity());
        vListView.setAdapter(adapter);

    }

    //========================= Stash Data =====================
    
    //========================= Fragment Listeners ====================



    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {


        TTSEngine engine = (TTSEngine) arg0.getAdapter().getItem(arg2);

        DlgTTSEngine dialog = DlgTTSEngine.newInstance(engine);
        dialog.show(this.getFragmentManager(), "dialogtts");

    }


    
}
