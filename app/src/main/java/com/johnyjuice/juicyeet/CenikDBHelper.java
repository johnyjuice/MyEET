package com.johnyjuice.juicyeet;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
/**
 * Created by Johny on 04.11.2016.
 */

public class CenikDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "SQLCenik.db";
    private static final int DATABASE_VERSION = 2;

    public static final String CENIK_TABLE_NAME = "cenik";
    public static final String CENIK_COLUMN_ID = "_id";
    public static final String CENIK_COLUMN_ZBOZI = "zbozi";
    public static final String CENIK_COLUMN_CENA = "cena";


    public CenikDBHelper(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + CENIK_TABLE_NAME +
                        "(" + CENIK_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                        CENIK_COLUMN_ZBOZI + " TEXT, " +
                        CENIK_COLUMN_CENA + " REAL)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CENIK_TABLE_NAME);
        onCreate(db);
    }

    public boolean vlozZbozi(String zbozi, float cena) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CENIK_COLUMN_ZBOZI, zbozi);
        contentValues.put(CENIK_COLUMN_CENA, cena);
        db.insert(CENIK_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean opravZbozi(Integer id, String zbozi, float cena) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CENIK_COLUMN_ZBOZI, zbozi);
        contentValues.put(CENIK_COLUMN_CENA, cena);
        db.update(CENIK_TABLE_NAME, contentValues, CENIK_COLUMN_ID + " = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer vymazZbozi(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(CENIK_TABLE_NAME,
                CENIK_COLUMN_ID + " = ? ",
                new String[] { Integer.toString(id) });
    }

    public Cursor vyberZbozi(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + CENIK_TABLE_NAME + " WHERE " +
                CENIK_COLUMN_ID + "=?", new String[]{Integer.toString(id)});
        return res;
    }

    public Cursor vyberVsechnoZbozi() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM " + CENIK_TABLE_NAME, null );
        return res;
    }

    public void deleteAll()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(CENIK_TABLE_NAME,null,null);


    }

}
