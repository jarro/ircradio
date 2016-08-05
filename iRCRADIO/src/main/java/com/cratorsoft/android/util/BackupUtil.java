package com.cratorsoft.android.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.widget.Toast;

import com.cratorsoft.android.aamain.BuildManager;
import com.cratorsoft.android.db.DatabaseHelper;
import com.earthflare.android.ircradio.Globo;
import com.earthflare.android.ircradio.R;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.io.File;


public class BackupUtil {

	public Context ctx;
	
	
	public String backuppathnosdcard;
	public String backupfolder;
	public String backuppath;
	public String databasepath;

	public BackupUtil(Context ctx) {		
		this.ctx = ctx;
		initPaths();
	}
	
	
	private void initPaths() {

		String appfolder = this.ctx.getString(R.string.app_storagefolder);
        String dbname = DatabaseHelper.DATABASE_NAME;
		String external = Environment.getExternalStorageDirectory().getPath();
		
		backuppathnosdcard =  appfolder + "backup.db";
		backupfolder = external + appfolder;
		backuppath = external + appfolder + "backup.db";
		databasepath = Environment.getDataDirectory() + "/data/" + ctx.getPackageName() + "/databases/"
				+ dbname;
	}
	
	public boolean backup() {		
		try{
			File outfolder = new File(backupfolder);
			outfolder.mkdirs();
			File outfile = new File(backuppath);
			outfile.createNewFile();
			File infile = new File(databasepath);
			FileUtil.copyFile(infile, outfile);
			return true;
		}catch(Exception e){
		    BuildManager.INSTANCE.sendCaughtException(e);
			return false;
		}
	}
	
	
	public boolean restore() {
		try{
			File backupfile = new File(backuppath);
			File dbfile = new File(databasepath);
			FileUtil.copyFile(backupfile, dbfile);
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	
	//set preference to app backed up
	public static void flagRestored(){				
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Globo.ctx);
		preferences.edit().putBoolean("restored_db", true).apply();
	}
	
	
	//set preference to app backed up
	public static void flagBackuped(){				
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Globo.ctx);
		preferences.edit().putBoolean("backuped_db", true).apply();
	}
	
	
	//set preference to app backed up
	public static boolean testRestored(){				
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Globo.ctx);
		boolean restored = preferences.getBoolean("restored_db", false);
		//D.d("restored = " + restored);
		if (restored){			
			preferences.edit().putBoolean("restored_db", false).apply();
		}
		return restored;
	}
	

	//set preference to app backed up
	public static boolean testBackuped(){				
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Globo.ctx);
		boolean backuped = preferences.getBoolean("backuped_db", false);
		//D.D("backuped = " + backuped);
		if (backuped){			
			preferences.edit().putBoolean("backuped_db", false).apply();
		}
		return backuped;
	}


    public static void backuprestore(){

        boolean backuped = BackupUtil.testBackuped();
        boolean restored = BackupUtil.testRestored();

        if (backuped || restored){

            //open and close db to clean up any rollbacks
            //set journal mode to delete in case it's an htc device using wal (write ahead logging)

            DatabaseHelper dbhelper = OpenHelperManager.getHelper(Globo.ctx, DatabaseHelper.class);
            SQLiteDatabase db = dbhelper.getWritableDatabase();

            db.rawQuery("PRAGMA journal_mode = DELETE", null);

            dbhelper.close();

        }



        String message = "";
        //do backup
        if (backuped){
            //backup
            BackupUtil bu = new BackupUtil(Globo.ctx);

            if (bu.backup() ){
                message = Globo.ctx.getString(R.string.backup_Complete);
            }else{
                message = Globo.ctx.getString(R.string.backup_errorbackup);
            }
        }



        //do restore
        if (restored){
            BackupUtil bu = new BackupUtil(Globo.ctx);
            if ( bu.restore() ){
                message = Globo.ctx.getString(R.string.backup_CompleteRestore);
            }else{
                message = Globo.ctx.getString(R.string.backup_errorrestore);
            }
        }


        if (backuped || restored){

            final String finalmessage = message;
            Handler h = new Handler(Globo.ctx.getMainLooper());
            h.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(Globo.ctx, finalmessage, Toast.LENGTH_LONG).show();
                }});


        }

    }

}
