package com.cratorsoft.android.db;

import com.cratorsoft.android.aamain.BuildManager;
import com.cratorsoft.android.dbtable.AccountData;
import com.cratorsoft.android.dbtable.ChannelData;
import com.earthflare.android.ircradio.Globo;

import java.sql.SQLException;

/**
 * Created by j on 04/12/14.
 */
public class DBInsert {


    public static boolean account(final AccountData accountData) {

        boolean success = false;
        try {

            int prowsupdated = Globo.dbHelper.getAccountDataDao().create(accountData);

            if (prowsupdated != 1) {
                throw new SQLException("error creating account");
            } else {
                return true;
            }

        } catch (SQLException e) {
            BuildManager.INSTANCE.sendCaughtException(e);
            e.printStackTrace();
        }

        return success;

    }


    public static boolean channel(final ChannelData channelData) {

        boolean success = false;
        try {

            int prowsupdated = Globo.dbHelper.getChannelDataDao().create(channelData);

            if (prowsupdated != 1) {
                throw new SQLException("error creating channel");
            } else {
                return true;
            }

        } catch (SQLException e) {
            BuildManager.INSTANCE.sendCaughtException(e);
            e.printStackTrace();
        }

        return success;

    }

}
