package com.cratorsoft.android.db;

import java.sql.SQLException;
import java.util.List;

import com.cratorsoft.android.dbtable.AccountData;
import com.earthflare.android.ircradio.Globo;

public class DBQAccount {

    public static List<AccountData> getAccountList() throws SQLException {

        return Globo.dbHelper.getAccountDataDao().queryBuilder().orderBy("accountname", true).query();

    }


    public static AccountData account(long accountId) throws SQLException{

        return Globo.dbHelper.getAccountDataDao().queryForId(accountId);

    }

}
