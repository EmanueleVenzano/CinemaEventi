package nf.application.emanuele.tesi1;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class CinemaDB {
    private SQLiteDatabase db;
    private DBHelper dbHelper;

    public static final String DB_NAME = "Cinema.db";
    public static final int DB_VERSION = 1;
    public static final String CINEMA_TABLE = "cinema";
    public static final String CINEMA_NAME = "nome";
    public static final int CINEMA_NAME_COL = 0;
    public static final String CINEMA_IMG = "img";
    public static final int CINEMA_IMG_COL = 1;
    public static final String CINEMA_DESCRIZIONE = "descrizione";
    public static final int CINEMA_DESCRIZIONE_COL = 2;
    public static final String CINEMA_LATITUDE = "latitude";
    public static final int CINEMA_LATITUDE_COL = 3;
    public static final String CINEMA_LONGITUDE = "longitude";
    public static final int CINEMA_LONGITUDE_COL = 4;

    public static final String CREATE_CINEMA_TABLE = "CREATE TABLE " + CINEMA_TABLE + "(" +
            CINEMA_NAME + " TEXT PRIMARY KEY, " +
            CINEMA_IMG + " TEXT, " +
            CINEMA_DESCRIZIONE + " TEXT, " +
            CINEMA_LATITUDE + " TEXT, "+
            CINEMA_LONGITUDE + " TEXT);";
    public static final String DROP_CINEMA_TABLE = "DROP TABLE IF EXISTS "+ CINEMA_TABLE;

    public CinemaDB(Context context){
        dbHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
    }

    private void openReadableDB(){
        db = dbHelper.getReadableDatabase();
    }

    private void openWriteableDB(){
        db = dbHelper.getWritableDatabase();
    }

    private void closeDB(){
        if (db!=null){
            db.close();
        }
    }

    public ArrayList<String> getInfoCinema (String cinemaName){
        String where = CINEMA_NAME + "= ? ";
        String[] whereArgs = {cinemaName};
        this.openReadableDB();
        Cursor cursor = db.query(CINEMA_TABLE, null, where, whereArgs, null, null, null);
        ArrayList<String> result = new ArrayList<>();
        result = getInfoCinemFromCursor(cursor);
        if (cursor!=null){
            cursor.close();
        }
        this.closeDB();
        return result;
    }

    private static ArrayList<String> getInfoCinemFromCursor (Cursor cursor){
        if (cursor == null || cursor.getCount()==0) return null;
        try{
            ArrayList<String> result = new ArrayList<>();
            result.add(cursor.getString(CINEMA_NAME_COL));
            result.add(cursor.getString(CINEMA_IMG_COL));
            result.add(cursor.getString(CINEMA_DESCRIZIONE_COL));
            return result;
        }catch (Exception e){
            return null;
        }
    }

}
