package com.earthflare.android.ircradio;


import java.util.Date;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.earthflare.android.logmanager.LogManager;

public class ActListChannels extends TemplateListChannels implements OnClickListener, OnItemSelectedListener {

    String[] servernames = new String[0];
    long[] serverids = new long[0];
    ListChannelsAdapter adapter;


    long threadID;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_listchannels);
        initDrawer();

    }

    private void launchRefreshThread() {

        this.threadID = new Date().getTime();


        //refresh thread check for new channel info
        new Thread(new Runnable() {
            synchronized public void run() {
                long launchID = threadID;
                Runnable action = new Runnable() {

                    @Override
                    public void run() {
                        beginSynchOperation();
                    }

                };

                while (launchID == threadID) {

                    try {
                        ActListChannels.this.runOnUiThread(action);
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

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.buttonlist:
                adapter.clean();
                listChans();
                break;
        }

    }


    void promptfilter() {
        PromptFilter pf = new PromptFilter(this);
        pf.show();

    }

    private long getSpinnerNetwork() {

        if (serverids.length > 0) {
            int pos = ((Spinner) this.findViewById(R.id.spinner_connectedaccount)).getSelectedItemPosition();

            //if (Globo.botManager.ircBots.containsKey(serverids[pos])){
            return serverids[pos];

            //}

        }

        return 0;
    }


    private String getSpinnerName() {
        if (serverids.length > 0) {
            int pos = ((Spinner) this.findViewById(R.id.spinner_connectedaccount)).getSelectedItemPosition();
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
                DialogInfo di = new DialogInfo(this, getSpinnerName(), this.getString(R.string.alert_message_notconnected));
                di.show();
            }
        }


    }


    public void initializeButtons() {

        this.findViewById(R.id.buttonlist).setOnClickListener(this);

        //populate spinner
        Spinner spin = (Spinner) this.findViewById(R.id.spinner_connectedaccount);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, servernames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
        spin.setOnItemSelectedListener(this);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        GloboUtil.claimaudiostream(this);
        this.initSpinnerArrays();
        this.initializeButtons();
        this.populateList();
        this.createContextMenu();

        Globo.handlerListChannels = this.myHandler;
        Globo.actListChannelsVisible = true;

        this.launchRefreshThread();

    }


    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        Globo.actListChannelsVisible = false;
        threadID = 0;
    }


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


    private void populateList() {

        boolean populated = false;
        ListView lv = (ListView) this.findViewById(R.id.listChannelsView);

        adapter = new ListChannelsAdapter(this);
        lv.setAdapter(adapter);
        Spinner spin = (Spinner) this.findViewById(R.id.spinner_connectedaccount);


        //if globo listchannel network in list recycle list

        if (Globo.listChannelsNetwork != 0) {
            int counter = 0;
            for (long id : this.serverids) {
                if (id == Globo.listChannelsNetwork) {
                    adapter.setList(serverids[counter]);
                    populated = true;
                    spin.setSelection(counter);
                    break;
                }
                counter++;
            }
        }


        if (serverids.length > 0 && !populated) {
            Globo.listChannelsNetwork = serverids[0];
            adapter.setList(serverids[0]);
            spin.setSelection(0);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                               long arg3) {
        // TODO Auto-generated method stub
        if (arg0.getId() == R.id.spinner_connectedaccount) {
            if (this.getSpinnerNetwork() != 0) {

                synchronized (LogManager.serverLogMap.get(getSpinnerNetwork()).channels) {
                    synchronized (adapter) {
                        Globo.listChannelsNetwork = getSpinnerNetwork();
                        adapter.setList(Globo.listChannelsNetwork);
                    }
                }
            }
        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

    public Handler myHandler = new Handler() {

        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);

            String action = msg.getData().getString("action");

            if (action.equals("filter")) {
                beginFilterOperation("filter", msg);
            }

        }
    };

    private void beginFilterOperation(String action, Message msg) {
        if (getSpinnerNetwork() == 0) {
            return;
        }

        AsyncListChannels aslc = new AsyncListChannels(this, adapter, getSpinnerNetwork());
        Bundle bundle = new Bundle();
        bundle.putString("action", action);
        bundle.putInt("min", msg.getData().getInt("min"));
        bundle.putInt("max", msg.getData().getInt("max"));
        bundle.putString("filtertext", msg.getData().getString("filtertext"));
        aslc.execute(bundle);
    }


    private void beginSynchOperation() {

        if (!adapter.needupdate()) {
            return;
        }

        AsyncListChannels aslc = new AsyncListChannels(this, adapter, getSpinnerNetwork());
        Bundle bundle = new Bundle();
        bundle.putString("action", "synch");
        aslc.execute(bundle);
    }


    void beginSortOperation(String action) {
        if (getSpinnerNetwork() == 0) {
            return;
        }
        AsyncListChannels aslc = new AsyncListChannels(this, adapter, getSpinnerNetwork());
        Bundle bundle = new Bundle();
        bundle.putString("action", action);
        aslc.execute(bundle);
    }

    private void joinChannel(String channel) {

        long accountId = this.getSpinnerNetwork();
        //check if server connected
        if (Globo.botManager.ircBots.containsKey(accountId)) {
            RadioBot rb = Globo.botManager.ircBots.get(accountId);
            rb.joinChannel(channel);
        } else {
            DialogInfo di = new DialogInfo(this, getSpinnerName(), this.getString(R.string.alert_message_notconnected));
            di.show();
        }

    }

    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo mi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Channel chan = adapter.getChannel(mi.position);
        switch (item.getItemId()) {
            case 0:
                //get topic
                DialogShow di = new DialogShow(this, chan.name, chan.topic);
                di.show();
                break;
            case 1:
                //join channel
                joinChannel(chan.name);
                break;
            case 2:
                //add channel
                DialogManagerAccountChannel dmac = new DialogManagerAccountChannel(this, getSpinnerNetwork());
                dmac.promptAddFromList(chan.name);
                break;
            case 3:
                //view messages
                viewMessages(chan.name);
            default:
                return super.onContextItemSelected(item);
        }
        return true;
    }


    private void createContextMenu() {

        ListView listView = (ListView) this.findViewById(R.id.listChannelsView);
        listView
                .setOnCreateContextMenuListener(
                        new View.OnCreateContextMenuListener() {

                            public void onCreateContextMenu(ContextMenu menu, View view,
                                                            ContextMenu.ContextMenuInfo menuInfo) {
                                AdapterContextMenuInfo mi =
                                        (AdapterContextMenuInfo) menuInfo;
                                menu.add(0, 0, 0, R.string.ctx_gettopic);
                                menu.add(0, 1, 0, R.string.ctx_joinchannel);
                                menu.add(0, 2, 0, R.string.ctx_addchannel);
                                menu.add(0, 3, 0, R.string.ctx_viewmessages);
                            }
                        });
    }


    private void viewMessages(String channel) {
        joinChannel(channel);
        Long network = getSpinnerNetwork();
        String accountname = getSpinnerName();
        Intent launchChannel = new Intent(this, ActChatLog.class);
        launchChannel.putExtra("accountname", accountname);
        launchChannel.putExtra("network", network);
        launchChannel.putExtra("channel", channel);
        launchChannel.putExtra("acttype", "channel");
        this.startActivity(launchChannel);
    }


    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();

    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

    }
}
    
