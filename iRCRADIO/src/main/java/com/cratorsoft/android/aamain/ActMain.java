package com.cratorsoft.android.aamain;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.cratorsoft.android.confirmback.ActConfirmBackPressed;
import com.cratorsoft.android.confirmback.FragConfirmBackPressed;
import com.cratorsoft.android.dialog.DlgAbout;
import com.cratorsoft.android.dialog.DlgInfo;
import com.cratorsoft.android.drawer.DrawerDisplayer;
import com.cratorsoft.android.events.RefreshToggles;
import com.cratorsoft.android.frag.FragAccountList_;
import com.cratorsoft.android.frag.FragChatLog_;
import com.cratorsoft.android.frag.FragDrawer;
import com.cratorsoft.android.frag.FragEditPreferences;
import com.cratorsoft.android.util.Networking;
import com.earthflare.android.ircradio.ActWebView;
import com.earthflare.android.ircradio.Globo;
import com.earthflare.android.ircradio.GloboUtil;
import com.earthflare.android.ircradio.IrcRadioService;
import com.earthflare.android.ircradio.NavItem;
import com.earthflare.android.ircradio.R;
import com.earthflare.android.logmanager.LogManager;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

@EActivity
 public class ActMain extends ActionBarActivity implements DrawerDisplayer, ActConfirmBackPressed {

     public static final String LAUNCH_NOTIFICATIONLINK = "launchnotificationlink";

     @ViewById(resName = "drawer_layout")
     DrawerLayout mDrawerLayout;

     @FragmentById(resName = "frag_drawer")
     FragDrawer mFragDrawer;

     private ActionBarDrawerToggle mDrawerToggle;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.act_main);

         // enable ActionBar app icon to behave as action to toggle nav drawer
         getSupportActionBar().setDisplayHomeAsUpEnabled(true);
         getSupportActionBar().setHomeButtonEnabled(true);

         // ActionBarDrawerToggle ties together the the proper interactions
         // between the sliding drawer and the action bar app icon
         mDrawerToggle = new ActionBarDrawerToggle(
                 this, /* host Activity */
                 mDrawerLayout, /* DrawerLayout object */
                 R.string.drawer_open, /*
                                          * "open drawer" description for
                                          * accessibility
                                          */
                 R.string.drawer_close /*
                                          * "close drawer" description for
                                          * accessibility
                                          */
         ) {
             public void onDrawerClosed(View view) {
                 refreshTitle(false);
                 invalidateOptionsMenu(); // creates call to
                 // onPrepareOptionsMenu()
                 if (ActMain.this.getSupportFragmentManager().getBackStackEntryCount() > 1) {
                     mDrawerToggle.setDrawerIndicatorEnabled(false);
                 }
             }

             public void onDrawerOpened(View drawerView) {
                 refreshTitle(true);
                 invalidateOptionsMenu(); // creates call to
                 // onPrepareOptionsMenu()
                 mDrawerToggle.setDrawerIndicatorEnabled(true);
             }
         };
         mDrawerLayout.setDrawerListener(mDrawerToggle);

         if (savedInstanceState == null) {
             addTopFragmentToStack();
         }

         getSupportFragmentManager().addOnBackStackChangedListener(new OnBackStackChangedListener() {

             @Override
             public void onBackStackChanged() {
                 int count = ActMain.this.getSupportFragmentManager().getBackStackEntryCount();
                 if (count > 1) {
                     mDrawerToggle.setDrawerIndicatorEnabled(false);
                 } else {
                     mDrawerToggle.setDrawerIndicatorEnabled(true);
                 }
             }
         });

     }

     @Override
     protected void onPostCreate(Bundle savedInstanceState) {
         super.onPostCreate(savedInstanceState);
         // Sync the toggle state after onRestoreInstanceState has occurred.
         int count = ActMain.this.getSupportFragmentManager().getBackStackEntryCount();
         if (count > 1) {
             mDrawerToggle.setDrawerIndicatorEnabled(false);
         } else {
             mDrawerToggle.setDrawerIndicatorEnabled(true);
         }
         mDrawerToggle.syncState();

     }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String action = intent.getAction();

        if (action != null && action.equals(ActMain.LAUNCH_NOTIFICATIONLINK)) {
            launchChatlogFromNotification(intent);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void addTopFragmentToStack() {

         Intent intent = this.getIntent();

         String action = intent.getAction();

         if (action != null && action.equals(ActMain.LAUNCH_NOTIFICATIONLINK)) {

             launchChatlogFromNotification(intent);

         } else {


             // Add default fragment
             FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
             FragAccountList_ fb = new FragAccountList_();
             ft.add(R.id.content_frame, fb);
             ft.addToBackStack("listaccount");
             ft.commit();
             //drawerOpen();

         }
     }

     @Override
     public void onConfigurationChanged(Configuration newConfig) {
         super.onConfigurationChanged(newConfig);
         // Pass any configuration change to the drawer toggls
         mDrawerToggle.onConfigurationChanged(newConfig);
     }

     private void refreshTitle(boolean isOpen) {
         if (isOpen) {
             getSupportActionBar().setTitle(Globo.drawerTitle);
             getSupportActionBar().setSubtitle(Globo.drawerSubtitle);
         } else {
             getSupportActionBar().setTitle(Globo.fragTitle);
             getSupportActionBar().setSubtitle(Globo.fragSubtitle);
         }
     }

     public void drawerOpen() {
         mDrawerLayout.openDrawer(Gravity.START);
     }

     public void drawerClose() {
         mDrawerLayout.closeDrawer(Gravity.START);
     }

     @Override
     public void drawerRefresh() {
         //mFragDrawer.refreshTask();
     }

     @Override
     public boolean drawerIsClosed() {
         return !mDrawerLayout.isDrawerOpen(Gravity.START);
     }

     @Override
     public boolean drawerIsOpen() {
         return mDrawerLayout.isDrawerOpen(Gravity.START);
     }

    @Override
    public void setFragTitle(String title, String subtitle) {
        Globo.fragTitle = new SpannableString(title);
        Globo.fragSubtitle = subtitle;
        refreshTitle(drawerIsOpen());
    }

    @Override
     public void setFragTitle(SpannableString title, String subtitle) {
         Globo.fragTitle = title;
         Globo.fragSubtitle = subtitle;
         refreshTitle(drawerIsOpen());
     }

     @Override
     public void setDrawerTitle(String title, String subtitle) {
         Globo.drawerTitle = title;
         Globo.drawerSubtitle = subtitle;
         refreshTitle(drawerIsOpen());
     }

     // ==================================================== Confirmation
     // ========================================

     FragConfirmBackPressed fragConfirmer = null;

     @Override
     public void onBackPressed() {


         if (this.getSupportFragmentManager().getBackStackEntryCount() == 1) {
             drawerOpen();
             return;
         }

         if (fragConfirmer == null) {
             super.onBackPressed();
             return;
         } else {
             fragConfirmer.onBackPressed();
             return;
         }

     }

     @Override
     public void setFragConfirmBackPressed(FragConfirmBackPressed fragment) {
         fragConfirmer = fragment;
     }

     @Override
     public void goBack() {
         super.onBackPressed();
     }

     @Override
     public boolean onPrepareOptionsMenu(Menu menu) {
         super.onPrepareOptionsMenu(menu);
         if (drawerIsOpen()) {


             menu.removeItem(R.id.ACTION_DISCONNECT);
             menu.add(0, R.id.ACTION_DISCONNECT, 0, this.getString(R.string.button_disconnect)).setIcon(R.drawable.ic_flash_on_white_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

             menu.removeItem(R.id.ACTION_TOGGLESOUND);
             if (Globo.mutestate) {
                 menu.add(0, R.id.ACTION_TOGGLESOUND, 0, this.getString(R.string.button_mute)).setIcon(R.drawable.ic_volume_off_white_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
             } else {
                 menu.add(0, R.id.ACTION_TOGGLESOUND, 0, this.getString(R.string.button_mute)).setIcon(R.drawable.ic_volume_up_white_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
             }

             menu.removeItem(R.id.ACTION_TOGGLETOAST);
             if (Globo.toaststate) {
                 menu.add(0, R.id.ACTION_TOGGLETOAST, 0, this.getString(R.string.button_toast)).setIcon(R.drawable.ic_message_white_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
             } else {
                 menu.add(0, R.id.ACTION_TOGGLETOAST, 0, this.getString(R.string.button_toast)).setIcon(R.drawable.ic_messenger_white_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
             }


             if (menu.findItem(R.id.ACTION_CLEARLOGS) == null) {
                 menu.add(0, R.id.ACTION_CLEARLOGS, 0, this.getString(R.string.button_clearlogs)).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
             }

             if (menu.findItem(R.id.ACTION_INFO) == null) {
                 menu.add(0, R.id.ACTION_INFO, 0, this.getString(R.string.menu_info)).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
             }

             /*
             if (menu.findItem(R.id.ACTION_PREFERENCES) == null) {
                 menu.add(0, R.id.ACTION_PREFERENCES, 0, this.getString(R.string.menu_preferences)).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
             }
             */

             if (menu.findItem(R.id.ACTION_ABOUT) == null) {
                 menu.add(0, R.id.ACTION_ABOUT, 0, this.getString(R.string.menu_about)).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
             }


         }
         return true;
     }

     @Override
     public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
         if (mDrawerToggle.onOptionsItemSelected(item)) {
             return true;
         }
         // Handle action buttons
         int selectedid = item.getItemId();
         if (R.id.ACTION_ABOUT == selectedid) {
             actionShowAbout();
         } else if (R.id.ACTION_INFO == selectedid) {
             actionShowInfo();
         } else if (R.id.ACTION_CLEARLOGS == selectedid) {
             clearLogsConfirm();
         } else if (android.R.id.home == selectedid) {
             onBackPressed();
         } else if (R.id.ACTION_DISCONNECT == selectedid) {
             confirmDisconnect();
         } else if (R.id.ACTION_TOGGLESOUND == selectedid) {
             toggleSound();
         } else if (R.id.ACTION_TOGGLETOAST == selectedid) {
             toggleToast();
         } else if (R.id.ACTION_PREFERENCES == selectedid) {
             editPreferences();
         } else {
             return super.onOptionsItemSelected(item);
         }


         return true;
     }

     //======================== Actions =====================

     private void actionShowAbout() {
         DlgAbout dialog = DlgAbout.newInstance();
         dialog.show(this.getSupportFragmentManager(), "dialogabout");
     }

     private void actionShowInfo() {
         if (Networking.haveNet()) {
             Intent intent = new Intent(this, ActWebView.class);
             intent.putExtra("url", this.getString(R.string.app_url));
             startActivity(intent);
         } else {

             Bundle data = new Bundle();
             data.putString("title", this.getString(R.string.error_title_networkerror));
             data.putString("message", this.getString(R.string.error_message_internetrequiredtodisplayinfopage));

             DlgInfo dialog = DlgInfo.newInstance(data);
             dialog.show(this.getSupportFragmentManager(), "dialoginfo");

         }
     }

     protected void clearLogsConfirm() {

         new MaterialDialog.Builder(this)
                 .title(R.string.alert_title_confirmclearlogs)
                 .content(R.string.alert_message_confirmclearlogs)
                 .positiveText(R.string.button_ok)
                 .negativeText(R.string.button_cancel)
                 .onPositive(new MaterialDialog.SingleButtonCallback() {
                     @Override
                     public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                         clearLogs();
                     }
                 })
                 .show();
     }

     protected void clearLogs() {
         try {
             Globo.purgeTTS();
         } catch (Exception f) {
         }
         LogManager.resetLogs();
     }

     protected void confirmDisconnect() {
         new MaterialDialog.Builder(this)
                 .title(R.string.alert_title_confirmdisconnect)
                 .content(R.string.alert_message_confirmdisconnect)
                 .positiveText(R.string.button_ok)
                 .negativeText(R.string.button_cancel)
                 .onPositive(new MaterialDialog.SingleButtonCallback() {
                     @Override
                     public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                         disconnect();
                     }
                 })
                 .show();
     }

     public void disconnect() {
         Intent radioServiceIntent = new Intent(this,
                 IrcRadioService.class);
         radioServiceIntent.putExtra("action", "kill");
         this.startService(radioServiceIntent);
     }

     public void toggleSound() {
         Globo.togglemuteTTS();
     }

     public void toggleToast() {
        Globo.toggleToastState();
     }

     private void editPreferences(){
         this.drawerClose();
         getSupportFragmentManager().popBackStack("editprefs", FragmentManager.POP_BACK_STACK_INCLUSIVE);
         Fargs fargs = Fargs.create();
         fargs.setPreferencesResource(R.xml.preferences);
         FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
         FragEditPreferences fb = new FragEditPreferences();
         fb.setArguments(fargs.bundle);
         ft.replace(R.id.content_frame, fb);
         ft.addToBackStack("editprefs");
         ft.commit();
     }


    private void launchChatlogFromNotification(Intent intent){

        Fargs fargs = new Fargs();
        fargs.setNotificationLink(intent.getBooleanExtra("notificationlink",false));
        fargs.setUid(intent.getLongExtra("uid",-1));
        fargs.setAccountName(intent.getStringExtra("accountname"));
        fargs.setNetwork(intent.getLongExtra("network",0));
        fargs.setChannelName(intent.getStringExtra("channelname"));
        fargs.setActtype(intent.getStringExtra("acttype"));

        FragChatLog_ frag = new FragChatLog_();
        frag.setArguments(fargs.bundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        ft.add(R.id.content_frame, frag);
        ft.addToBackStack("chatlog");
        ft.commit();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RefreshToggles event) {
        invalidateOptionsMenu();
    };

}
