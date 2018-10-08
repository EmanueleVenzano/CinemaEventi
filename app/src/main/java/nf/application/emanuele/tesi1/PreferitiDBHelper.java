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
        db.execSQL("INSERT INTO eventi (eventiTitolo, eventiData, eventiLuogo, eventiDescrizione) VALUES ('Renzo Arbore', '16/10/18 21:00', 'Teatro Carlo Felice' , 'Lo showman italiano gira ininterrottamente con la sua Orchestra con innumerevoli concerti acclamatissimi ovunque in un clima da record. Lo spirito travolgente e contagioso di questo artista sta per infiammare Genova.')");
        db.execSQL("INSERT INTO eventi (eventiTitolo, eventiData, eventiLuogo, eventiDescrizione) VALUES ('Glen Miller Orchestra', '16/10/18 21:00', 'Teatro Politeama Genovese', 'La Glenn Miller Orchestra è l''ensemble jazz e swing più famoso e più seguito al mondo, tributo ad un''epoca, quella della Swing Era, e ai protagonisti di quella musica che ha fatto innamorare il mondo.')");
        db.execSQL("INSERT INTO eventi (eventiTitolo, eventiData, eventiLuogo, eventiDescrizione) VALUES ('Ben Ottewell', '17/10/18 21:00', 'Teatro Bloser', 'Il famoso cantautore britannico ed il suo Indie Rock finalmente live a Genova.')");
        db.execSQL("INSERT INTO eventi (eventiTitolo, eventiData, eventiLuogo, eventiDescrizione) VALUES ('Ghali', '26/10/18 21:00', 'RDS Stadium', 'Ghali arriva con l''attesissimo tour nelle più importanti arene indoor. La rivelazione del rap italiano torna nuovamente a Genova dopo il successo del suo ultimo concerto.')");
        db.execSQL("INSERT INTO eventi (eventiTitolo, eventiData, eventiLuogo, eventiDescrizione) VALUES ('Luca Carboni', '31/10/18 21:00', 'Teatro Politeama Genovese', 'Luca Carboni ora in tour con il suo nuovo album Pop Up riproponendo in chiave elettronica il suo repertorio.')");
        db.execSQL("INSERT INTO eventi (eventiTitolo, eventiData, eventiLuogo, eventiDescrizione) VALUES ('Tommy Emmanuel feat Jerry Douglas', '04/11/18 21:00', 'Teatro Politeama Genovese', 'Il grande chitarrista australiano torna live con l''Accomplice Tour!')");
        db.execSQL("INSERT INTO eventi (eventiTitolo, eventiData, eventiLuogo, eventiDescrizione) VALUES ('Brit Floyd-Eclipse World Tour', '05/11/18 21:00', 'Teatro Politeama Genovese', 'Brit Floyd, il più grande spettacolo live che porta sul palco le canzoni dei Pink Floyd, torna in Italia per celebrare i 45 anni dall’iconico album della band, The Dark Side of the Moon.')");
        db.execSQL("INSERT INTO eventi (eventiTitolo, eventiData, eventiLuogo, eventiDescrizione) VALUES ('Malika Ayane', '06/11/18 21:00', 'Teatro Politeama Genovese', 'Malika Ayane torna sulla scena musicale dopo due anni di lavoro con il suo Domino tour con ben due date a Genova !')");
        db.execSQL("INSERT INTO eventi (eventiTitolo, eventiData, eventiLuogo, eventiDescrizione) VALUES ('Malika Ayane', '07/11/18 21:30', 'Teatro La Claque in Agorà', 'Seconda data di Malika Ayane a Genova, questa volta presso il Teatro La Claque.')");
        db.execSQL("INSERT INTO eventi (eventiTitolo, eventiData, eventiLuogo, eventiDescrizione) VALUES ('Thegiornalisti', '08/11/18 21:00', 'RDS Stadium', 'Finalmente di nuovo in tour per il quinto album del gruppo i thegiornalisti fanno tappa all''RDS Stadium.')");
        db.execSQL("INSERT INTO eventi (eventiTitolo, eventiData, eventiLuogo, eventiDescrizione) VALUES ('Luca Barbarossa', '10/11/18 21:30', 'Teatro della Tosse', 'Luca Barbarossa torna con un nuovo tour teatrale!')");
        db.execSQL("INSERT INTO eventi (eventiTitolo, eventiData, eventiLuogo, eventiDescrizione) VALUES ('Edoardo Bennato', '15/11/18 21:00', 'Teatro Carlo Felice', 'La creatività, quell’arte, pregio di pochi, che appartiene da sempe ad Edoardo Bennato che torna a teatro dopo lo strepitoso tour sold out dello scorso autunno, sul palco il “PINOCCHIO & COMPANY TOUR 2018”.')");
        db.execSQL("INSERT INTO eventi (eventiTitolo, eventiData, eventiLuogo, eventiDescrizione) VALUES ('Massimo Ranieri Sogno e Son Desto 400 volte', '11/12/18 21:00', 'Teatro Carlo Felice', 'Tanta energia, gran talento e divertimento adatto a tutti al Teatro Carlo Felice.')");
        db.execSQL("INSERT INTO eventi (eventiTitolo, eventiData, eventiLuogo, eventiDescrizione) VALUES ('Ara Malikian', '18/12/18 21:00', 'Teatro Politeama Genovese', 'Il famoso violinista armeno/libanese torna in Italia. Un musicista crossover che pensa alla musica senza barriere di generi o stili, uno spettacolo da non perdere!')");
        db.execSQL("INSERT INTO eventi (eventiTitolo, eventiData, eventiLuogo, eventiDescrizione) VALUES ('Dodi Battaglia - Perle Il Tour', '14/02/19 21:00', 'Teatro Politeama Genovese', 'Il nuovo progetto musicale di Dodi Battaglia, accompagnato per l’occasione da quattro musicisti e da un corista per festeggiare i suoi 50 anni di carriera totalmente dedicati alla storica band italiana I Pooh')");
        db.execSQL("INSERT INTO eventi (eventiTitolo, eventiData, eventiLuogo, eventiDescrizione) VALUES ('Claudio Baglioni', '09/04/19 21:00', 'RDS Stadium', 'Il tour AL CENTRO che vedrà Claudio Baglioni protagonista nelle principali arene indoor d’Italia, dove grazie al palco al centro il pubblico sarà disposto a 360 gradi.')");
        db.execSQL("INSERT INTO eventi (eventiTitolo, eventiData, eventiLuogo, eventiDescrizione) VALUES ('Canto Libero', '11/04/19 21:00', 'Teatro Politeama Genovese', 'Dopo il successo della passata stagione, torna Canto Libero, non un semplice concerto ma un grande spettacolo che omaggia il periodo d’oro della storica accoppiata Mogol – Battisti.')");
        db.execSQL("INSERT INTO eventi (eventiTitolo, eventiData, eventiLuogo, eventiDescrizione) VALUES ('Big One - The European Pink Floyd Show', '04/05/19 21:00', 'Teatro della Corte', 'Questo anno i Big One, la Miglior Tribute Band in Europa per la musica dei Pink Floyd, saranno in tour con il The Division Bell Tour più il Greatest Hits della celebre band inglese.')");
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


