/*
 * The MIT License
 * Copyright (c) 2011 Santiago Lezica (slezica89@gmail.com)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package com.cratorsoft.android.taskmanager;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.cratorsoft.android.dialog.FragBusy;
import com.cratorsoft.android.drawer.DrawerDisplayer;
import com.earthflare.android.ircradio.Globo;
import com.earthflare.android.ircradio.R;
import com.j256.ormlite.dao.CloseableIterator;

/**
 * 
 * To use title spinner requires window feature requested by activity To use
 * content spinner requires layout has progress container similar to listview
 * list_content
 * 
 */
public class BusyTaskFragment extends Fragment implements TaskManager {

    protected final Object mLock = new Object();

    protected Boolean mReady = false;
    protected boolean mDestroyed = false;

    protected LinkedBlockingQueue<Cursor> cursorCleanup = new LinkedBlockingQueue<Cursor>();
    protected LinkedBlockingQueue<CloseableIterator<?>> cleanupCloseableIterator = new LinkedBlockingQueue<CloseableIterator<?>>();
    protected LinkedBlockingQueue<Cursor> cursorRefreshCleanup = new LinkedBlockingQueue<Cursor>();
    protected LinkedBlockingQueue<CloseableIterator<?>> cleanupRefreshCloseableIterator = new LinkedBlockingQueue<CloseableIterator<?>>();

    protected List<Runnable> mPendingCallbacks = new LinkedList<Runnable>();

    protected String mBusyMessage = "Busy...";

    View mProgressContainer;
    View mContentContainer;

    // counters
    protected int cntModal = 0;

    protected int cntDialogDark = 0;
    protected int cntDialogLight = 0;
    protected int cntTitle = 0;
    protected int cntContent = 0;

    // prevent poste execution on non match
    protected int mExecuteId = 1;

    public void setBusyMessage(String busymessage) {
        this.mBusyMessage = busymessage;
        Runnable run = new Runnable() {

            @Override
            public void run() {

            }

        };

    }

    /**
     * increment and start busy dialog if not busy
     */
    public void incrementBusy(int busyType) {

        synchronized (mLock) {

            switch (busyType) {
            case BUSY.MODAL_DARK_DIALOG:
                cntModal++;
                cntDialogDark++;
                if (!mReady) {
                    break;
                }
                if (cntModal == 1) {
                    setModal(true);
                }
                if (cntDialogDark == 1) {
                    showBusyDarkDialog();
                }
                break;
            case BUSY.MODAL_LIGHT_DIALOG:
                cntModal++;
                cntDialogLight++;
                if (!mReady) {
                    break;
                }
                if (cntModal == 1) {
                    setModal(true);
                }
                if (cntDialogLight == 1) {
                    showBusyLightDialog();
                }
                break;
            case BUSY.MODAL_TITLE:
                cntModal++;
                cntTitle++;
                if (!mReady) {
                    break;
                }
                if (cntModal == 1) {
                    setModal(true);
                }
                if (cntTitle == 1) {
                    showBusyTitle();
                }
                break;
            case BUSY.MODAL_CONTENT:
                cntModal++;
                cntContent++;
                if (!mReady) {
                    break;
                }
                if (cntModal == 1) {
                    setModal(true);
                }
                if (cntContent == 1) {
                    showBusyContent(true);
                }
                break;
            case BUSY.NONMODAL_TITLE:
                cntTitle++;
                if (!mReady) {
                    break;
                }
                if (cntTitle == 1) {
                    showBusyTitle();
                }
                break;
            case BUSY.NONMODAL_CONTENT:
                cntContent++;
                if (!mReady) {
                    break;
                }
                if (cntContent == 1) {
                    showBusyContent(true);
                }
                break;
            }

        }

    }

    public void resumeBusy() {
        synchronized (mLock) {
            if (cntModal > 0) {
                setModal(true);
            }
            if (cntDialogDark > 0) {
                showBusyDarkDialog();
            }
            if (cntDialogLight > 0) {
                showBusyLightDialog();
            }
            if (cntTitle > 0) {
                showBusyTitle();
            }
            if (cntContent > 0) {
                showBusyContent(false);
            }
        }

    }

    public void setModal(boolean modal) {
        //UIUtil.enableDisableViewGroup((ViewGroup) this.getView(), !modal);
    }

    public void showBusyDarkDialog() {
        FragBusy mBusyDialog = FragBusy.newInstance(mBusyMessage);
        mBusyDialog.setCancelable(false);
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        mBusyDialog.show(ft, "busy_darkdialog");
        getChildFragmentManager().executePendingTransactions();
    }

    public void dismissBusyDarkDialog() {
        FragBusy dialog = (FragBusy) this.getChildFragmentManager().findFragmentByTag("busy_darkdialog");
        if (dialog != null) {
            dialog.getDialog().dismiss();
        }
    }

    public void showBusyLightDialog() {
        FragBusy mBusyDialog = FragBusy.newInstance(mBusyMessage);
        mBusyDialog.setCancelable(false);
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        mBusyDialog.show(ft, "busy_lightdialog");
        getChildFragmentManager().executePendingTransactions();
    }

    public void dismissBusyLightDialog() {
        FragBusy dialog = (FragBusy) this.getChildFragmentManager().findFragmentByTag("busy_lightdialog");
        if (dialog != null) {
            dialog.getDialog().dismiss();
        }
    }

    public void showBusyTitle() {
        this.getActivity().setProgressBarIndeterminateVisibility(true);
    }

    public void dismissBusyTitle() {
        this.getActivity().setProgressBarIndeterminateVisibility(false);
    }

    public void showBusyContent(boolean animate) {
        showContentSpinner(true, animate);
    }

    public void dismissBusyContent() {
        showContentSpinner(false, true);
    }

