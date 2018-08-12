package nf.application.emanuele.tesi1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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
import java.util.List;

public class PreferitiCustomAdapter extends ArrayAdapter<PreferitiDB.filmPreferiti> {
    public PreferitiCustomAdapter (Context context, int textViewResoutceId, List<PreferitiDB.filmPreferiti> objects){
        super(context, textViewResoutceId, objects);
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
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        PreferitiDB.filmPreferiti copertina =getItem(position);
        viewHolder.nome.setText(copertina.nome);
        viewHolder.luogo.setText(copertina.luogo);
        viewHolder.orario.setText(copertina.orario);
        return convertView;
    }

    private class ViewHolder {
        public TextView nome;
        public TextView luogo;
        public TextView orario;

        public ViewHolder(){
            nome=null;
            luogo=null;
            orario=null;
        }
    }
}
