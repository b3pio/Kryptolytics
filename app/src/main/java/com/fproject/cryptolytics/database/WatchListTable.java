package com.fproject.cryptolytics.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fproject.cryptolytics.watchlist.WatchedItem;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class WatchListTable {
    private static final String TABLE_WATCHLIST = "T_WatchList";

    private static final String KEY_ID          = "Id";
    private static final String KEY_FROM_SYMBOL = "FromSymbol";
    private static final String KEY_TO_SYMBOL   = "ToSymbol";

    private DatabaseManager databaseManager = null;

    /**
     * Constructor.
     */
    protected WatchListTable(DatabaseManager databaseManager){
        this.databaseManager = databaseManager;
    }

    /**
     * Creates the table.
     */
    protected static void onCreate(SQLiteDatabase database) {
        String query = "CREATE TABLE IF NOT EXISTS "
                + TABLE_WATCHLIST + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_FROM_SYMBOL + " TEXT, "
                + KEY_TO_SYMBOL + " TEXT" + ")";

        database.execSQL(query);
    }

    /**
     * Upgrades the table.
     */
    protected static void onUpgrade(SQLiteDatabase database){
        String query = "DROP TABLE IF EXISTS " + TABLE_WATCHLIST;
        database.execSQL(query);

        onCreate(database);
    }

    /**
     * Add an item to the table.
     */
    public long add(String fromSymbol, String toSymbol){
        ContentValues values = new ContentValues();

        values.put(KEY_FROM_SYMBOL, fromSymbol);
        values.put(KEY_TO_SYMBOL,   toSymbol);

        return databaseManager.getWritableDatabase().insert(TABLE_WATCHLIST, null, values);
    }

    /**
     * Remove an item from the table.
     */
    public void remove(long id){
        SQLiteDatabase database = databaseManager.getWritableDatabase();

        database.delete(TABLE_WATCHLIST, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });

        database.close();
    }

    /**
     * Gets the {@link WatchedItem} list from the table.
     */
    public List<WatchedItem> getItems(){
        List<WatchedItem> watchedItems = new ArrayList<>();

        Cursor cursor = databaseManager.getReadableDatabase().query(TABLE_WATCHLIST,
                new String[]{KEY_ID, KEY_FROM_SYMBOL, KEY_TO_SYMBOL},
                null, null, null, null, null);

        int id         = cursor.getColumnIndex(KEY_ID);
        int fromSymbol = cursor.getColumnIndex(KEY_FROM_SYMBOL);
        int toSymbol   = cursor.getColumnIndex(KEY_TO_SYMBOL);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){

            WatchedItem watchedItem = new WatchedItem(cursor.getInt(id),
                    cursor.getString(fromSymbol),
                    cursor.getString(toSymbol));

            watchedItems.add(watchedItem);
        }

        return watchedItems;
    }


}
