package nf.application.emanuele.tesi1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PreferitiDBHelper extends SQLiteOpenHelper {
    public PreferitiDBHelper (Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(PreferitiDB.CREATE_PREFERITI_TABLE);
        return;
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion){
        Log.d("Preferiti", "Upgrading DB version from "+oldVersion+" to "+newVersion);
        db.execSQL(PreferitiDB.DROP_PREFERITI_TABLE);
        onCreate(db);
    }
}
