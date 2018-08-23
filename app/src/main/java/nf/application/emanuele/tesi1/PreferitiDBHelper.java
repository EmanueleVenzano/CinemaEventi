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
        //-------------------------------------------------------------
        db.execSQL(PreferitiDB.CREATE_EVENTI_TABLE);
        db.execSQL("INSERT INTO eventi (eventiTitolo, eventiData, eventiLuogo) VALUES ('Renzo Arbore', '16/10/18 21:00', 'Teatro Carlo Felice')");
        db.execSQL("INSERT INTO eventi (eventiTitolo, eventiData, eventiLuogo) VALUES ('Glen Miller Orchestra', '16/10/18 21:00', 'Teatro Politeama Genovese')");
        db.execSQL("INSERT INTO eventi (eventiTitolo, eventiData, eventiLuogo) VALUES ('Ben Ottewell', '17/10/18 21:00', 'Teatro Bloser')");
        db.execSQL("INSERT INTO eventi (eventiTitolo, eventiData, eventiLuogo) VALUES ('Ghali', '26/10/18 21:00', 'RDS Stadium')");
        db.execSQL("INSERT INTO eventi (eventiTitolo, eventiData, eventiLuogo) VALUES ('Luca Carboni', '16/10/18 21:00', 'Teatro Politeama Genovese')");
        db.execSQL("INSERT INTO eventi (eventiTitolo, eventiData, eventiLuogo) VALUES ('Tommy Emmanuel feat Jerry Douglas', '04/11/18 21:00', 'Teatro Politeama Genovese')");
        db.execSQL("INSERT INTO eventi (eventiTitolo, eventiData, eventiLuogo) VALUES ('Brit Floyd-Eclipse World Tour', '05/11/18 21:00', 'Teatro Politeama Genovese')");
        db.execSQL("INSERT INTO eventi (eventiTitolo, eventiData, eventiLuogo) VALUES ('Malika Ayane', '06/11/18 21:00', 'Teatro Politeama Genovese')");
        db.execSQL("INSERT INTO eventi (eventiTitolo, eventiData, eventiLuogo) VALUES ('Malika Ayane', '07/11/18 21:30', 'Teatro La Claque in Agorà')");
        db.execSQL("INSERT INTO eventi (eventiTitolo, eventiData, eventiLuogo) VALUES ('Thegiornalisti', '08/11/18 21:00', 'RDS Stadium')");
        db.execSQL("INSERT INTO eventi (eventiTitolo, eventiData, eventiLuogo) VALUES ('Luca Barbarossa', '10/11/18 21:30', 'Teatro della Tosse')");
        db.execSQL("INSERT INTO eventi (eventiTitolo, eventiData, eventiLuogo) VALUES ('Edoardo Bennato', '15/11/18 21:00', 'Teatro Carlo Felice')");
        db.execSQL("INSERT INTO eventi (eventiTitolo, eventiData, eventiLuogo) VALUES ('Massimo Ranieri Sogno e Son Desto 400 volte', '11/12/18 21:00', 'Teatro Carlo Felice')");
        db.execSQL("INSERT INTO eventi (eventiTitolo, eventiData, eventiLuogo) VALUES ('Ara Malikian', '18/12/18 21:00', 'Teatro Politeama Genovese')");
        db.execSQL("INSERT INTO eventi (eventiTitolo, eventiData, eventiLuogo) VALUES ('Dodi Battaglia - Perle Il Tour', '14/02/19 21:00', 'Teatro Politeama Genovese')");
        db.execSQL("INSERT INTO eventi (eventiTitolo, eventiData, eventiLuogo) VALUES ('Claudio Baglioni', '09/04/19 21:00', 'RDS Stadium')");
        db.execSQL("INSERT INTO eventi (eventiTitolo, eventiData, eventiLuogo) VALUES ('Canto Libero', '11/04/19 21:00', 'Teatro Politeama Genovese')");
        db.execSQL("INSERT INTO eventi (eventiTitolo, eventiData, eventiLuogo) VALUES ('Big One - The European Pink Floyd Show', '04/05/19 21:00', 'Teatro della Corte')");
        //--------------------------------------------------------
        return;
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion){
        Log.d("Preferiti", "Upgrading DB version from "+oldVersion+" to "+newVersion);
        db.execSQL(PreferitiDB.DROP_PREFERITI_TABLE);
        db.execSQL(PreferitiDB.DROP_EVENTI_TABLE);
        onCreate(db);
    }

}


