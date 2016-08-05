package com.cratorsoft.android.db;

import android.database.SQLException;

import com.earthflare.android.ircradio.Globo;

/**
 * Created by j on 05/12/14.
 */
public class DBDelete {


    public static void deleteAccount(long accountid){

        try {
            Globo.db.beginTransaction();
            Globo.db.execSQL("delete from account where _id = " + accountid);
            Globo.db.execSQL("delete from channel where accountid = " + accountid);
            Globo.db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally{
            Globo.db.endTransaction();
        }


    }


    public static void deleteChannel(long channelid){

        Globo.db.execSQL("delete from channel where _id = " + channelid);

    }

}
