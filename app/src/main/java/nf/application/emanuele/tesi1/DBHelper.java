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
        db.execSQL("INSERT INTO cinema VALUES ('Cinema Italia', 'italia', 'NON Multisala STRABILIANTEE', 44.402998, 8.684109)");
        db.execSQL("INSERT INTO cinema VALUES ('TheSpace', 'thespace', 'Multisala in centro', 44.408186, 8.921580)");
        db.execSQL("INSERT INTO cinema VALUES ('Fiumara', 'fiumara', 'Multisala STRABILIANTEE', 44.413469, 8.880971)");
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion){
        Log.d("Cinema", "Upgrading DB version from "+oldVersion+" to "+newVersion);
        db.execSQL(CinemaDB.DROP_CINEMA_TABLE);
        onCreate(db);
    }
}
