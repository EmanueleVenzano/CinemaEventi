package nf.application.emanuele.tesi1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.VibrationEffect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class CustomAdapter extends ArrayAdapter<Copertina> {
    public CustomAdapter (Context context, int textViewResoutceId, List<Copertina> objects){
        super(context, textViewResoutceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder viewHolder = null;
        if (convertView==null){
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.items_listview, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView)convertView.findViewById(R.id.titleLocandine);
            viewHolder.genere=(TextView)convertView.findViewById(R.id.genereLocandine);
            viewHolder.img = (ImageView)convertView.findViewById(R.id.imgLocandine);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Copertina copertina =getItem(position);
        viewHolder.name.setText("Titolo: "+copertina.name);
        viewHolder.genere.setText("Genere: "+copertina.genere);

        new ImageDownloaderTask((ImageView)convertView.findViewById(R.id.imgLocandine)).execute(copertina.img.toString());

        /*Bitmap bmp = null;
        try{
            URLConnection c = copertina.img.openConnection();
            InputStream is = c.getInputStream();
            bmp = BitmapFactory.decodeStream(is);
        }catch (Exception error){
            Log.e("!!!!!!!!!!!", error.getMessage());
            error.printStackTrace();
        }
        viewHolder.img.setImageBitmap(bmp);*/
        return convertView;
    }

    private class ViewHolder {
        public TextView name;
        public TextView genere;
        public ImageView img;

        public ViewHolder(){
            name=null;
            genere=null;
            img=null;
        }
    }
    private class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap>{
        private final WeakReference<ImageView> imageViewWeakReference;

        public ImageDownloaderTask (ImageView imageView){
            imageViewWeakReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground (String... params){
            return downloadBitmap(params[0]);
        }

        @Override
        protected void onPostExecute (Bitmap result){
            if (isCancelled()){
                result=null;
            }
            if (imageViewWeakReference!=null){
                ImageView imageView = imageViewWeakReference.get();
                if (imageView!=null){
                    if (result!=null) {
                        imageView.setImageBitmap(result);
                    }else{
                        Drawable placeholder = imageView.getContext().getResources().getDrawable(R.drawable.ic_launcher_background);
                        imageView.setImageDrawable(placeholder);
                    }
//                    WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
  //                  Display display = wm.getDefaultDisplay();
    //                Point size = new Point();
      //              display.getSize(size);
        //            int height = size.y/3;
          //          height=1;
            //        imageView.on;
              //      height=4400404;
                }
            }
        }

        private Bitmap downloadBitmap(String url){
            HttpURLConnection urlConnection = null;
            try{
                URL uri = new URL(url);
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
                Log.w("Image Downloader", "Error downloding image from "+url);
            }finally {
                if (urlConnection!=null){
                    urlConnection.disconnect();
                }
            }
            return null;
        }
    }
}
