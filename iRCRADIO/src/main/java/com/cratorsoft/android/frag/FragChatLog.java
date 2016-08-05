package com.cratorsoft.android.frag;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.text.ClipboardManager;
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cratorsoft.android.aamain.BuildManager;
import com.cratorsoft.android.aamain.Fargs;
import com.cratorsoft.android.chat.MenuData;
import com.cratorsoft.android.dialog.DlgPromptUser;
import com.cratorsoft.android.dialog.DlgPromptVoiceLanguage;
import com.cratorsoft.android.dialog.DlgTopic;
import com.cratorsoft.android.drawer.DrawerDisplayer;
import com.cratorsoft.android.taskmanager.BusyAsyncTask;
import com.cratorsoft.android.taskmanager.BusyTaskFragment;
import com.earthflare.android.cross.CrossView;
import com.earthflare.android.cross.OnCrossListener;
import com.earthflare.android.ircradio.Globo;
import com.earthflare.android.ircradio.IrcRadioService;
import com.earthflare.android.ircradio.ListChatAdapter;
import com.earthflare.android.ircradio.MessageType;
import com.earthflare.android.ircradio.NavBar;
import com.earthflare.android.ircradio.NavItem;
import com.earthflare.android.ircradio.R;
import com.earthflare.android.logmanager.LogEntry;
import com.earthflare.android.logmanager.LogManager;
import com.earthflare.android.logmanager.ServerLog;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;

@EFragment
public class FragChatLog extends BusyTaskFragment implements OnCrossListener {

    int mBusyStyle = com.cratorsoft.android.taskmanager.BUSY.NONMODAL_CONTENT;
    private boolean mInitialized = false;
    private Handler mFragHandler;

    // Misc
    private long uid;
    private MenuData menuData;

    // Data
    Bundle mArgs;

    // Views
    @ViewById(R.id.ListViewServerLog)
    ListView vListViewServerLog;

