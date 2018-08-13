package nf.application.emanuele.tesi1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PreferitiCustomAdapter extends ArrayAdapter<ArrayList<String>> {
    private Context context;

    public PreferitiCustomAdapter (Context context, int textViewResoutceId, List<ArrayList<String>> objects){
        super(context, textViewResoutceId, objects);
        this.context=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder viewHolder = null;
        if (convertView==null){
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.preferiti_items_listview, null);
            viewHolder = new ViewHolder();
            viewHolder.nome = (TextView)convertView.findViewById(R.id.item_title_film);
            viewHolder.luogo=(TextView)convertView.findViewById(R.id.item_luogo_film);
            viewHolder.orario = (TextView)convertView.findViewById(R.id.item_orario_film);
            viewHolder.img=(ImageView)convertView.findViewById(R.id.item_star_film);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final ArrayList<String> copertina =getItem(position);
        viewHolder.nome.setText(copertina.get(2));
        viewHolder.luogo.setText(copertina.get(1));
        viewHolder.orario.setText(copertina.get(0));
        viewHolder.img.setImageResource(R.drawable.delete);
        viewHolder.img.setTag(R.drawable.delete);
        viewHolder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView view = (ImageView) v;
                PreferitiDB db;
                switch ((Integer) view.getTag()){
                    case R.drawable.delete:
                        db = new PreferitiDB(context);
                        int info = db.deletePreferito(copertina.get(2),copertina.get(1), copertina.get(0));
                        view.setImageResource(R.drawable.plus);
                        view.setTag(R.drawable.plus);
                        break;
                    case R.drawable.plus:
                        db = new PreferitiDB(context);
                        long result = db.insertPreferito(copertina.get(2), "1", copertina.get(0), copertina.get(1));
                        view.setImageResource(R.drawable.delete);
                        view.setTag(R.drawable.delete);
                        break;
                }
            }
        });
        return convertView;
    }

    private class ViewHolder {
        public TextView nome;
        public TextView luogo;
        public TextView orario;
        public ImageView img;

        public ViewHolder(){
            nome=null;
            luogo=null;
            orario=null;
            img=null;
        }
    }
}
