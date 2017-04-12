package com.earthflare.android.ircradio;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.cratorsoft.android.aamain.ActMain_;
import com.cratorsoft.android.receiver.RemoteReceiver;
import com.cratorsoft.android.ttslanguage.TTSPipeline;
import com.earthflare.android.logmanager.LogManager;

public class IrcRadioService extends android.app.Service {

    private final static int MY_DATA_CHECK_CODE = 1;
    private BotManager botManager = new BotManager();
    private TelephonyManager tm;
    private IrcRadioService thisService;


    HeadPhone receiver;

    private LanguageResource voiceResource;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();


        this.startForeground(1, this.getNotification());

        GloboUtil.globalizePrefs(this);
        thisService = this;

        Globo.botManager = botManager;
        Globo.AM = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

        Globo.wifiLocker = new WifiLocker(this);




        //voiceResource = new LanguageResource(this);
        //Globo.voiceResource = voiceResource;

        //start phone state listener
        tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(mPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        if (TelephonyManager.CALL_STATE_IDLE != tm.getCallState()) {
            Globo.PauseTTS();
        }

        registerHeadphoneListener();

    }

    private void registerHeadphoneListener() {

        int mode = Globo.AM.getRouting(AudioManager.MODE_NORMAL);
        Globo.headphoneconnected = (mode == AudioManager.ROUTE_HEADSET) ? true : false;

        receiver = new HeadPhone();
        IntentFilter filter = new IntentFilter("android.intent.action.HEADSET_PLUG");
        this.registerReceiver(receiver, filter);

    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);


        String action = intent.getStringExtra("action");

        if (action.equals("flush")) {

        }


        if (action.equals("connect")) {
            botManager.addChannel(this, intent);
        }


        if (action.equals("kill")) {

            final BotManager temp = botManager;
            botManager = new BotManager();
            BotManager.refreshUI(this);
            Globo.botManager = botManager;

            Runnable killbots = new Runnable() {
                @Override
                synchronized public void run() {
                    // TODO Auto-generated method stub
                    temp.kill();
                }

            };
            new Thread(killbots).start();

            this.stopSelf();


        }

        if (action.equals("updateicon")) {

            if (!dieOnNoServersConnecting()) {
                Notification notif = getNotification();
                NotificationManager nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                nm.notify(1, notif);
            }

        }

        return START_NOT_STICKY;

    }


    public void returnError(String message, String trace) {

        Message msg = new Message();
        Bundle data = new Bundle();
        data.putString("action", "error");
        data.putString("message", message);
        data.putString("trace", trace);
        msg.setData(data);
        Globo.handlerAccountList.sendMessage(msg);
    }


    private PhoneStateListener mPhoneListener = new PhoneStateListener() {

        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:

                    //unpause service
                    Globo.UnPauseTTS();


                    break;
                default:
                    //pause service
                    Globo.PauseTTS();

                    break;
            }
        }
    };


    private boolean dieOnNoServersConnecting() {


        int[] counters = BotManager.getConnectedCount();


        int serversconnected = counters[0];
        int serversconnecting = counters[2];


        //destroy service if servers = 0
        if (serversconnecting > 0 || serversconnected > 0) {

            lockWifi();
            return false;
        } else {
            lockWifi();
            this.stopSelf();
            return true;
        }


    }

    private Notification getNotification() {

        int[] counters = BotManager.getConnectedCount();

        int[] logcounters = LogManager.countChecked();


        int channels = counters[1];
        int serversconnected = counters[0];
        int serversconnecting = counters[2];

        int logservers = logcounters[0];
        int logserversunread = logcounters[1];
        int logchannels = logcounters[2];
        int logchannelsunread = logcounters[3];
        int logprivate = logcounters[4];
        int logprivateunread = logcounters[5];


        String title = getString(R.string.notice_serversconnected) + " " + serversconnected +"  " +
                getString(R.string.notice_channelsjoined) + " "  + channels ;
        String message = getString(R.string.notice_privatemessages) + " "  + logprivateunread;

        //configure expanded statusbar notice



        Intent statusintent = new Intent(Intent.ACTION_MAIN);
        statusintent.addCategory(Intent.CATEGORY_LAUNCHER);
        statusintent.setComponent(new ComponentName(this.getApplicationContext(), ActMain_.class));

        int statusicon;
        if (logprivateunread == 0) {
            statusicon = R.drawable.ic_stat_gg_white_notification;
        } else {
            statusicon = R.drawable.ic_stat_gg_white_notification;
        }


        NotificationCompat.Builder nbuilder = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(statusicon);


        //purge intent
        Intent purgeIntent = new Intent(this, RemoteReceiver.class);
        purgeIntent.setAction(RemoteReceiver.ACTION_PURGE_MESSAGES);
        PendingIntent pendingPurgeIntent = PendingIntent.getBroadcast(this, 0, purgeIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        nbuilder.addAction(R.drawable.icn_nuke, "Purge", pendingPurgeIntent);


        //toggle tts
        Intent ttsIntent = new Intent(this, RemoteReceiver.class);
        ttsIntent.setAction(RemoteReceiver.ACTION_TOGGLE_TTS);
        PendingIntent pendingTTSIntent = PendingIntent.getBroadcast(this, 0, ttsIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        if(Globo.mutestate){
            nbuilder.addAction(R.drawable.icn_volume_off, "Unmute", pendingTTSIntent);
        }else{
            nbuilder.addAction(R.drawable.icn_volume_high, "Mute", pendingTTSIntent);
        }

        //toggle toast
        Intent toastIntent = new Intent(this, RemoteReceiver.class);
        toastIntent.setAction(RemoteReceiver.ACTION_TOGGLE_TOAST);
        PendingIntent pendingToastIntent = PendingIntent.getBroadcast(this, 0, toastIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        if(Globo.toaststate){
            nbuilder.addAction(R.drawable.icn_message_text, "Toast", pendingToastIntent);
        }else{
            nbuilder.addAction(R.drawable.icn_message, "Toastless", pendingToastIntent);
        }



        Notification status = nbuilder.build();
        status.flags |= Notification.FLAG_ONGOING_EVENT;

        status.contentIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, statusintent, PendingIntent.FLAG_UPDATE_CURRENT);


        return status;
    }


    private void cleanup() {

        this.unregisterReceiver(this.receiver);

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancelAll();
        TTSPipeline.getInstance().reset();
        Globo.wifiLocker.release();
        GloboUtil.abandonAudioFocus();

        tm.listen(mPhoneListener, PhoneStateListener.LISTEN_NONE);
        mPhoneListener = null;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        cleanup();
        super.onDestroy();

    }


    //Keeps wifi turned on while connected.
    public void lockWifi() {

        int[] counters = BotManager.getConnectedCount();
        if (counters[0] > 0) {
            Globo.wifiLocker.acquire();
        } else {
            Globo.wifiLocker.release();
        }


    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }


}
