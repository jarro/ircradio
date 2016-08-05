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
import com.cratorsoft.android.dialog.DlgInfo;
import com.cratorsoft.android.taskmanager.BUSY;
import com.cratorsoft.android.taskmanager.BusyAsyncTask;
import com.cratorsoft.android.taskmanager.BusyTaskFragment;
import com.earthflare.android.ircradio.Globo;
import com.earthflare.android.ircradio.NavItem;
import com.earthflare.android.ircradio.R;
import com.earthflare.android.notifications.ListNoticeAdapter;
import com.earthflare.android.notifications.Notice;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.lang.ref.WeakReference;

@EFragment
public class FragListNotifications extends BusyTaskFragment implements AdapterView.OnItemClickListener {

    int mBusyStyle = BUSY.NONMODAL_CONTENT;
    private boolean mInitialized = false;
    private Handler mFragHandler;

    // Misc

    // Data

    // Views
    @ViewById(R.id.noticeListView)
    ListView vListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.parseIntent();
        mFragHandler = new FragHandler(this);
        loadRefreshDataTask();
        loadCreateDataTask();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.frag_listnotifications, container, false);
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
        Globo.handlerNotice = mFragHandler;
        Globo.actNoticeVisible = true;
    }

    @Override
    public void onPause() {
        Globo.actNoticeVisible = false;
        Globo.handlerNotice = null;
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

        vListView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle(R.string.ctx_title_notifications);
                menu.add(0, 0, 0, R.string.ctx_viewmessage);
            }
        });


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
        getDrawerDisplayer().setFragTitle(getString(R.string.label_notifications), "");
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

        ListNoticeAdapter adapter = new ListNoticeAdapter(this.getActivity());
        vListView.setAdapter(adapter);

    }

    //========================= Stash Data =====================
    
    //========================= Fragment Listeners ====================

    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();


        switch (item.getItemId()) {
            case 0:
                //view messages
                ListNoticeAdapter adapter = (ListNoticeAdapter) vListView.getAdapter();
                Notice note = adapter.getItem(info.position);
                Bundle data = new Bundle();
                data.putString("title", note.sender);
                data.putString("message", note.message);
                DlgInfo dlg = DlgInfo.newInstance(data);
                dlg.show(this.getFragmentManager(),"dlgnotification");
                break;
        }


        return true;

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

        ListNoticeAdapter adapter = (ListNoticeAdapter) vListView.getAdapter();
        Notice note = adapter.getItem(arg2);

        Long network = note.network;
        String channelname = note.channel;
        String accountname = note.accountname;

        FragChatLog_ frag = new FragChatLog_();

        Fargs fargs = new Fargs();
        fargs.setAccountName(accountname);
        fargs.setNetwork(network);
        fargs.setChannelName(channelname);
        fargs.setActtype(NavItem.CHANNEL);
        fargs.setUid(note.uid);

        frag.setArguments(fargs.bundle);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        ft.add(R.id.content_frame, frag);
        ft.addToBackStack("chatlog");
        ft.commit();

    }

    //========================= Handler ======================

    private static class FragHandler extends Handler {
        private WeakReference<FragListNotifications> mTarget;

        FragHandler(FragListNotifications target) {
            mTarget = new WeakReference<FragListNotifications>(target);
        }

        @Override
        public void handleMessage(Message msg) {

            FragListNotifications frag = mTarget.get();
            if (frag == null) {
                return;
            }

            String action = msg.getData().getString("action");

            if (action == "refresh") {
                return;
            }

            if (action == "add"){
                ListNoticeAdapter adapter = (ListNoticeAdapter) frag.vListView.getAdapter();
                Notice note = (Notice)msg.getData().getSerializable("notice");
                adapter.addToList(note);
                return;
            }
        }

    }
    
}
