package com.cratorsoft.android.db;

import java.sql.SQLException;
import java.util.List;

import com.cratorsoft.android.dbtable.ChannelData;
import com.earthflare.android.ircradio.Globo;
import com.j256.ormlite.stmt.Where;

public class DBQChannel {

	
	public static List<ChannelData> getChannelList(long accountid) throws SQLException {
		
		Where<ChannelData,Long> where = Globo.dbHelper.getChannelDataDao().queryBuilder().orderBy("channel", true).where();
		
		where.eq("accountid", accountid);
		
		return Globo.dbHelper.getChannelDataDao().query(where.prepare());
		
	}


    public static ChannelData getChannelExists(long accountid, String channel) throws SQLException{


        Where<ChannelData,Long> where = Globo.dbHelper.getChannelDataDao().queryBuilder().orderBy("channel", true).where();

        where.eq("accountid", accountid).and().eq("channel",channel);

        return  Globo.dbHelper.getChannelDataDao().queryForFirst(where.prepare());



    }

	
}
