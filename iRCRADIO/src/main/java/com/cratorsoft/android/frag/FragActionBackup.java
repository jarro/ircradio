package com.cratorsoft.android.frag;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cratorsoft.android.aamain.ActMain_;
import com.cratorsoft.android.aamain.BuildManager;
import com.cratorsoft.android.dialog.DlgInfo;
import com.cratorsoft.android.dialog.FragConfirm;
import com.cratorsoft.android.drawer.DrawerDisplayer;
import com.cratorsoft.android.listener.ConfirmListener;
import com.cratorsoft.android.taskmanager.BUSY;
import com.cratorsoft.android.taskmanager.BusyAsyncTask;
import com.cratorsoft.android.taskmanager.BusyTaskFragment;
import com.cratorsoft.android.util.BackupUtil;
import com.earthflare.android.ircradio.Globo;
import com.earthflare.android.ircradio.R;


public class FragActionBackup extends BusyTaskFragment implements ConfirmListener {

    int mBusyStyle = BUSY.NONMODAL_CONTENT;
    private boolean mInitialized = false;

    // Misc
    BackupUtil bu;

    // Data
    

    // Views

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        this.setHasOptionsMenu(true);
        bu = new BackupUtil(this.getActivity().getApplicationContext());


        loadRefreshDataTask();
        loadCreateDataTask();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.frag_action_backup, container, false);
        return v;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
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
        // setTitle();
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

    private void configViews() {

    }

    private void configFrag() {
        TextView tv1 = (TextView) this.getView().findViewById(R.id.TextView01);
        tv1.setText(tv1.getText() + "\n" + bu.backuppathnosdcard);
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

    public void restorePrompt() {

        if (!externalWriteable()) {
            return;
        }

        confirmRestore();
    }

    public void confirmRestore() {

        Bundle data = new Bundle();
        data.putString("title", getString(R.string.backup_confirmimport));
        data.putString("message",
                getString(R.string.backup_confirmimportmessage));

        FragConfirm dialog = FragConfirm.newInstance(data);
        dialog.setTargetFragment(this, 0);
        dialog.show(this.getFragmentManager(), "confirm");

    }

    private void alertFileNotFound() {

        Bundle data = new Bundle();
        data.putString("title", this.getString(R.string.alert_filenotfound));
        data.putString("message", this.getString(R.string.alert_backupnotfound));

        DlgInfo dialog = DlgInfo.newInstance(data);
        dialog.show(this.getFragmentManager(), "dialogAlert");

    }

    private void alertNoExternal() {
        Bundle data = new Bundle();
        data.putString("title", "Backup Error");
        data.putString("message", "External storage unavailable.");
        DlgInfo dialog = DlgInfo.newInstance(data);
        dialog.show(this.getFragmentManager(), "dialogAlert");
    }

    private boolean externalWriteable() {

        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but
            // all we need
            // to know is we can neither read nor write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }

        if (!mExternalStorageAvailable || !mExternalStorageWriteable) {
            this.alertNoExternal();
            return false;
        } else {
            return true;
        }

    }

    @Override
    public void onConfirm(String action) {
        restoreTask();
    }

    // ======================== Fragment Actions =====

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        if (((DrawerDisplayer) this.getActivity()).drawerIsClosed()) {

            if (menu.findItem(R.id.ACTION_BACKUP) == null) {

                menu.add(0, R.id.ACTION_BACKUP, 0, getString(R.string.backup_backup)).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
                menu.add(0, R.id.ACTION_RESTORE, 0, getString(R.string.backup_restore)).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

            }

        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (this.isBusy()) {
            return true;
        }

        int itemId = item.getItemId();
        if (itemId == R.id.ACTION_BACKUP) {
            backupTask();
            return true;
        } else if (itemId == R.id.ACTION_RESTORE) {
            restorePrompt();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
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

    public void backupTask() {

        if (!externalWriteable()) {
            return;
        }

        new BusyAsyncTask<Boolean>(this, mBusyStyle, true) {

            protected Boolean doInBackground() {

                try {
                    boolean success = false;
                    try {
                        Globo.db.beginTransaction();
                        BackupUtil.flagBackuped();
                        success = true;
                    } finally {
                        Globo.db.endTransaction();
                    }

                    if (success) {
                        this.getActivity().startActivity(new Intent(this.getActivity(), ActMain_.class));
                        System.exit(0);
                    }

                    return success;
                } catch (Exception e) {
                    e.printStackTrace();
                    BuildManager.INSTANCE.sendCaughtException(e);
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {

                    this.getActivity().startActivity(new Intent(this.getActivity(), ActMain_.class));
                    System.exit(0);

                } else {
                    abort("Error");
                }
            }
        }.execute();
    }

    public void restoreTask() {

        if (!externalWriteable()) {
            return;
        }

        new BusyAsyncTask<Boolean>(this, mBusyStyle, true) {

            protected Boolean doInBackground() {

                try {
                    boolean success = false;
                    try {
                        Globo.db.beginTransaction();
                        BackupUtil.flagRestored();
                        success = true;
                    } finally {
                        Globo.db.endTransaction();
                    }

                    if (success) {
                        this.getActivity().startActivity(new Intent(this.getActivity(), ActMain_.class));
                        System.exit(0);
                    }

                    return success;
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

    // ============================== Fragment Attach/Populate
    // =============================

    public void attachAdapters() {

    }

}
