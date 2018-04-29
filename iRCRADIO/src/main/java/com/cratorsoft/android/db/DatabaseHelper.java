package com.cratorsoft.android.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cratorsoft.android.dbtable.AccountData;
import com.cratorsoft.android.dbtable.ChannelData;
import com.earthflare.android.ircradio.Globo;
import com.earthflare.android.ircradio.R;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	public static final String DATABASE_NAME = "ircradio";
    private static final int DATABASE_VERSION = 11;
		
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// the DAO objects 
	private Dao<AccountData, Long> accountDao = null;
	private Dao<ChannelData, Long> channelDao = null;
	
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {

		try {
			Log.i(DatabaseHelper.class.getName(), "DB Created");

			String text;

			// InputStream is = this.getResources().openRawResource (R.raw.sql);
			try {
				InputStream is = Globo.ctx.getResources().openRawResource(R.raw.sql);

				// We guarantee that the available method returns the total
				// size of the asset... of course, this does mean that a single
				// asset can't be more than 2 gigs.
				int size = is.available();

				// Read the entire asset into a local byte buffer.
				byte[] buffer = new byte[size];
				is.read(buffer);
				is.close();

				// Convert the buffer into a string.
				text = new String(buffer);

			} catch (IOException e) {
				// Should never happen!
				throw new RuntimeException(e);
			}

			String[] str = text.split(";");
			for (String s : str) {
				db.execSQL(s);
			}

		} catch (Exception e) {
			Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
			int oldVersion, int newVersion) {

		
        if ( newVersion > 1 && oldVersion < 2) {
      	//upgrade to version 2
      	//add new columns for channel language and server encoding  
      	Log.v("y", "Upgrading database from version " + oldVersion 
                  + " to "
                  + newVersion + ", which will not destroy all old data");
          
      	String sql = "Alter table account add column guesscharset text default 'false' not null";
      	String sql2 = "Alter table account add column reconnect text default 'false' not null";
      	String sql3 = "Alter table channel add column autojoin text default 'false' not null";        	
      	String sql4 = "Alter table channel add column language text default 'en' not null";
      	db.execSQL(sql);
      	db.execSQL(sql2);
      	db.execSQL(sql3);
      	db.execSQL(sql4);
      	
        }
        
        if ( newVersion > 2 && oldVersion < 3) {
        	//upgrade to version 3
        	//add new column for ssl
        	Log.v("y", "Upgrading database from version " + oldVersion 
                    + " to "
                    + newVersion + ", which will not destroy all old data");
            
        	String sql = "Alter table account add column ssl text default 'false' not null";          	
        	db.execSQL(sql);          	
        }
        
        if ( newVersion > 3 && oldVersion < 8) {
          	//upgrade to version 3
          	//add new column for ssl
          	Log.v("y", "Upgrading database from version " + oldVersion 
                      + " to "
                      + newVersion + ", which will not destroy all old data");
              
          	            	
          	//clean up nulls
          	//clean up nulls;
          	String sql  = "Update account set guesscharset = 'false' where guesscharset is null";
          	String sql2 = "Update account set reconnect = 'false' where reconnect is null";
          	String sql3 = "Update channel set autojoin = 'false' where autojoin is null";
          	String sql4 = "Update channel set language = 'en' where language is null";
          	String sql5 = "Update channel set language = 'en' where language = '0'";
          	String sql6 = "Update account set ssl = 'false' where ssl is null";
          	db.execSQL(sql);
          	db.execSQL(sql2);
          	db.execSQL(sql3);
          	db.execSQL(sql4);
          	db.execSQL(sql5);
          	db.execSQL(sql6);
          }
        
        if ( newVersion > 8 && oldVersion < 9) {
        	//upgrade to version 9
        	//add new columns on account 
        	Log.v("y", "Upgrading database from version " + oldVersion 
                    + " to "
                    + newVersion + ", which will not destroy all old data");
            
        	            	
        	String sql = "Alter table account add column autoidentify text default 'false' not null";
        	String sql2 = "Update account set autoidentify = 'false' where autoidentify is null";
        	db.execSQL(sql);
        	db.execSQL(sql2);
        	
        	sql = "Alter table account add column nickserv text default 'false' not null";
        	sql2 = "Update account set nickserv = 'NickServ' where nickserv is null";
        	db.execSQL(sql);
        	db.execSQL(sql2);
        	
        	sql = "Alter table account add column nickpass text default '' not null";
        	sql2 = "Update account set nickpass = '' where nickpass is null";
        	db.execSQL(sql);
        	db.execSQL(sql2);
        	
        	sql = "Alter table account add column perform text default '' not null";
        	sql2 = "Update account set perform = '' where perform is null";
        	db.execSQL(sql);
        	db.execSQL(sql2);
        	
        	sql = "Alter table account add column encodingoverride text default 'false' not null";
        	sql2 = "Update account set encodingoverride = 'false' where encodingoverride is null";
        	db.execSQL(sql);
        	db.execSQL(sql2);
        	
        	sql = "Alter table account add column encodingsend text default 'UTF-8' not null";
        	sql2 = "Update account set encodingsend = 'UTF-8' where encodingsend is null";
        	db.execSQL(sql);
        	db.execSQL(sql2);
        	
        	sql = "Alter table account add column encodingreceive text default 'UTF-8' not null";
        	sql2 = "Update account set encodingreceive = 'UTF-8' where encodingreceive is null";
        	db.execSQL(sql);
        	db.execSQL(sql2);
        	
        	sql = "Alter table account add column encodingserver text default 'UTF-8' not null";
        	sql2 = "Update account set encodingserver = 'UTF-8' where encodingserver is null";
        	db.execSQL(sql);
        	db.execSQL(sql2);
        	
        	sql = "Alter table account add column reconnectinterval text default '30' not null";
        	sql2 = "Update account set reconnectinterval = '30' where reconnectinterval is null";
        	db.execSQL(sql);
        	db.execSQL(sql2);
        	
        	sql = "Alter table account add column reconnectretries text default '12' not null";
        	sql2 = "Update account set reconnectretries = '12' where reconnectretries is null";
        	db.execSQL(sql);
        	db.execSQL(sql2);
        	
        }

        if ( newVersion > 9 && oldVersion < 10) {

            String sql = "Alter table account add column language text default 'en' not null";
            String sql2 = "Update account set language = 'en' where language is null";
            db.execSQL(sql);
            db.execSQL(sql2);
        }


		if ( newVersion > 10 && oldVersion < 11) {

			String sql = "Alter table channel add column ttsprefixes text default '' not null";
			db.execSQL(sql);

		}
		
	}

	
	/**
	 * Returns the Database Access Object (DAO) for our ProfileData class. It
	 * will create it or just give the cached value.
	 */
	public Dao<AccountData, Long> getAccountDataDao() throws SQLException {
		if (accountDao == null) {
			accountDao = getDao(AccountData.class);
		}
		return accountDao;
	}

	public Dao<ChannelData, Long> getChannelDataDao() throws SQLException {
        if (channelDao == null) {
        	channelDao = getDao(ChannelData.class);
        }
        return channelDao;
    }
	
}
