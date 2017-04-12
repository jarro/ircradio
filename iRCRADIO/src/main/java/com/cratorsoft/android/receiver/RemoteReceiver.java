package com.cratorsoft.android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.earthflare.android.ircradio.Globo;

/**
 * Created by j on 4/11/2017.
 */

public class RemoteReceiver extends BroadcastReceiver {


    public static final String ACTION_TOGGLE_TOAST = "toggle_toast";
    public static final String ACTION_TOGGLE_TTS = "toggle_tts";
    public static final String ACTION_PURGE_MESSAGES = "purge_messages";

    public static long overflow = 0;

    @Override
    public void onReceive(Context context, Intent intent) {

        long now = System.currentTimeMillis();
        if(now - overflow < 250){
            return;
        }
        overflow = now;

        if (intent.getAction() != null) {

            if (intent.getAction().equals("purge_messages")) {

                Globo.purgeTTS();

            } else if (intent.getAction().equals("toggle_tts")) {

                Globo.togglemuteTTS();

            } else if (intent.getAction().equals("toggle_toast")) {

                Globo.toggleToastState();

            }



        }

    }
}
