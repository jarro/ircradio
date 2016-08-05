package com.cratorsoft.android.toaster;

import android.content.Context;
import android.widget.Toast;

import com.earthflare.android.ircradio.Globo;

/**
 * Created by j on 03/12/14.
 */
public class Toaster {

    public static void toast(Context ctx, String message){

        if (ctx == null){
            return;
        }else{

            try {
                Globo.toast.cancel();
            } catch (Exception e) {}

            Globo.toast = Toast.makeText(ctx, message, Toast.LENGTH_LONG);
            Globo.toast.show();
        }

    }

}
