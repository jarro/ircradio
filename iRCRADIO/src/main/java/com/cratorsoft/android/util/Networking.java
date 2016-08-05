package com.cratorsoft.android.util;

import android.content.Context;
import android.net.ConnectivityManager;

import com.earthflare.android.ircradio.Globo;

/**
 * Created by j on 04/12/14.
 */
public class Networking {

    public static boolean haveNet(){

        ConnectivityManager connec = (ConnectivityManager) Globo.ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connec.getActiveNetworkInfo() == null)
            return false;
        if (connec.getActiveNetworkInfo().isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }

    }

}