    @ViewById(R.id.textinput)
    EditText vChatTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
        this.parseIntent(savedInstanceState);
        mFragHandler = new FragHandler(this);
        loadRefreshDataTask();
        loadCreateDataTask();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.frag_chatlog, container, false);
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
        Globo.handlerChatLog = mFragHandler;
        Globo.actChatlogVisisble = true;
        refreshInterface();
    }

    @Override
    public void onPause() {
        Globo.actChatlogVisisble = false;
        Globo.handlerChatLog = null;
        super.onPause();
        if (mInitialized) {
            stashData();
        }
    }

    // ======================== Fragment Data Lifecycle ====

    public void parseIntent(Bundle savedInstanceState) {

        if (savedInstanceState != null){
            mArgs = savedInstanceState.getBundle("argsbundle");
        }else{
            mArgs = this.getArguments();
        }

        killNotification();
        Fargs fargs = new Fargs(mArgs);

        uid = fargs.getUid();

        Globo.chatlogActChannelName = fargs.getChannelName();
        Globo.chatlogActNetworkName = fargs.getNetwork();
        Globo.chatlogActType = fargs.getActtype();
        Globo.chatlogActAccountName = fargs.getAccountName();

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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBundle("argsbundle", mArgs);
    }

    // ======================== Fragment Functions =====

    private void setTitle() {

        SpannableString ss;

        boolean isChannelVoice;
        boolean isServerVoice;

        isChannelVoice = (Globo.voiceServer == Globo.chatlogActNetworkName && Globo.voiceChannel.equals(Globo.chatlogActChannelName) && Globo.voiceType.equals("channel") );
        isServerVoice = (Globo.voiceServer == Globo.chatlogActNetworkName && Globo.voiceType.equals("server"));

        int servercolor = this.getResources().getColor(Globo.botManager.getStatusColorServer(Globo.chatlogActNetworkName, false));
        int appnamelength =0;

        if ( Globo.chatlogActType.equals("server")) {
            ss = new SpannableString ( Globo.chatlogActAccountName);
            Globo.botManager.getStatusColorServer(Globo.chatlogActNetworkName, false);
            ss.setSpan(new ForegroundColorSpan(servercolor), appnamelength,  ss.length() , 0);
            if (isServerVoice) {
                ss.setSpan(new StyleSpan(Typeface.ITALIC), appnamelength, ss.length(), 0);
            }

        }else{
            int channelcolor = this.getResources().getColor(Globo.botManager.getStatusColorChannel(Globo.chatlogActNetworkName, Globo.chatlogActChannelName, false));
            ss = new SpannableString ( Globo.chatlogActAccountName + " " + Globo.chatlogActChannelName);
            ss.setSpan(new ForegroundColorSpan(servercolor), appnamelength,  appnamelength + Globo.chatlogActAccountName.length() , 0);
            ss.setSpan(new ForegroundColorSpan(channelcolor), appnamelength + Globo.chatlogActAccountName.length() , ss.length() , 0);
            if (isServerVoice) {
                ss.setSpan(new StyleSpan(Typeface.ITALIC), appnamelength, appnamelength + Globo.chatlogActAccountName.length(), 0);
            }else if(isChannelVoice) {
                ss.setSpan(new StyleSpan(Typeface.ITALIC), appnamelength + Globo.chatlogActAccountName.length(), ss.length(), 0);
            }
        }

        ((DrawerDisplayer)this.getActivity()).setFragTitle(ss,"");


    }

    private ListChatAdapter getChatAdapter() {
        return (ListChatAdapter) vListViewServerLog.getAdapter();
    }

    public void changeLog(NavItem ni) {

        synchronized (LogManager.serverLogMap) {
            Globo.chatlogActChannelName = ni.channelname;
            Globo.chatlogActNetworkName = ni.network;
            Globo.chatlogActType = ni.acttype;
            Globo.chatlogActAccountName = ni.accountname;

            mArgs = ni.getBundle();
            updateChecked();


            //font style  mono for server log.
            Typeface typeface = Typeface.DEFAULT;
            if (Globo.chatlogActType.equals("server")) {
                typeface = Typeface.MONOSPACE;
            }


            ListChatAdapter adapter = new ListChatAdapter(this.getActivity(), typeface, Globo.chatlogActAccountName);
            vListViewServerLog.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            vListViewServerLog.setSelection(adapter.getCount());

            setTitle();

            LinearLayout navChanll;
            navChanll = (LinearLayout) this.getView().findViewById(R.id.navchanll);
            NavBar.initialize(this, this.getActivity(), navChanll, new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    navClick(v);
                }
            });
        }
    }


    protected void updateChecked() {

        if (LogManager.serverLogMap.containsKey(Globo.chatlogActNetworkName)) {

            ServerLog sl = LogManager.serverLogMap.get(Globo.chatlogActNetworkName);
            if (Globo.chatlogActType.equals("server")) {
                sl.checked = true;
            } else {
                if (sl.channelLogMap.containsKey(Globo.chatlogActChannelName)) {
                    if (!sl.channelLogMap.get(Globo.chatlogActChannelName).checked) {
                        //mark checked
                        sl.channelLogMap.get(Globo.chatlogActChannelName).checked = true;

                        if (!Globo.chatlogActChannelName.startsWith("#")) {
                            //refresh notification
                            Intent serviceIconUpdate = new Intent(this.getActivity(), IrcRadioService.class);
                            serviceIconUpdate.putExtra("action", "updateicon");
                            this.getActivity().startService(serviceIconUpdate);
                        }
                    }
                }
            }
        }
    }

    public void makeToast(String message) {
        Toast.makeText(this.getActivity(), message, Toast.LENGTH_LONG).show();
    }

    private void refreshNavBar() {

        setTitle();

        if (Globo.chatlogChanNav.get()) {

            LinearLayout navChanll;
            View divider1;

            navChanll = (LinearLayout) getView().findViewById(R.id.navchanll);


            synchronized (LogManager.serverLogMap) {
                NavBar.initialize(this, this.getActivity(), navChanll, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        navClick(v);
                    }
                });
            }

        }

    }

    protected void killNotification() {

        boolean linkedfromstatus = mArgs.getBoolean("notificationlink", false);
        if (linkedfromstatus) {
            NotificationManager nm = (NotificationManager) this.getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            nm.cancel(2);
        }


    }

    protected void onNewArgs(Bundle args) {

        mArgs = args;

        uid = mArgs.getLong("uid", -1);
        killNotification();

        NavItem ni = new NavItem(mArgs.getLong("network", 0), mArgs.getString("account"), mArgs.getString("channel"), mArgs.getString("type"));

        this.changeLog(ni);
        scrollToUid();
    }

    protected void refreshInterface(){

        if (Globo.chatlogChanNav == null) {
            Globo.chatlogChanNav = new AtomicBoolean();
            Globo.chatlogChanNav.set(Globo.pref_chan_showchannav);
        }

        if (Globo.chatlogActNav == null)  {
            Globo.chatlogActNav = new AtomicBoolean();
            Globo.chatlogActNav.set(Globo.pref_chan_showactnav);
        }

        //mark read and update notification
        synchronized(LogManager.serverLogMap) {
            updateChecked();
        }

        initialize();
        initializeCrossing();
        setTitle();

        vListViewServerLog.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);

        scrollToUid();

    }

    synchronized private void initialize() {



        //font style  mono for server log.
        Typeface typeface = Typeface.DEFAULT;
        if (Globo.chatlogActType.equals("server") ) {
            typeface = Typeface.MONOSPACE;
        }

        ListChatAdapter adapter = new ListChatAdapter(this.getActivity(), typeface, Globo.chatlogActAccountName  );
        vListViewServerLog.setAdapter(adapter);

        registerForContextMenu(vListViewServerLog);
        Globo.actChatlogVisisble = true;


        vChatTextView.setRawInputType(InputType.TYPE_CLASS_TEXT);
        vChatTextView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        vChatTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (    actionId == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    postmessage();
                    return true;
                }
                return false;
            }
        });


        /*
        //initialize textbox
        vChatTextView.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (event.getAction() == KeyEvent.ACTION_UP) {
                        postmessage();
                        return true;
                    }
                    return true;
                }
                return false;
            }
        });
        */


    }

    private void postmessage() {

        String input = vChatTextView.getText().toString();
        //check if server connected  - parse command else warn not connected

        if ( Globo.botManager.ircBots.containsKey(Globo.chatlogActNetworkName) ) {

            if (  Globo.botManager.ircBots.get(Globo.chatlogActNetworkName).isConnected() ){

                Globo.botManager.ircBots.get(Globo.chatlogActNetworkName).parseinput(input, Globo.chatlogActChannelName);

            }else{
                notConnectedError();
            }

        }else {

            notConnectedError();

        }

        vChatTextView.setText("");
    }

    private void notConnectedError() {

        if(Globo.chatlogActType.equals("channel")){

            //message notconnected
            LogManager.writeChannelLog(MessageType.ERROR, "IRC Radio", getString(R.string.error_notconnectedtoserver), Globo.chatlogActNetworkName, Globo.chatlogActChannelName, Globo.chatlogActAccountName, this.getActivity(), true,"");
            vChatTextView.setText("");

        }else{

            LogManager.writeServerLog("error",getString(R.string.error_notconnectedtoserver),"",Globo.chatlogActNetworkName,  Globo.chatlogActAccountName, this.getActivity());

        }


    }

    // ======================== Fragment Actions =====


    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        if (((DrawerDisplayer) this.getActivity()).drawerIsClosed()) {

            //remove items
            menu.removeItem(R.id.ACTION_OPTIONS);
            menu.removeItem(R.id.ACTION_COMMANDS);
            menu.removeItem(R.id.ACTION_USERLIST);
            menu.removeItem(R.id.ACTION_CHANNELTOPIC);


            if( Globo.chatlogActType.equals("channel")) {

                if(Globo.chatlogActChannelName.startsWith("#")) {
                    menu.add(0, R.id.ACTION_USERLIST, 0, getString(R.string.menu_userlist)).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                    menu.add(0, R.id.ACTION_CHANNELTOPIC, 0, getString(R.string.menu_topic)).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                    menu.add(0, R.id.ACTION_OPTIONS, 0, getString(R.string.menu_options)).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                    menu.add(0, R.id.ACTION_COMMANDS, 0, getString(R.string.menu_commands)).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                }else{
                    menu.add(0, R.id.ACTION_OPTIONS, 0, getString(R.string.menu_options)).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                    menu.add(0, R.id.ACTION_COMMANDS, 0, getString(R.string.menu_commands)).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                }


            }else{
                menu.add(0, R.id.ACTION_OPTIONS, 0, getString(R.string.menu_options)).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                menu.add(0, R.id.ACTION_COMMANDS, 0, getString(R.string.menu_commands)).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            }

        }

        super.onPrepareOptionsMenu(menu);


    }



    public boolean onOptionsItemSelected (MenuItem item){


        switch (item.getItemId()){


            //channel
            case R.id.ACTION_USERLIST:
                DlgPromptUser dlgPromptUser = DlgPromptUser.newInstance();
                dlgPromptUser.setTargetFragment(this,0);
                dlgPromptUser.show(this.getFragmentManager(),"dlgpromptuser");
                return true;
            case R.id.ACTION_CHANNELTOPIC:
                DlgTopic dlgTopic = DlgTopic.newInstance();
                dlgTopic.show(this.getFragmentManager(),"dlgtopic");
                return true;

            //both
            case R.id.ACTION_OPTIONS:
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                FragEditChannelPreferences fb = new FragEditChannelPreferences();
                ft.replace(R.id.content_frame, fb);
                ft.addToBackStack("editprefs");
                ft.commit();
                return true;

            case R.id.ACTION_COMMANDS:
                this.getActivity().openContextMenu(vListViewServerLog);
                return true;
        }

        return false;

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

    protected void scrollToUid() {
        final ListChatAdapter adapter = getChatAdapter();
        if (uid > -1) {
            int position = adapter.getPositionByUid(uid);

            vListViewServerLog.post(new Runnable() {
                public void run() {
                    vListViewServerLog.setSelectionFromTop(adapter.getPositionByUid(uid), 10);
                    uid = -1;
                    mArgs.putLong("uid", -1);
                }
            });
        } else {
            vListViewServerLog.post(new Runnable() {
                public void run() {

                    vListViewServerLog.setSelection(adapter.getCount() - 1);
                }
            });
        }

    }


    protected void clipboardCopy(LogEntry logEntry) {
        ClipboardManager cm = (ClipboardManager)this.getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText(logEntry.ss.toString());
    }

    protected void promptLanguage( String type, long network, String channel ) {
        DlgPromptVoiceLanguage frag = DlgPromptVoiceLanguage.newInstance(type, network, channel);
        frag.show(this.getFragmentManager(),"promptlanguage");
    }

    public void nextLog() {

        NavItem ni = new NavItem(Globo.chatlogActNetworkName,Globo.chatlogActAccountName);

        ni = LogManager.getNextLog(ni);

        if (ni.acttype.equals("none")) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            FragAccountList_ fb = new FragAccountList_();
            ft.add(R.id.content_frame, fb);
            ft.addToBackStack("accountlist");
            ft.commit();
        }else{
            this.changeLog(ni);
        }

    }

    // ======================== Fragment Attach/Populate =====


    public void attachAdapters() {

    }

    //========================= Stash Data =====================

    //========================= Fragment Listeners ====================

    public void navClick(View v) {
        NavItem ni = (NavItem) v.getTag();
        changeLog(ni);
    }

    public void initializeCrossing() {

        CrossView cross = (CrossView) getView().findViewById(R.id.crossview);
        cross.addOnCrossListener(this);

        LinearLayout navChanll;


        navChanll = (LinearLayout)getView().findViewById(R.id.navchanll);



        if (Globo.chatlogChanNav.get()) {
            navChanll.setVisibility(View.VISIBLE);

            this.refreshNavBar();
        }else{
            navChanll.setVisibility(View.GONE);

        }


        if (Globo.chatlogActNav.get()) {

            ((ActionBarActivity)this.getActivity()).getSupportActionBar().show();


        }else{
            ((ActionBarActivity)this.getActivity()).getSupportActionBar().hide();


        }


    }

    @Override
    public void onCross(boolean crossed) {
        LinearLayout navChanll;

        navChanll = (LinearLayout)getView().findViewById(R.id.navchanll);

        if (!crossed ) {
            if (Globo.chatlogChanNav.get()) {
                Globo.chatlogChanNav.set(false);
                navChanll.setVisibility(View.GONE);

            }else{
                Globo.chatlogChanNav.set(true);
                navChanll.setVisibility(View.VISIBLE);

                this.refreshNavBar();
            }
        }else{
            if (Globo.chatlogActNav.get()) {
                Globo.chatlogActNav.set(false);
                ((ActionBarActivity)getActivity()).getSupportActionBar().hide();

            }else{
                Globo.chatlogActNav.set(true);
                ((ActionBarActivity)getActivity()).getSupportActionBar().show();

            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        // TODO Auto-generated method stub
        super.onCreateContextMenu(menu, v, menuInfo);

        long network;
        String account;
        String channel;
        String type;

        boolean copysafe=false;
        AdapterView.AdapterContextMenuInfo mi = (AdapterView.AdapterContextMenuInfo)menuInfo;
        if (mi != null){
            copysafe = true;
        }



        boolean navbutton = true;
        if (v.getTag() == null) {
            navbutton = false;
            network = Globo.chatlogActNetworkName;
            account = Globo.chatlogActAccountName;
            channel = Globo.chatlogActChannelName;
            type = Globo.chatlogActType;
        }else{
            NavItem ni = (NavItem)v.getTag();
            network = ni.network;
            account = ni.accountname;
            channel = ni.channelname;
            type = ni.acttype;
        }

        menuData = new MenuData(network,account,channel,type);


        if (type.equals("server")) {
            menu.setHeaderTitle(account);
            if (!navbutton && copysafe){
                menu.add(0, 5, 0, R.string.ctx_copy);
            }
            menu.add(0, 1, 0, R.string.ctx_clear);
            menu.add(0, 2, 0, R.string.ctx_disconnect);
            menu.add(0, 3, 0, R.string.ctx_close);
            menu.add(0, 4, 0, R.string.ctx_activatespeech);
        }else{
            menu.setHeaderTitle(channel);
            if (!navbutton && copysafe){
                menu.add(0, 5, 0, R.string.ctx_copy);
            }
            menu.add(0, 0, 0, R.string.ctx_clear);
            if (channel.startsWith("#")){
                menu.add(0, 1, 0, R.string.ctx_part);
            }
            menu.add(0, 2, 0 , R.string.ctx_close);
            menu.add(0, 3, 0, R.string.ctx_activatespeech);
        }



    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        int clickeditem = item.getItemId();

        ListChatAdapter adapter = getChatAdapter();
        if (menuData.type.equals("server")) {

            switch(clickeditem) {
                case 1:
                    LogManager.clearServer(menuData.network, menuData.account);
                    adapter.refresh();
                    adapter.notifyDataSetChanged();
                    this.refreshNavBar();
                    break;
                case 2:
                    Globo.botManager.disconnectServer(menuData.network);
                    break;
                case 3:
                    Globo.botManager.disconnectServer(menuData.network);
                    LogManager.closeServer(this.getActivity(), menuData.network);
                    nextLog();
                    break;
                case 4:
                    promptLanguage("server",menuData.network,"");
                    break;
                case 5:
                    AdapterView.AdapterContextMenuInfo mi = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

                    LogEntry logEntry = (LogEntry) adapter.getItem(mi.position);
                    clipboardCopy(logEntry);
                    break;
            }
            return true;


        }

        if (menuData.type.equals("channel")) {
            switch(clickeditem) {
                case 0:
                    LogManager.clearChannel(menuData.network, menuData.channel, menuData.account);
                    adapter.refresh();
                    adapter.notifyDataSetChanged();
                    this.refreshNavBar();
                    break;
                case 1:
                    Globo.botManager.partChannel(menuData.network, menuData.channel, this.getActivity());
                    break;
                case 2:
                    Globo.botManager.partChannel(menuData.network, menuData.channel, this.getActivity());
                    LogManager.closeChannel(this.getActivity(), menuData.network, menuData.channel , menuData.account);
                    nextLog();
                    break;
                case 3:
                    promptLanguage("channel",menuData.network,menuData.channel);
                    this.refreshNavBar();
                    break;
                case 5:
                    AdapterView.AdapterContextMenuInfo mi = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
                    LogEntry logEntry = (LogEntry) adapter.getItem(mi.position);
                    clipboardCopy(logEntry);
                    break;
            }
            return true;
        }
        return false;
    }

    //========================= Handler ======================

    private static class FragHandler extends Handler {
        private WeakReference<FragChatLog> mTarget;

        FragHandler(FragChatLog target) {
            mTarget = new WeakReference<FragChatLog>(target);
        }

        @Override
        public void handleMessage(Message msg) {

            FragChatLog frag = mTarget.get();
            if (frag == null) {
                return;
            }

            String action = msg.getData().getString("action");
            String type = msg.getData().getString("type");

            ListChatAdapter adapter = frag.getChatAdapter();

            if (action == "addline") {
                LogEntry logentry = (LogEntry) msg.getData().getSerializable("logentry");
                adapter.addToList(logentry);
                adapter.notifyDataSetChanged();
                return;
            }

            if (action == "toast") {
                frag.makeToast(msg.getData().getString("message"));
                return;
            }

            if (action == "refresh") {
                frag.refreshNavBar();
            }


        }

    }


}
