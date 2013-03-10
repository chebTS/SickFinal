package org.geekhub.tsibrovsky.sickukrainefinal.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	public static DatabaseHelper instance;

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "cheb.db";
	
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    
	@Override
	public void onCreate(SQLiteDatabase db) {
		ArticlesTable.onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		ArticlesTable.onUpgrade(db, oldVersion, newVersion);
	}
}
