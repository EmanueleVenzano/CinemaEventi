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
import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends ArrayAdapter<ArrayList<String>> {
    public CustomAdapter (Context context, int textViewResoutceId, List<ArrayList<String>> objects){
        super(context, textViewResoutceId, objects);
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
        ArrayList<String> copertina =getItem(position);
        if(copertina.get(1) == null) {
            Drawable placeholder = viewHolder.img.getContext().getResources().getDrawable(R.drawable.bho1);
            viewHolder.img.setImageDrawable(placeholder);
        }else {
            ImageDownloaderTask idt = new ImageDownloaderTask((ImageView)convertView.findViewById(R.id.imgLocandine));
            idt.execute(copertina.get(1));
        }
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
