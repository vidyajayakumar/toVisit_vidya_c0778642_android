package com.vidya.toVisit_vidya_c0778642_android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.vidya.toVisit_vidya_c0778642_android.networking.Favourites;

import java.util.ArrayList;

public
class dbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "favourites";
    private static final String TABLE_FAVOURITE = "favourite";

    private static final String COLUMN_ID = BaseColumns._ID;
    private static final String COLUMN_ADDRESS = "favAddress";
    private static final String COLUMN_DATE = "favDate";
    private static final String COLUMN_LAT = "favLat";
    private static final String COLUMN_LNG = "favLng";
    private static final String COLUMN_VISITED = "favVisited";

    public
    dbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public
    void onCreate(SQLiteDatabase db) {
        String CREATE_FAVOURITES_TABLE = "CREATE	TABLE " + TABLE_FAVOURITE + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_ADDRESS + " TEXT," +
                COLUMN_DATE + " TEXT," +
                COLUMN_LAT + " DOUBLE," +
                COLUMN_LNG + " DOUBLE," +
                COLUMN_VISITED + " BOOLEAN" +
                ")";
        db.execSQL(CREATE_FAVOURITES_TABLE);
    }

    @Override
    public
    void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVOURITE);
        onCreate(db);
    }

    public
    ArrayList<Favourites> listFavourites() {
        String                sql             = "select * from " + TABLE_FAVOURITE;
        SQLiteDatabase        db              = this.getReadableDatabase();
        ArrayList<Favourites> storeFavourites = new ArrayList<>();
        Cursor                cursor          = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                int     id      = Integer.parseInt(cursor.getString(0));
                String  address = cursor.getString(1);
                String  date    = cursor.getString(2);
                double  lat     = cursor.getDouble(3);
                double  lng     = cursor.getDouble(4);
                boolean visited = cursor.getInt(5) > 0;
                storeFavourites.add(new Favourites(id, address, date, lat, lng, visited));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return storeFavourites;
    }


    public
    void addFavourites(Favourites favourites) {
        ContentValues values = new ContentValues();
//        values.put(COLUMN_ID,favourites.get_id());
        values.put(COLUMN_ADDRESS, favourites.getFavAddress());
        values.put(COLUMN_DATE, favourites.getFavDate());
        values.put(COLUMN_LAT, favourites.getFavLat());
        values.put(COLUMN_LNG, favourites.getFavLng());
        values.put(COLUMN_VISITED, favourites.isFavVisited());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_FAVOURITE, null, values);
    }

    public
    void updateFavourites(Favourites favourites) {
        ContentValues values = new ContentValues();
//        values.put(COLUMN_ID, favourites.get_id());
        values.put(COLUMN_ADDRESS, favourites.getFavAddress());
        values.put(COLUMN_DATE, favourites.getFavDate());
        values.put(COLUMN_LAT, favourites.getFavLat());
        values.put(COLUMN_LNG, favourites.getFavLng());
        values.put(COLUMN_VISITED, favourites.isFavVisited());
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_FAVOURITE, values, COLUMN_ID + "	= ?", new String[]{String.valueOf(favourites.get_id())});
    }

    public
    void updateFavVisited(Favourites favourites) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_VISITED, favourites.isFavVisited());
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_FAVOURITE, values, COLUMN_ID + "	= ?", new String[]{String.valueOf(favourites.get_id())});
    }

    public
    Favourites findFavourites(String name) {
        String         query      = "Select * FROM " + TABLE_FAVOURITE + " WHERE " + COLUMN_ID + " = " + "name";
        SQLiteDatabase db         = this.getWritableDatabase();
        Favourites     favourites = null;
        Cursor         cursor     = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            int     id      = Integer.parseInt(cursor.getString(0));
            String  address = cursor.getString(1);
            String  date    = cursor.getString(2);
            double  lat     = cursor.getDouble(3);
            double  lng     = cursor.getDouble(4);
            boolean visited = cursor.getInt(5) > 0;
            favourites = new Favourites(id, address, date, lat, lng, visited);
        }
        cursor.close();
        return favourites;
    }

    public
    void deleteFavourite(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAVOURITE, COLUMN_ID + "	= ?", new String[]{String.valueOf(id)});
    }


}
