package com.cratorsoft.android.frag;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.cratorsoft.android.aamain.Action;
import com.cratorsoft.android.aamain.BuildManager;
import com.cratorsoft.android.aamain.Fargs;
import com.cratorsoft.android.dialog.DlgInfo;
import com.cratorsoft.android.dialog.DlgManageChannel;
import com.cratorsoft.android.dialog.DlgPromptFilter;
import com.cratorsoft.android.drawer.DrawerDisplayer;
import com.cratorsoft.android.taskmanager.BusyAsyncTask;
import com.cratorsoft.android.taskmanager.BusyTaskFragment;
import com.earthflare.android.ircradio.ActChatLog;
import com.earthflare.android.ircradio.AsyncListChannels;
import com.earthflare.android.ircradio.Channel;
import com.earthflare.android.ircradio.Globo;
import com.earthflare.android.ircradio.ListChannelsAdapter;
import com.earthflare.android.ircradio.NavItem;
import com.earthflare.android.ircradio.PromptFilter;
import com.earthflare.android.ircradio.R;
import com.earthflare.android.ircradio.RadioBot;
import com.earthflare.android.logmanager.LogManager;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.lang.ref.WeakReference;
import java.util.Date;

@EFragment
public class FragCommandListChannels extends BusyTaskFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    int mBusyStyle = com.cratorsoft.android.taskmanager.BUSY.NONMODAL_CONTENT;
    private boolean mInitialized = false;
    private Handler mFragHandler;

    // Misc
    static long THREAD_ID;

    // Data
    String[] servernames = new String[0];
    long[] serverids = new long[0];

    // Views
    @ViewById(R.id.buttonlist)
    Button vButtonList;

    @ViewById(R.id.spinner_connectedaccount)
    Spinner vSpinnerConnecteAccount;

    @ViewById(R.id.listChannelsView)
    ListView vListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
        this.parseIntent();
        mFragHandler = new FragHandler(this);
        loadRefreshDataTask();
        loadCreateDataTask();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.frag_commandlistchannels, container, false);
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
        Globo.handlerListChannels = mFragHandler;
        Globo.actListChannelsVisible = true;
    }

    @Override
    public void onPause() {
        THREAD_ID = 0;
        Globo.actListChannelsVisible = false;
        Globo.handlerListChannels = null;
        super.onPause();
        if (mInitialized) {
            stashData();
        }
    }

    // ======================== Fragment Data Lifecycle ====
    
    public void parseIntent(){
        
    }
    
    private void configViews() {

        vButtonList.setOnClickListener(this);

        vListView.setOnCreateContextMenuListener(
                new View.OnCreateContextMenuListener() {

                    public void onCreateContextMenu(ContextMenu menu, View view,
                                                    ContextMenu.ContextMenuInfo menuInfo) {
                        AdapterView.AdapterContextMenuInfo mi =
                                (AdapterView.AdapterContextMenuInfo) menuInfo;
                        menu.add(0, 0, 0, R.string.ctx_gettopic);
                        menu.add(0, 1, 0, R.string.ctx_joinchannel);
                        menu.add(0, 2, 0, R.string.ctx_addchannel);
                        menu.add(0, 3, 0, R.string.ctx_viewmessages);
                    }
                });

    }

    private void configFrag() {

    }

    private void stashData() {

    }

    private void unstashData() {
        initSpinnerArrays();
        attachAdapters();
    }

    private void initAllData() {
        initSpinnerArrays();
        attachAdapters();
    }

    // ======================== Fragment Functions =====
    
    private void setTitle(){
        getDrawerDisplayer().setFragTitle(getString(R.string.label_actlistchannels), "");
    }

    private void beginSynchOperation() {

        ListChannelsAdapter adapter = (ListChannelsAdapter) vListView.getAdapter();
        if (!adapter.needupdate()) {
            return;
        }

        AsyncListChannels aslc = new AsyncListChannels(this.getActivity(), adapter, getSpinnerNetwork());
        Bundle bundle = new Bundle();
        bundle.putString("action", "synch");
        aslc.execute(bundle);
    }

    private long getSpinnerNetwork() {

        if (serverids.length > 0) {
            int pos = vSpinnerConnecteAccount.getSelectedItemPosition();

            //if (Globo.botManager.ircBots.containsKey(serverids[pos])){
            return serverids[pos];

            //}

        }

        return 0;
    }

    private String getSpinnerName() {
        if (serverids.length > 0) {
            int pos = vSpinnerConnecteAccount.getSelectedItemPosition();
            return servernames[pos];
        }
        return "";
    }

    private void listChans() {

        long network = getSpinnerNetwork();
        if (network != 0) {
            if (Globo.botManager.ircBots.containsKey(network)) {
                Globo.botManager.ircBots.get(network).startListChannels("");
            } else {
                Fargs fargs = Fargs.create().setTitle(getSpinnerName()).setMessage(getString(R.string.alert_message_notconnected));
                DlgInfo dlg = DlgInfo.newInstance(fargs.bundle);
                dlg.show(this.getFragmentManager(),"dlgnotconnected");
            }
        }


    }

    private void beginFilterOperation(String action, Message msg) {
        if (getSpinnerNetwork() == 0) {
            return;
        }
        ListChannelsAdapter adapter = (ListChannelsAdapter) vListView.getAdapter();
        AsyncListChannels aslc = new AsyncListChannels(this.getActivity(), adapter, getSpinnerNetwork());
        Bundle bundle = new Bundle();
        bundle.putString("action", action);
        bundle.putInt("min", msg.getData().getInt("min"));
        bundle.putInt("max", msg.getData().getInt("max"));
        bundle.putString("filtertext", msg.getData().getString("filtertext"));
        aslc.execute(bundle);
    }

    private void joinChannel(String channel) {

        long accountId = this.getSpinnerNetwork();
        //check if server connected
        if (Globo.botManager.ircBots.containsKey(accountId)) {
            RadioBot rb = Globo.botManager.ircBots.get(accountId);
            rb.joinChannel(channel);
        } else {
            Fargs fargs = Fargs.create()
                    .setTitle(getSpinnerName())
                    .setMessage(getString(R.string.alert_message_notconnected));
            DlgInfo dlg = DlgInfo.newInstance(fargs.bundle);
            dlg.show(this.getFragmentManager(),"dlginfo");
        }

    }

    private void viewMessages(String channelname) {
        joinChannel(channelname);
        Long network = getSpinnerNetwork();
        String accountname = getSpinnerName();


        FragChatLog_ frag = new FragChatLog_();

        Fargs fargs = new Fargs();
        fargs.setAccountName(accountname);
        fargs.setNetwork(network);
        fargs.setChannelName(channelname);
        fargs.setActtype(NavItem.CHANNEL);


        frag.setArguments(fargs.bundle);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        ft.add(R.id.content_frame, frag);
        ft.addToBackStack("chatlog");
        ft.commit();
    }



    // ======================== Fragment Actions =====

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        if (((DrawerDisplayer) this.getActivity()).drawerIsClosed()) {

            if (menu.findItem(R.id.ACTION_SORTUSERS) == null) {
                menu.add(0, R.id.ACTION_SORTUSERS, 0, getString(R.string.button_sortusers)).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            }

            if (menu.findItem(R.id.ACTION_SORTCHANNELS) == null) {
                menu.add(0, R.id.ACTION_SORTCHANNELS, 0, getString(R.string.button_sortchannels)).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            }

            if (menu.findItem(R.id.ACTION_FILTERLIST) == null) {
                menu.add(0, R.id.ACTION_FILTERLIST, 0, getString(R.string.button_filterlist)).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            }

        }

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (isBusy()){return true;}

        int itemId = item.getItemId();
        if (itemId == R.id.ACTION_SORTUSERS) {
            beginSortOperation("sortusers");
            return true;
        } else if (itemId == R.id.ACTION_SORTCHANNELS){
            beginSortOperation("sortchannels");
            return true;
        } else if (itemId == R.id.ACTION_FILTERLIST){
            promptfilter();
            return true;
        } else{
            return super.onOptionsItemSelected(item);
        }
    }

    void promptfilter() {
        DlgPromptFilter dlg = DlgPromptFilter.newInstance();
        dlg.show(this.getFragmentManager(),"dlgpromptfilter");
    }

    void beginSortOperation(String action) {
        ListChannelsAdapter adapter = (ListChannelsAdapter) vListView.getAdapter();
        if (getSpinnerNetwork() == 0) {
            return;
        }
        AsyncListChannels aslc = new AsyncListChannels(this.getActivity(), adapter, getSpinnerNetwork());
        Bundle bundle = new Bundle();
        bundle.putString("action", action);
        aslc.execute(bundle);
    }
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
                    launchRefreshThread();
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

    private void launchRefreshThread() {

        this.THREAD_ID = new Date().getTime();


        //refresh thread check for new channel info
        new Thread(new Runnable() {
            synchronized public void run() {
                long launchID = THREAD_ID;
                Runnable action = new Runnable() {

                    @Override
                    public void run() {
                        beginSynchOperation();
                    }

                };

                while (launchID == THREAD_ID) {

                    try {
                        if (FragCommandListChannels.this.isResumed()) {
                            FragCommandListChannels.this.getActivity().runOnUiThread(action);
                        }
                    } catch (Exception e1) {
                        launchID = 0;
                    }

                    try {
                        wait(3000);
                    } catch (InterruptedException e) {
                    }
                }

            }
        }).start();
    }

    // ======================== Fragment Attach/Populate =====

    protected void initSpinnerArrays() {

        synchronized (LogManager.serverLogMap) {

            int size = LogManager.serverLogMap.size();
            serverids = new long[size];
            servernames = new String[size];
            int counter = 0;
            for (long key : LogManager.serverLogMap.keySet()) {
                serverids[counter] = key;
                servernames[counter] = LogManager.serverLogMap.get(key).accountName;
                counter++;
            }

        }


    }

    public void attachAdapters() {

        //populate network spinner

        ArrayAdapter<String> spinneradapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, servernames);
        spinneradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vSpinnerConnecteAccount.setAdapter(spinneradapter);
        vSpinnerConnecteAccount.setOnItemSelectedListener(this);

        //populate list
        ListChannelsAdapter listadapter = new ListChannelsAdapter(this.getActivity());
        vListView.setAdapter(listadapter);

        boolean populated = false;
        if (Globo.listChannelsNetwork != 0) {
            int counter = 0;
            for (long id : this.serverids) {
                if (id == Globo.listChannelsNetwork) {
                    listadapter.setList(serverids[counter]);
                    populated = true;
                    vSpinnerConnecteAccount.setSelection(counter);
                    break;
                }
                counter++;
            }
        }

        //set active spinner network
        if (serverids.length > 0 && !populated) {
            Globo.listChannelsNetwork = serverids[0];
            listadapter.setList(serverids[0]);
            vSpinnerConnecteAccount.setSelection(0);
        }

    }

    //========================= Stash Data =====================
    
    //========================= Fragment Listeners ====================

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.buttonlist:
                ListChannelsAdapter adapter = (ListChannelsAdapter) vListView.getAdapter();
                adapter.clean();
                listChans();
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {}

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                               long arg3) {
        // TODO Auto-generated method stub
        if (arg0.getId() == R.id.spinner_connectedaccount) {
            if (this.getSpinnerNetwork() != 0) {

                synchronized (LogManager.serverLogMap.get(getSpinnerNetwork()).channels) {
                    ListChannelsAdapter adapter = (ListChannelsAdapter) vListView.getAdapter();
                    synchronized (adapter) {
                        Globo.listChannelsNetwork = getSpinnerNetwork();
                        adapter.setList(Globo.listChannelsNetwork);
                    }
                }
            }
        }
    }

    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo mi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        ListChannelsAdapter adapter = (ListChannelsAdapter) vListView.getAdapter();
        Channel chan = adapter.getChannel(mi.position);
        switch (item.getItemId()) {
            case 0:
                //get topic
                Fargs fargs = Fargs.create()
                        .setTitle(chan.name)
                        .setMessage(chan.topic);
                DlgInfo dlg = DlgInfo.newInstance(fargs.bundle);
                dlg.show(this.getFragmentManager(),"dlginfo");
                break;
            case 1:
                //join channel
                joinChannel(chan.name);
                break;
            case 2:
                //add channel
                DlgManageChannel dlgManageChannel = DlgManageChannel.newManageFromCommandListChannels(getSpinnerNetwork(),chan.name);
                if (dlgManageChannel != null){
                    dlgManageChannel.show(this.getFragmentManager(), "dlgmanagechannel");
                }
                break;
            case 3:
                //view messages
                viewMessages(chan.name);
            default:
                return super.onContextItemSelected(item);
        }
        return true;
    }

    //========================= Handler ======================

    private static class FragHandler extends Handler {
        private WeakReference<FragCommandListChannels> mTarget;

        FragHandler(FragCommandListChannels target) {
            mTarget = new WeakReference<FragCommandListChannels>(target);
        }

        @Override
        public void handleMessage(Message msg) {

            FragCommandListChannels frag = mTarget.get();
            if (frag == null) {
                return;
            }

            String action = msg.getData().getString("action");

            if (action.equals("filter")) {
                frag.beginFilterOperation("filter", msg);
            }
        }

    }

}
