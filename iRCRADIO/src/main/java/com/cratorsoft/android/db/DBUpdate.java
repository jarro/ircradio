package com.cratorsoft.android.db;

import com.cratorsoft.android.aamain.BuildManager;
import com.cratorsoft.android.dbtable.AccountData;
import com.cratorsoft.android.dbtable.ChannelData;
import com.earthflare.android.ircradio.Globo;

import java.sql.SQLException;

/**
 * Created by j on 04/12/14.
 */
public class DBUpdate {

    public static boolean saveAccount (AccountData accountdata){


        int rowsupdated=0;
        try {
            rowsupdated = Globo.dbHelper.getAccountDataDao().update(accountdata);
        } catch (SQLException e) {
            BuildManager.INSTANCE.sendCaughtException(e);
            e.printStackTrace();
        }
        boolean success = (rowsupdated == 1);
        return success;

    }


    public static boolean saveChannel (ChannelData channelData){


        int rowsupdated=0;
        try {
            rowsupdated = Globo.dbHelper.getChannelDataDao().update(channelData);
        } catch (SQLException e) {
            BuildManager.INSTANCE.sendCaughtException(e);
            e.printStackTrace();
        }
        boolean success = (rowsupdated == 1);
        return success;

    }

}
