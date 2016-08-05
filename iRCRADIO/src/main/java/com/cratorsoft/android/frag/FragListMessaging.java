package com.cratorsoft.android.frag;

import android.content.Intent;
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
 import android.widget.ExpandableListView;
 import android.widget.Toast;

 import com.cratorsoft.android.aamain.BuildManager;
import com.cratorsoft.android.aamain.Fargs;
import com.cratorsoft.android.dialog.DlgAbout;
import com.cratorsoft.android.dialog.DlgPromptVoiceLanguage;
import com.cratorsoft.android.taskmanager.BusyAsyncTask;
 import com.cratorsoft.android.taskmanager.BusyTaskFragment;
 import com.earthflare.android.ircradio.ActChatLog;
 import com.earthflare.android.ircradio.ExpandableListChannelAdapter;
 import com.earthflare.android.ircradio.Globo;
import com.earthflare.android.ircradio.NavItem;
import com.earthflare.android.ircradio.PromptVoiceLanguage;
import com.earthflare.android.ircradio.R;
 import com.earthflare.android.logmanager.LogManager;

 import org.androidannotations.annotations.EFragment;
 import org.androidannotations.annotations.ViewById;

 import java.lang.ref.WeakReference;

@EFragment
 public class FragListMessaging extends BusyTaskFragment implements ExpandableListView.OnChildClickListener {

     int mBusyStyle = com.cratorsoft.android.taskmanager.BUSY.NONMODAL_CONTENT;
     private boolean mInitialized = false;
     private Handler mFragHandler;

     // Misc

     // Data


     // Views
     @ViewById(R.id.messagingListView)
     ExpandableListView vListView;

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

         View v = inflater.inflate(R.layout.frag_listmessaging, container, false);
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
         Globo.handlerChannelList = mFragHandler;
         Globo.actChannelListVisible = true;
     }

     @Override
     public void onPause() {
         Globo.actChannelListVisible = false;
         Globo.handlerChannelList = null;
         super.onPause();
         if (mInitialized) {
             stashData();
         }
     }

     // ======================== Fragment Data Lifecycle ====

     public void parseIntent() {

     }

     private void configViews() {


         vListView.setOnChildClickListener(this);
         vListView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {


             public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo
                     menuInfo) {

                 ExpandableListChannelAdapter adapter = (ExpandableListChannelAdapter) vListView.getExpandableListAdapter();
                 ExpandableListView.ExpandableListContextMenuInfo elcm = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;

                 int type = ExpandableListView.getPackedPositionType(elcm.packedPosition);
                 if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {

                     int groupPosition = ExpandableListView.getPackedPositionGroup(elcm.packedPosition);
                     int childPosition = ExpandableListView.getPackedPositionChild(elcm.packedPosition);
                     String channel = adapter.children[groupPosition][childPosition];


                     menu.setHeaderTitle(channel);
                     menu.add(0, 0, 0, R.string.ctx_clear);
                     if (channel.startsWith("#")) {
                         menu.add(0, 1, 0, R.string.ctx_part);
                     }
                     menu.add(0, 2, 0, R.string.ctx_close);
                     menu.add(0, 3, 0, R.string.ctx_activatespeech);

                 } else {

                     menu.setHeaderTitle(adapter.groupNames[ExpandableListView.getPackedPositionGroup(elcm.packedPosition)]);
                     menu.add(0, 0, 0, R.string.ctx_viewlog);
                     menu.add(0, 1, 0, R.string.ctx_clear);
                     menu.add(0, 2, 0, R.string.ctx_disconnect);
                     menu.add(0, 3, 0, R.string.ctx_close);
                     menu.add(0, 4, 0, R.string.ctx_activatespeech);
                 }
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

     private void setTitle() {
         getDrawerDisplayer().setFragTitle(getString(R.string.label_messages), "");
     }

     public void refreshUI() {
         ExpandableListChannelAdapter adapter = (ExpandableListChannelAdapter) vListView.getExpandableListAdapter();
         adapter.refresh();
         adapter.notifyDataSetChanged();
     }

     protected void promptLanguage(String type, long network, String channel) {
         DlgPromptVoiceLanguage frag = DlgPromptVoiceLanguage.newInstance(type, network, channel);
         frag.show(this.getFragmentManager(),"promptlanguage");
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

         ExpandableListChannelAdapter adapter = new ExpandableListChannelAdapter(this.getActivity());
         vListView.setAdapter(adapter);

     }

     //========================= Stash Data =====================

     //========================= Fragment Listeners ====================

     public boolean onContextItemSelected(MenuItem item) {

         ExpandableListChannelAdapter adapter = (ExpandableListChannelAdapter) vListView.getExpandableListAdapter();
         ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) item.getMenuInfo();

         String channel;
         Long network;

         int type = ExpandableListView.getPackedPositionType(info.packedPosition);
         if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
             int groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition);
             int childPos = ExpandableListView.getPackedPositionChild(info.packedPosition);
             int clickeditem = item.getItemId();
             channel = adapter.children[groupPos][childPos];
             network = adapter.groups[groupPos];
             String accountName = adapter.groupNames[groupPos];

             switch (clickeditem) {
                 case 0:
                     LogManager.clearChannel(network, channel, accountName);
                     this.refreshUI();
                     break;
                 case 1:
                     Globo.botManager.partChannel(network, channel, this.getActivity());
                     break;
                 case 2:
                     Globo.botManager.partChannel(network, channel, this.getActivity());
                     LogManager.closeChannel(this.getActivity(), network, channel, accountName);
                     this.refreshUI();
                     break;
                 case 3:
                     promptLanguage("channel", network, channel);
                     this.refreshUI();
                     break;
             }

             return true;
         } else if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
             int groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition);
             int clickeditem = item.getItemId();
             network = adapter.groups[groupPos];
             String accountName = adapter.groupNames[groupPos];
             switch (clickeditem) {

                 case 0:
                     FragChatLog_ frag = new FragChatLog_();
                     Fargs fargs = new Fargs();
                     fargs.setAccountName(accountName);
                     fargs.setNetwork(network);
                     fargs.setChannelName("");
                     fargs.setActtype(NavItem.SERVER);

                     frag.setArguments(fargs.bundle);

                     FragmentTransaction ft = getFragmentManager().beginTransaction();
                     getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                     ft.add(R.id.content_frame, frag);
                     ft.addToBackStack("chatlog");
                     ft.commit();
                     break;
                 case 1:
                     LogManager.clearServer(network, accountName);
                     this.refreshUI();
                     break;
                 case 2:
                     Globo.botManager.disconnectServer(network);
                     break;
                 case 3:
                     Globo.botManager.disconnectServer(network);
                     LogManager.closeServer(this.getActivity(), network);
                     this.refreshUI();
                     break;
                 case 4:
                     promptLanguage("server", network, "");
                     break;
             }
             return true;
         }

         return false;


     }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

        ExpandableListChannelAdapter adapter = (ExpandableListChannelAdapter) vListView.getExpandableListAdapter();

        Long network = adapter.groups[groupPosition];
        String channelname = adapter.children[groupPosition][childPosition];
        String accountname = adapter.groupNames[groupPosition];


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


        return true;
    }

     //========================= Handler ======================

     private static class FragHandler extends Handler {
         private WeakReference<FragListMessaging> mTarget;

         FragHandler(FragListMessaging target) {
             mTarget = new WeakReference<FragListMessaging>(target);
         }

         @Override
         public void handleMessage(Message msg) {

             FragListMessaging frag = mTarget.get();
             if (frag == null) {
                 return;
             }

             String action = msg.getData().getString("action");

             if (action == "refresh") {
                 frag.refreshUI();
                 return;
             }

             if (action == "toast") {
                 frag.makeToast(msg.getData().getString("message"));
                 return;
             }


         }


     }



     public void makeToast(String message) {
         Toast.makeText(this.getActivity(), message, Toast.LENGTH_LONG).show();
     }

 }
