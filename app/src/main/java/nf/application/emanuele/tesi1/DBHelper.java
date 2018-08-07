package nf.application.emanuele.tesi1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper (Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CinemaDB.CREATE_CINEMA_TABLE);
        db.execSQL("INSERT INTO cinema VALUES ('Fiumara', 'fiumara', 'Multisala STRABILIANTEE', 44.24500, 8.2463)");
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion){
        Log.d("Cinema", "Upgrading DB version from "+oldVersion+" to "+newVersion);
        db.execSQL(CinemaDB.DROP_CINEMA_TABLE);
        onCreate(db);
    }
}