    /**
     * decrement and dismiss busy dialog if not busy anymore
     */
    public void decrementBusy(int busyType) {
        synchronized (mLock) {
            switch (busyType) {
            case BUSY.MODAL_DARK_DIALOG:
                cntModal--;
                cntDialogDark--;
                if (cntModal == 0) {
                    setModal(false);
                }
                if (cntDialogDark == 0) {
                    dismissBusyDarkDialog();
                }
                break;
            case BUSY.MODAL_LIGHT_DIALOG:
                cntModal--;
                cntDialogLight--;
                if (cntModal == 0) {
                    setModal(false);
                }
                if (cntDialogLight == 0) {
                    dismissBusyLightDialog();
                }
                break;
            case BUSY.MODAL_TITLE:
                cntModal--;
                cntTitle--;
                if (cntModal == 0) {
                    setModal(false);
                }
                if (cntTitle == 0) {
                    dismissBusyTitle();
                }
                break;
            case BUSY.MODAL_CONTENT:
                cntModal--;
                cntContent--;
                if (cntModal == 0) {
                    setModal(false);
                }
                if (cntContent == 0) {
                    dismissBusyContent();
                }
                break;
            case BUSY.NONMODAL_TITLE:
                cntTitle--;
                if (cntTitle == 0) {
                    dismissBusyTitle();
                }
                break;
            case BUSY.NONMODAL_CONTENT:
                cntContent--;
                if (cntContent == 0) {
                    dismissBusyContent();
                }
                break;
            }

        }
    }

    public boolean isBusy() {
        synchronized (mLock) {
            if (cntDialogDark > 0 || cntDialogLight > 0 || cntTitle > 0 || cntContent > 0) {
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean isModal() {
        synchronized (mLock) {
            if (cntModal > 0) {
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setRetainInstance(true);
        } catch (Exception e) {
            // Nested Fragment can't retaininstance
        }

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgressContainer = this.getView().findViewById(R.id.progressContainer);
        mContentContainer = this.getView().findViewById(R.id.listContainer);

    }

    @Override
    public void onPause() {
        super.onPause();

        synchronized (mLock) {
            mReady = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        this.resumeBusy();
        synchronized (mLock) {
            mReady = true;

            int pendingCallbacks = mPendingCallbacks.size();

            while (pendingCallbacks-- > 0)
                runNow(mPendingCallbacks.remove(0));
        }
    }

    public boolean isReady() {
        synchronized (mLock) {
            return mReady;
        }
    }

    protected void setReady(boolean ready) {
        synchronized (mLock) {
            mReady = ready;
        }
    }

    protected void addPending(Runnable runnable) {
        synchronized (mLock) {
            mPendingCallbacks.add(runnable);
        }
    }

    public void runWhenReady(Runnable runnable) {
        if (isReady()) {

            runNow(runnable);
        } else {
            addPending(runnable);
        }
    }

    protected void runNow(Runnable runnable) {

        getActivity().runOnUiThread(runnable);
    }

    /**
     * Android spinner
     * 
     */
    public void showContentSpinner(boolean showspinner, boolean animate) {
        if (!showspinner && mProgressContainer.getVisibility() == View.VISIBLE) {
            if (animate) {
                mProgressContainer.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_out));
                mContentContainer.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_in));
            } else {
                mProgressContainer.clearAnimation();
                mContentContainer.clearAnimation();
            }
            mProgressContainer.setVisibility(View.GONE);
            mContentContainer.setVisibility(View.VISIBLE);
        } else if (showspinner && mProgressContainer.getVisibility() == View.GONE) {
            if (animate) {
                mProgressContainer.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_in));
                mContentContainer.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_out));
            } else {
                mProgressContainer.clearAnimation();
                mContentContainer.clearAnimation();
            }
            mProgressContainer.setVisibility(View.VISIBLE);
            mContentContainer.setVisibility(View.GONE);
        }
    }

    protected void cleanupDBConnections() {

        Runnable r = new Runnable() {

            @Override
            public void run() {

                while (cursorCleanup.size() > 0) {
                    try {
                        cursorCleanup.remove().close();
                    } finally {
                    }
                }
                while(cleanupCloseableIterator.size() > 0){
                    try{
                        cleanupCloseableIterator.remove().closeQuietly();
                    }finally{}
                }
            }

        };

        Globo.dbHandler.post(r);
    }

    protected void addCleanupCursor(Cursor c) {
        if (this.mDestroyed) {
            c.close();
        } else {
            try {
                cursorCleanup.put(c);
            } catch (InterruptedException e) {
                c.close();
            }
        }
    }

    protected void cleanupRefreshDBConnections() {

        Runnable r = new Runnable() {

            @Override
            public void run() {

                while (cursorRefreshCleanup.size() > 0) {
                    try {
                        cursorRefreshCleanup.remove().close();
                    } finally {
                    }
                }
                while(cleanupRefreshCloseableIterator.size() > 0){
                    try{
                        cleanupRefreshCloseableIterator.remove().closeQuietly();
                    }finally{}
                }
            }

        };

        Globo.dbHandler.post(r);
    }

    protected void addRefreshCleanupCursor(Cursor c) {
        if (this.mDestroyed) {
            c.close();
        } else {
            try {
                cursorRefreshCleanup.put(c);
            } catch (InterruptedException e) {
                c.close();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mDestroyed = true;
        cleanupDBConnections();
        cleanupRefreshDBConnections();
    }

    protected void abort(String message) {
        Toast.makeText(this.getActivity(), message, Toast.LENGTH_SHORT).show();
        this.getActivity().finish();
    }

    public DrawerDisplayer getDrawerDisplayer(){
        return (DrawerDisplayer) this.getActivity();
    }
}
