package com.cratorsoft.android.frag;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.cratorsoft.android.aamain.Action;
import com.cratorsoft.android.aamain.BuildManager;
import com.cratorsoft.android.aamain.Fargs;
import com.cratorsoft.android.adapter.ExpandableListORMAccountAdapter;
import com.cratorsoft.android.db.DBDelete;
import com.cratorsoft.android.dbtable.AccountData;
import com.cratorsoft.android.dbtable.ChannelData;
import com.cratorsoft.android.dialog.DlgManageChannel;
import com.cratorsoft.android.drawer.DrawerDisplayer;
import com.cratorsoft.android.taskmanager.BUSY;
import com.cratorsoft.android.taskmanager.BusyAsyncTask;
import com.cratorsoft.android.taskmanager.BusyTaskFragment;
import com.cratorsoft.android.toaster.Toaster;
import com.earthflare.android.ircradio.ActChatLog;
import com.earthflare.android.ircradio.Globo;
import com.earthflare.android.ircradio.IrcRadioService;
import com.earthflare.android.ircradio.NavItem;
import com.earthflare.android.ircradio.R;
import com.earthflare.android.logmanager.LogManager;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.lang.ref.WeakReference;

@EFragment
public class FragAccountList extends BusyTaskFragment implements ExpandableListView.OnChildClickListener {

    int mBusyStyle = BUSY.NONMODAL_CONTENT;
    private boolean mInitialized = false;
    private Handler mFragHandler;


    // Misc

    // Data
    ExpandableListORMAccountAdapter.AdapterBundle mAdapterBundle;

