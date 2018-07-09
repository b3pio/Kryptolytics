package com.fproject.cryptolitycs.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 *
 * @author lszathmary
 */
public class DatabaseManager extends SQLiteOpenHelper {

    private static final String DATABASE_NAME       = "CryptoBase";
    private static final int    DATABASE_VERSION    = 4;

    private WatchListTable watchListTable;
    private ConverterTable converterTable;

    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        watchListTable = new WatchListTable(this);
        converterTable = new ConverterTable(this);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        WatchListTable.onCreate(database);
        ConverterTable.onCreate(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        WatchListTable.onUpgrade(database);
        ConverterTable.onUpgrade(database);
    }

    public WatchListTable getWatchListTable(){
        return watchListTable;
    }

    public ConverterTable getConverterTable(){
        return converterTable;
    }
}
