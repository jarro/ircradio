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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


import android.support.v7.app.ActionBarActivity;



public abstract class BusyAsyncTask<Result> {

	
	
	
    private BusyTaskFragment mManager;
    private InternalAsyncTask mTask;
    private int mBusyType;
    private int mExecuteId = 0;
     
    
    public BusyAsyncTask(BusyTaskFragment fragment, int busyType, boolean executeAlways) {
    	
        mManager = fragment;        
        mTask = new InternalAsyncTask();
        mBusyType = busyType;        
        fragment.incrementBusy(busyType);
        
        if (executeAlways == false){
        	mManager.mExecuteId++;
        	mExecuteId = mManager.mExecuteId;
        }
    }

    protected void onPreExecute() {}

    protected abstract Result doInBackground();

    protected void onProgressUpdate() {}

    protected void onPostExecute(Result result) {
    	if ( !(mExecuteId == 0  || mExecuteId == mManager.mExecuteId) ){
    		//abort stale task
    		return;
    	}
    }

    protected void onCancelled() {}

    public BusyAsyncTask<Result> execute() {        
        mTask.execute();
        return this;
    }

       
    
    public ActionBarActivity getActivity() {
        return (ActionBarActivity)mManager.getActivity();
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        return mTask.cancel(mayInterruptIfRunning);
    }

    public boolean isCancelled() {
        return mTask.isCancelled();
    }

    public Result get() throws InterruptedException, ExecutionException {
        return mTask.get();
    }

    public Result get(long timeout, TimeUnit unit) throws InterruptedException,
            ExecutionException, TimeoutException {
        return mTask.get(timeout, unit);
    }

    public BusyAsyncTaskBase.Status getStatus() {
        return mTask.getStatus();
    }

    protected class InternalAsyncTask extends
            BusyAsyncTaskBase<Void, Void, Result> {

        @Override
        protected void onPreExecute() { 
        	
            BusyAsyncTask.this.onPreExecute();
        }

        @Override
        protected Result doInBackground(Void... params) {
            return BusyAsyncTask.this.doInBackground();
        }

        protected void onProgressUpdate(final Void... values) {
            mManager.runWhenReady(new Runnable() {
                public void run() {
                    BusyAsyncTask.this.onProgressUpdate();
                }
            });

            return;
        };

        protected void onPostExecute(final Result result) {
            mManager.runWhenReady(new Runnable() {
                public void run() {
                	
                	BusyAsyncTask.this.onPostExecute(result);
                	mManager.decrementBusy(mBusyType);
                }
            });

            return;
        }

        @Override
        protected void onCancelled() {
            mManager.runWhenReady(new Runnable() {
                public void run() {
                    BusyAsyncTask.this.onCancelled();
                }
            });
            
            return;
        }
    }
}