    // Views
    @ViewById(R.id.accountListView)
    ExpandableListView vListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
        mFragHandler = new FragHandler(this);
        this.parseIntent();
        loadRefreshDataTask();
        loadCreateDataTask();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.frag_accountlist, container, false);
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
        Globo.handlerAccountList = mFragHandler;
        Globo.actAccountListVisible = true;
    }



    @Override
    public void onPause() {
        Globo.actAccountListVisible = false;
        Globo.handlerAccountList = null;
        super.onPause();
        if (mInitialized) {
            stashData();
        }
    }

    // ======================== Fragment Data Lifecycle ====
    
    public void parseIntent(){
        
    }
    
    private void configViews() {


        vListView.setOnChildClickListener(this);
        vListView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {

            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

                ExpandableListView.ExpandableListContextMenuInfo elcm = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;

                int type = ExpandableListView.getPackedPositionType(elcm.packedPosition);
                if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {

                    int groupPosition = ExpandableListView.getPackedPositionGroup(elcm.packedPosition);
                    int childPosition = ExpandableListView.getPackedPositionChild(elcm.packedPosition);

                    menu.setHeaderTitle(R.string.ctx_title_channelmenu);
                    menu.add(0, 3, 0, R.string.ctx_viewmessages);
                    menu.add(0, 0, 1, R.string.ctx_part);
                    menu.add(0, 1, 2, R.string.ctx_edit);
                    menu.add(0, 2, 3, R.string.ctx_delete);
                }

                else {
                    menu.setHeaderTitle(R.string.ctx_title_servermenu);
                    menu.add(0, 0, 0, R.string.ctx_addchannel);
                    menu.add(0, 1, 0, R.string.ctx_connect);
                    menu.add(0, 2, 0, R.string.ctx_disconnect);
                    menu.add(0, 3, 0, R.string.ctx_edit);
                    menu.add(0, 4, 0, R.string.ctx_delete);
                }}
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
        getDrawerDisplayer().setFragTitle(getString(R.string.label_accounts), "");
    }

    private void notifyListView(){
        ((ExpandableListORMAccountAdapter)vListView.getExpandableListAdapter()).notifyDataSetChanged();
    }

    private Intent connectIntentServer (AccountData accountData) {

        Intent mIntent = new Intent(this.getActivity(), IrcRadioService.class);

        mIntent.putExtra("server",accountData.server.toLowerCase());
        mIntent.putExtra("nick",accountData.nick);
        mIntent.putExtra("accountname",accountData.accountname);
        mIntent.putExtra("serverpass",accountData.serverpass);
        mIntent.putExtra("port",accountData.port);
        mIntent.putExtra("auth",accountData.auth);
        mIntent.putExtra("accountid", accountData.id);
        mIntent.putExtra("action","connect");

        mIntent.putExtra("autoidentify", Boolean.valueOf(accountData.autoidentify));
        mIntent.putExtra("nickserv", accountData.nickserv);
        mIntent.putExtra("nickpass", accountData.nickpass);
        mIntent.putExtra("perform", accountData.perform);
        mIntent.putExtra("encodingoverride", Boolean.valueOf(accountData.encodingoverride));
        mIntent.putExtra("encodingsend", accountData.encodingsend);
        mIntent.putExtra("encodingreceive", accountData.encodingreceive );
        mIntent.putExtra("encodingserver", accountData.encodingserver );
        mIntent.putExtra("reconnectinterval", accountData.reconnectinterval );
        mIntent.putExtra("reconnectretries", accountData.reconnectretries);

        mIntent.putExtra("channel", "");
        mIntent.putExtra("channelpass", "");
        mIntent.putExtra("autojoin", "0");
        mIntent.putExtra("language", "0");
        mIntent.putExtra("ttsprefixes", "");

        mIntent.putExtra("guesscharset", Boolean.valueOf(accountData.guesscharset));
        mIntent.putExtra("reconnect", Boolean.valueOf(accountData.reconnect));
        mIntent.putExtra("ssl", Boolean.valueOf(accountData.ssl));

        return mIntent;
    }

    private Intent connectIntentChannel (Intent mIntent, ChannelData channelData) {


        mIntent.putExtra("channel",channelData.channel.toLowerCase());
        mIntent.putExtra("channelpass",channelData.channelpass);
        mIntent.putExtra("joinleave",channelData.joinleave);
        mIntent.putExtra("language", channelData.language);
        mIntent.putExtra("ttsprefixes", channelData.ttsprefixes);

        return mIntent;

    }

    private void confirmDeleteAccount(final AccountData accountData){
        new MaterialDialog.Builder(this.getActivity())
                .title(R.string.alert_title_confirmdeleteaccount)
                .content( getString(R.string.alert_message_confirmdeleteaccount) + " " + accountData.accountname + getString(R.string.char_questionmark))
                .positiveText(R.string.button_ok)
                .negativeText(R.string.button_cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        deleteAccount(accountData);
                    }
                })
                .show();
    }

    private void deleteAccount(AccountData accountData){

        DBDelete.deleteAccount(accountData.id);
        //disconnect bot if connected
        Globo.botManager.disconnectAccount(accountData.id);
        loadRefreshDataTask();
    }

    private void confirmDeleteChannel(final ChannelData channelData){

        new MaterialDialog.Builder(this.getActivity())
                .title(R.string.alert_title_confirmdeletechannel)
                .content(getString(R.string.alert_message_confirmdeletechannel) + " " + channelData.channel + getString(R.string.char_questionmark))
                .positiveText(R.string.button_ok)
                .negativeText(R.string.button_cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        deleteChannel(channelData);
                    }
                })
                .show();

    }

    private void deleteChannel(ChannelData chanelData){
        DBDelete.deleteChannel(chanelData.id);
        loadRefreshDataTask();
    }

    private void viewMessages(long network,  String accountname, String channelname) {

        NavItem next = LogManager.getActiveLog();
        if (next.acttype.equals("none")) {
            Toaster.toast(this.getActivity(), this.getString(R.string.error_nochatlogs) );
        } else {

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

    }

    private void editAccount(long accountid) {


        Globo.tabaccount = 0;
        FragEditAccount frag = new FragEditAccount();
        Fargs fargs = Fargs.create().setAction(Action.EDIT).setAccount(accountid);
        frag.setArguments(fargs.bundle);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, frag);
        ft.addToBackStack("editaccount");
        ft.commit();


    }



    // ======================== Fragment Actions =====

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        if (((DrawerDisplayer) this.getActivity()).drawerIsClosed()) {

            if (menu.findItem(R.id.ACTION_ADD ) == null) {
                //menu.add(0, R.id.ACTION_ADD, 0, "Add").setIcon(android.R.drawable.ic_menu_add).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                menu.add(0, R.id.ACTION_ADD, 0, getString(R.string.menu_addaccount)).setIcon(R.drawable.ic_add_white_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            }
        }

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (isBusy()){return true;}

        int itemId = item.getItemId();
        if (itemId == R.id.ACTION_ADD) {
            clickActionAdd();
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }

    private void clickActionAdd(){

        Globo.tabaccount = 0;
        FragEditAccount frag = new FragEditAccount();
        Fargs fargs = Fargs.create().setAction(Action.INSERT);
        frag.setArguments(fargs.bundle);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, frag);
        ft.addToBackStack("insertaccount");
        ft.commit();

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

            ExpandableListORMAccountAdapter.AdapterBundle adapterBundle;

            protected Boolean doInBackground() {

                try {
                    adapterBundle = ExpandableListORMAccountAdapter.AdapterBundle.getAdapterBundle();
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
                    mAdapterBundle = adapterBundle;

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
        ExpandableListORMAccountAdapter adapter = new ExpandableListORMAccountAdapter(getActivity());
        adapter.setData(mAdapterBundle);
        vListView.setAdapter(adapter);
    }



    //========================= Stash Data =====================
    
    //========================= Fragment Listeners ====================

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Intent mIntent;

        ExpandableListORMAccountAdapter adapter = (ExpandableListORMAccountAdapter) vListView.getExpandableListAdapter();
        ChannelData channelData = (ChannelData) adapter.getChild(groupPosition, childPosition);
        AccountData accountData = (AccountData) adapter.getGroup(groupPosition);

        mIntent = connectIntentServer(accountData);
        mIntent = connectIntentChannel(mIntent, channelData);

        Globo.setTTSServer(accountData.id, accountData.language);
        Globo.setTTSChannel(channelData.accountid, channelData.channel.toLowerCase(),  channelData.language );

        this.getActivity().startService(mIntent);

        return true;
    }

    public boolean onContextItemSelected(MenuItem item) {

        ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) item.getMenuInfo();

        ExpandableListORMAccountAdapter adapter = (ExpandableListORMAccountAdapter)vListView.getExpandableListAdapter();
        AccountData accountData;
        ChannelData channelData;

        int type = ExpandableListView.getPackedPositionType(info.packedPosition);
        if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            int groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition);
            int childPos = ExpandableListView.getPackedPositionChild(info.packedPosition);
            int clickeditem = item.getItemId();

            accountData = (AccountData) adapter.getGroup(groupPos);
            channelData = (ChannelData) adapter.getChild(groupPos, childPos);

            long channelid = channelData.id;
            long accountid = channelData.accountid;

            //network = adapter.getGroup(groupPos)

            switch(clickeditem) {

                case 0:  //part
                    Globo.botManager.partAccountChannel(accountData.id, channelData.channel, this.getActivity());
                    break;
                case 1:  //edit
                    DlgManageChannel dialog = DlgManageChannel.newEdit(accountData, channelData, DlgManageChannel.FRAGACCOUNTLIST);
                    dialog.setTargetFragment(this,0);
                    dialog.show(this.getFragmentManager(), "dialogmanagechannel");
                    break;
                case 2:  //delete
                    confirmDeleteChannel(channelData);
                    break;
                case 3: //view messages
                    accountid = adapter.getGroupId(groupPos);
                    String accountname = accountData.accountname;
                    viewMessages(accountid, accountname ,channelData.channel);
                    break;
            }

            return true;
        } else if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
            int groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition);
            int clickeditem = item.getItemId();

            accountData = (AccountData) adapter.getGroup(groupPos);
            long accountid = accountData.id;

            switch(clickeditem) {
                case 0: //add channel
                    DlgManageChannel dialog = DlgManageChannel.newInsert(accountData, "#", DlgManageChannel.FRAGACCOUNTLIST);
                    dialog.setTargetFragment(this,0);
                    dialog.show(this.getFragmentManager(), "dialogmanagechannel");
                    break;
                case 1: //connect
                    this.getActivity().startService(connectIntentServer((AccountData)adapter.getGroup(groupPos)));
                    break;
                case 2:// disconnect
                    Globo.botManager.disconnectAccount(accountid);
                    break;
                case 3: //edit
                    editAccount(accountid);
                    break;
                case 4: //delete
                    confirmDeleteAccount(accountData);
                    break;
            }
            return true;
        }

        return false;

    }


    //========================= Handler ======================

    private static class FragHandler extends Handler {
        private WeakReference<FragAccountList> mTarget;

        FragHandler(FragAccountList target) {
            mTarget = new WeakReference<FragAccountList>(target);
        }

        @Override
        public void handleMessage(Message msg) {

            FragAccountList frag= mTarget.get();
            if (frag == null){
                return;
            }

            String action = msg.getData().getString("action");

            if (action == "toast") {
                Toaster.toast(frag.getActivity(),msg.getData().getString("message"));
                return;
            }

            if (action == "refresh") {
                frag.notifyListView();
                return;
            }

            if (action == "requery") {
                frag.loadRefreshDataTask();
                return;
            }


            if (action == "error") {
                //refactor into an error log
                return;
            }


        }


    };


}
