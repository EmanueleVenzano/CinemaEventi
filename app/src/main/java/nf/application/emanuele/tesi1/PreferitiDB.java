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

    public static final String CREATE_PREFERITI_TABLE = "CREATE TABLE " + PREFERITI_TABLE + "(" +
            PREFERITI_ID + "INT AUTO_INCREMENT PRIMARY KEY, "+
            PREFERITI_TITOLO + " TEXT, " +
            PREFERITI_ISFILM + " TEXT, " +
            PREFERITI_ORARIO + " TEXT, " +
            PREFERITI_LUOGO + " TEXT);";
    public static final String DROP_PREFERITI_TABLE = "DROP TABLE IF EXISTS "+ PREFERITI_TABLE;

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
//        String sqlStatement = "INSERT INTO "+PREFERITI_TABLE+" ("+PREFERITI_TITOLO+", "+PREFERITI_ISFILM+", "+PREFERITI_ORARIO+", "+PREFERITI_LUOGO+") " + "VALUES ("+titolo+", "+isfilm+", "+orario+", "+luogo+");";
  //      try{
    //        db.execSQL(sqlStatement);
      //  }catch (Exception e){
        //    e.printStackTrace();
        //}
        long id = db.insert(PREFERITI_TABLE, null, contentValues);
        this.closeDB();
        return id;
    }

    public int deletePreferito(String titolo){
        this.openReadableDB();
        this.openWriteableDB();
        String where = PREFERITI_TITOLO + " = ? ";
        String[] whereArgs = {titolo};
        int id = db.delete(PREFERITI_TABLE, where, whereArgs);
        this.closeDB();
        return id;
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

    public class filmPreferiti{
        public String nome;
        public String orario;
        public String luogo;

        public filmPreferiti(){
            nome="";
            orario="";
            luogo="";
        }
    }

    public ArrayList<filmPreferiti> getAllFavourites (){
        this.openReadableDB();
        Cursor cursor = db.query(PREFERITI_TABLE, null, null, null, null, null, null);
        filmPreferiti temp = new filmPreferiti();
        ArrayList<filmPreferiti> result = getAllFavourites(cursor, temp);
        if (cursor!=null){
            cursor.close();
        }
        this.closeDB();
        return result;
    }

    private static ArrayList<filmPreferiti> getAllFavourites (Cursor cursor, filmPreferiti temp) {
        ArrayList<filmPreferiti> result = new ArrayList<>();
        if (cursor == null || cursor.getCount() == 0) return result;
        try {
            while (cursor.moveToNext()){
                String orario = cursor.getString(PREFERITI_ORARIO_COL);
                temp.orario=orario;
                String cinema = cursor.getString(PREFERITI_LUOGO_COL);
                temp.luogo=cinema;
                String titolo = cursor.getString(PREFERITI_TITOLO_COL);
                temp.nome=titolo;
                result.add(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
/*

    private static Location getLocationCinemFromCursor (String name, Cursor cursor){
        if (cursor == null || cursor.getCount()==0) return null;
        try{
            cursor.moveToFirst();
            double lat = Double.parseDouble(cursor.getString(CINEMA_LATITUDE_COL));
            double lon = cursor.getDouble(CINEMA_LONGITUDE_COL);
            Location l = new Location(name);
            l.setLongitude(lon);
            l.setLatitude(lat);
            return l;
        }catch (Exception e){
            return null;
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
            cursor.moveToFirst();
            ArrayList<String> result = new ArrayList<>();
            result.add(cursor.getString(CINEMA_NAME_COL));
            result.add(cursor.getString(CINEMA_IMG_COL));
            result.add(cursor.getString(CINEMA_DESCRIZIONE_COL));
            return result;
        }catch (Exception e){
            return null;
        }
    }
*/
}
