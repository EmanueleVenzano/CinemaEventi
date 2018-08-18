package nf.application.emanuele.tesi1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

import java.util.ArrayList;

public class PreferitiDB {
    private SQLiteDatabase db;
    private PreferitiDBHelper dbHelper;

    public static final String DB_NAME = "Preferiti.db";
    public static final int DB_VERSION = 1;
    public static final String PREFERITI_TABLE = "preferiti";
    public static final String PREFERITI_ID = "id";
    public static final int PREFERITI_ID_COL = 0;
    public static final String PREFERITI_TITOLO = "titolo";
    public static final int PREFERITI_TITOLO_COL = 1;
    public static final String PREFERITI_ORARIO = "orario";
    public static final int PREFERITI_ORARIO_COL = 3;
    public static final String PREFERITI_LUOGO = "luogo";
    public static final int PREFERITI_LUOGO_COL = 4;
    public static final String PREFERITI_ISFILM = "isfilm";
    public static final int PREFERITI_ISFILM_COL = 2;
//NEW PART-----------
    public static final String EVENTI_TABLE = "eventi";
    public static final String EVENTI_ID = "eventiId";
    public static final int EVENTI_ID_COL = 0;
    public static final String EVENTI_TITOLO = "eventiTitolo";
    public static final int EVENTI_TITOLO_COL = 1;
    public static final String EVENTI_DATA = "eventiData";
    public static final int EVENTI_DATA_COL = 2;
    public static final String EVENTI_LUOGO = "eventiLuogo";
    public static final int EVENTI_LUOGO_COL = 3;

    //-------------------
    public static final String CREATE_PREFERITI_TABLE = "CREATE TABLE " + PREFERITI_TABLE + "(" +
            PREFERITI_ID + "INT AUTO_INCREMENT PRIMARY KEY, "+
            PREFERITI_TITOLO + " TEXT, " +
            PREFERITI_ISFILM + " TEXT, " +
            PREFERITI_ORARIO + " TEXT, " +
            PREFERITI_LUOGO + " TEXT);";
    public static final String DROP_PREFERITI_TABLE = "DROP TABLE IF EXISTS "+ PREFERITI_TABLE;

    //---------------------------------
    public static final String CREATE_EVENTI_TABLE = "CREATE TABLE " + EVENTI_TABLE + "(" +
            EVENTI_ID + "INT AUTO_INCREMENT PRIMARY KEY, "+
            EVENTI_TITOLO + " TEXT, " +
            EVENTI_DATA + " TEXT, " +
            EVENTI_LUOGO + " TEXT);";
    public static final String DROP_EVENTI_TABLE = "DROP TABLE IF EXISTS "+ EVENTI_TABLE;
//-------------------------------
    public PreferitiDB(Context context){
        dbHelper = new PreferitiDBHelper(context, DB_NAME, null, DB_VERSION);
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

    public long insertPreferito (String titolo, String isfilm, String orario, String luogo){
        this.openReadableDB();
        this.openWriteableDB();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PREFERITI_TITOLO, titolo);
        contentValues.put(PREFERITI_ISFILM, isfilm);
        contentValues.put(PREFERITI_ORARIO, orario);
        contentValues.put(PREFERITI_LUOGO, luogo);
        long id = db.insert(PREFERITI_TABLE, null, contentValues);
        this.closeDB();
        return id;
    }

    public long deletePreferito(String titolo, String luogo, String orario){
        this.openReadableDB();
        this.openWriteableDB();
        String where =
                PREFERITI_TITOLO + " = ? AND "+
                        PREFERITI_LUOGO + " = ? AND "+
                        PREFERITI_ORARIO + " = ?";
        String[] whereArgs = {titolo, luogo, orario};
        Cursor cursor = db.query(PREFERITI_TABLE, null, where, whereArgs, null, null, null);
        long result = 0;
        if (cursor == null || cursor.getCount() == 0)try{
            cursor.moveToFirst();
            result =cursor.getInt(PREFERITI_ID_COL);
        }catch (Exception e){
            e.printStackTrace();
        }
        if (cursor!=null){
            cursor.close();
        }
        int id = db.delete(PREFERITI_TABLE, where, whereArgs);
        this.closeDB();
        return result;
    }

    public ArrayList<ArrayList<String>> getPreferito (String name){
        String where = PREFERITI_TITOLO + " = ? ";
        String[] whereArgs = {name};
        this.openReadableDB();
        Cursor cursor = db.query(PREFERITI_TABLE, null, where, whereArgs, null, null, null);
        ArrayList<ArrayList<String>> result = getInfoPreferito(cursor);
        if (cursor!=null){
            cursor.close();
        }
        this.closeDB();
        return result;
    }

    private static ArrayList<ArrayList<String>> getInfoPreferito (Cursor cursor) {
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        if (cursor == null || cursor.getCount() == 0) return result;
        try {
            while (cursor.moveToNext()){
                ArrayList<String> temp = new ArrayList<>();
                String orario = cursor.getString(PREFERITI_ORARIO_COL);
                temp.add(orario);
                String cinema = cursor.getString(PREFERITI_LUOGO_COL);
                temp.add(cinema);
                result.add(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;

    }

    public ArrayList<ArrayList<String>> getAllFavourites (String isFilm){
        this.openReadableDB();
        String where = PREFERITI_ISFILM + " = ? ";
        String[] whereArgs = {isFilm};
        Cursor cursor = db.query(PREFERITI_TABLE, null, where, whereArgs, null, null, null);
        ArrayList<ArrayList<String>> result = getAllFavourites(cursor);
        if (cursor!=null){
            cursor.close();
        }
        this.closeDB();
        return result;
    }

    private static ArrayList<ArrayList<String>> getAllFavourites (Cursor cursor) {
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        if (cursor == null || cursor.getCount() == 0) return result;
        try {
            while (cursor.moveToNext()){
                ArrayList<String> temp = new ArrayList<>();
                String orario = cursor.getString(PREFERITI_ORARIO_COL);
                temp.add(orario);
                String cinema = cursor.getString(PREFERITI_LUOGO_COL);
                temp.add(cinema);
                String titolo = cursor.getString(PREFERITI_TITOLO_COL);
                temp.add(titolo);
                result.add(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    //--------------------------------------------------------------------------
    public ArrayList<ArrayList<String>> getAllEvents (){
        this.openReadableDB();
        Cursor cursor = db.query(EVENTI_TABLE, null, null, null, null, null, null);
        ArrayList<ArrayList<String>> result = getAllEvents(cursor);
        if (cursor!=null){
            cursor.close();
        }
        this.closeDB();
        return result;
    }

    private static ArrayList<ArrayList<String>> getAllEvents (Cursor cursor) {
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        if (cursor == null || cursor.getCount() == 0) return result;
        try {
            while (cursor.moveToNext()){
                ArrayList<String> temp = new ArrayList<>();
                String data = cursor.getString(EVENTI_DATA_COL);
                temp.add(data);
                String luogo = cursor.getString(EVENTI_LUOGO_COL);
                temp.add(luogo);
                String titolo = cursor.getString(EVENTI_TITOLO_COL);
                temp.add(titolo);
                result.add(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    //------------------------------------------------------------------------------

}