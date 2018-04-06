package com.fproject.cryptolytics.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fproject.cryptolytics.converter.ConverterItem;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ConverterTable {

    private static final String TABLE_CONVERTER = "T_Converter";

    private static final String KEY_ID          = "Id";
    private static final String KEY_SYMBOL = "Symbol";


    private DatabaseManager databaseManager = null;

    /**
     * Constructor.
     */
    protected ConverterTable(DatabaseManager databaseManager){
        this.databaseManager = databaseManager;
    }

    /**
     * Creates the table.
     */
    protected static void onCreate(SQLiteDatabase database) {
        String query = "CREATE TABLE IF NOT EXISTS "
                + TABLE_CONVERTER + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_SYMBOL + " TEXT" + ")";

        database.execSQL(query);
    }

    /**
     * Upgrades the table.
     */
    protected static void onUpgrade(SQLiteDatabase database){
        String query = "DROP TABLE IF EXISTS " + TABLE_CONVERTER;
        database.execSQL(query);

        onCreate(database);
    }

    /**
     * Add an item to the table.
     */
    public long add(String symbol) {
        ContentValues values = new ContentValues();

        values.put(KEY_SYMBOL, symbol);

        return databaseManager.getWritableDatabase().insert(TABLE_CONVERTER, null, values);
    }

    /**
     * Remove an item from the table.
     */
    public void remove(long id){
        SQLiteDatabase database = databaseManager.getWritableDatabase();

        database.delete(TABLE_CONVERTER, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });

        database.close();
    }

    /**
     * Gets the {@link ConverterItem} list from the table.
     */
    public List<ConverterItem> getItems(){
        List<ConverterItem> watchedItems = new ArrayList<>();

        Cursor cursor = databaseManager.getReadableDatabase().query(TABLE_CONVERTER,
                new String[]{KEY_ID, KEY_SYMBOL },
                null, null, null, null, null);

        int id     = cursor.getColumnIndex(KEY_ID);
        int symbol = cursor.getColumnIndex(KEY_SYMBOL);


        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){

            ConverterItem watchedItem = new ConverterItem(cursor.getInt(id),
                    cursor.getString(symbol),
                    " - ");

            watchedItems.add(watchedItem);
        }

        return watchedItems;
    }
}
