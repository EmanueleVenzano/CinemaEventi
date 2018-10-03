package nf.application.emanuele.tesi1;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CustomAdapter extends ArrayAdapter<String> {
    Context context;
    ArrayList<Bitmap> immagini;
    ArrayList<String> urls;
    public CustomAdapter (Context context, int textViewResoutceId, List<String> titoli, ArrayList<String> urls, ArrayList<Bitmap>immagini){
        super(context, textViewResoutceId, titoli);
        this.context = context;
        this.immagini = immagini;
        this.urls = urls;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder viewHolder = null;
        if (convertView==null){
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            try {
                convertView = inflater.inflate(R.layout.items_listview, parent, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            convertView.setTag(R.id.imgLocandine, convertView.findViewById(R.id.imgLocandine));
            viewHolder = new ViewHolder();
            viewHolder.img = (ImageView) convertView.getTag(R.id.imgLocandine);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (immagini.get(position) == null){
            String copertina =getItem(position);
            Bitmap bitmap = null;
            if (urls.get(position) != null && !urls.get(position).equals("null")){
                try {
                    bitmap = new ImageDownloaderTask().execute(urls.get(position)).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }else{
                Bitmap src = BitmapFactory.decodeResource(context.getResources(), R.drawable.bho1);
                Bitmap dest = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
                String yourText = String.valueOf(copertina);
                Canvas cs = new Canvas(dest);
                Paint tPaint = new Paint();
                tPaint.setTextSize(100);
                tPaint.setColor(Color.BLACK);
                tPaint.setStyle(Paint.Style.FILL);
                cs.drawBitmap(src, 5f, 5f, null);
                float height = tPaint.measureText("yY");
                float width = tPaint.measureText(yourText);
                float x_coord = (src.getWidth() - width)/2;
                float y_coord = (src.getHeight()-height-100f);
                cs.drawText(yourText, x_coord, y_coord, tPaint);
                bitmap = new BitmapDrawable(context.getResources(), dest).getBitmap();
            }
            immagini.set(position, bitmap);
        }
        viewHolder.img.setImageBitmap(immagini.get(position));
        return convertView;
    }

    private class ViewHolder {
        public TextView name;
        public ImageView img;

        public ViewHolder(){
            name=null;
            img=null;
        }
    }

    private class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap>{
        @Override
        protected Bitmap doInBackground (String... params){
            HttpURLConnection urlConnection = null;
            try{
                URL uri = new URL(params[0]);
                urlConnection = (HttpURLConnection) uri.openConnection();
                int statusCode = urlConnection.getResponseCode();
                if (statusCode!=200){
                    return null;
                }
                InputStream inputStream = urlConnection.getInputStream();

                if (inputStream!=null){
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds=true;
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;
                }
            }catch (Exception e){
                urlConnection.disconnect();
                Log.w("Image Downloader", "Error downloding image from "+params[0]);
            }finally {
                if (urlConnection!=null){
                    urlConnection.disconnect();
                }
            }
            return null;
        }
    }
}