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

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DirectionAdapter extends ArrayAdapter<ArrayList<String>> {
    private GoogleMap mMap;
    private Bitmap tempBitmap;
    public DirectionAdapter (Context context, int textViewResoutceId, ArrayList<ArrayList<String>> objects, GoogleMap mMap){
        super(context, textViewResoutceId, objects);
        this.mMap = mMap;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder viewHolder = null;
        if (convertView==null){
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            try {
                convertView = inflater.inflate(R.layout.direction_item, parent, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            convertView.setTag(R.id.directionItemIcon, convertView.findViewById(R.id.directionItemIcon));
            viewHolder = new ViewHolder();
            viewHolder.line = (TextView) convertView.findViewById(R.id.directionItemLine);
            viewHolder.start =(TextView) convertView.findViewById(R.id.directionItemStart);
            viewHolder.startTime = (TextView) convertView.findViewById(R.id.directionItemStartTime);
            viewHolder.distance = (TextView) convertView.findViewById(R.id.directionItemDistance);
            viewHolder.numStops = (TextView) convertView.findViewById(R.id.directionItemNumStops);
            viewHolder.end = (TextView) convertView.findViewById(R.id.directionItemEnd);
            viewHolder.endTime = (TextView) convertView.findViewById(R.id.directionItemEndTime);
            viewHolder.icon = (ImageView) convertView.getTag(R.id.directionItemIcon);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ArrayList<String> copertina = getItem(position);
        LatLng start = new LatLng(Double.parseDouble(copertina.get(0)), Double.parseDouble(copertina.get(1)));
        LatLng end = new LatLng(Double.parseDouble(copertina.get(2)), Double.parseDouble(copertina.get(3)));
        int numMarkerStart = position+1;
        String urlMarkerStart = "http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld="+toAlphabetic(numMarkerStart)+"|FE6256|000000";
        new BitmapDownloaderTask().execute(urlMarkerStart);
        MarkerOptions startMarker = new MarkerOptions()
                .position(start)
                .icon(BitmapDescriptorFactory.fromBitmap(tempBitmap));
        mMap.addMarker(startMarker);
        int numMarkerEnd = position+2;
        String urlMarkerEnd = "http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld="+toAlphabetic(numMarkerEnd)+"|FE6256|000000";
        new BitmapDownloaderTask().execute(urlMarkerEnd);
        MarkerOptions endMarker = new MarkerOptions()
                .position(end)
                .icon(BitmapDescriptorFactory.fromBitmap(tempBitmap));
        mMap.addMarker(endMarker);
        viewHolder.start.setText(toAlphabetic(numMarkerStart));
        viewHolder.end.setText(toAlphabetic(numMarkerEnd));
        viewHolder.distance.setText(copertina.get(4));
        if (!copertina.get(5).equals("WALKING")){
            viewHolder.line.setText(copertina.get(6));
            viewHolder.numStops.setText(copertina.get(7));
            viewHolder.startTime.setText(copertina.get(8));
            viewHolder.endTime.setText(copertina.get(9));
            if (copertina.get(10).equals("null")) {
                Drawable placeholder = viewHolder.icon.getContext().getResources().getDrawable(R.drawable.bho1);
                viewHolder.icon.setImageDrawable(placeholder);
            }else{
                new DirectionAdapter.ImageDownloaderTask(viewHolder.icon).execute(copertina.get(10));
            }
        }else{
            viewHolder.numStops.setText("");
            viewHolder.line.setText("");
            viewHolder.endTime.setText("");
            viewHolder.startTime.setText("");
            viewHolder.icon.setImageResource(R.drawable.ic_walk);
        }
        return convertView;
    }

    private class ViewHolder {
        public ImageView icon;
        public TextView line;
        public TextView start;
        public TextView startTime;
        public TextView distance;
        public TextView numStops;
        public TextView end;
        public TextView endTime;

        public ViewHolder() {
            this.icon = null;
            this.line = null;
            this.start = null;
            this.startTime = null;
            this.distance = null;
            this.numStops = null;
            this.end = null;
            this.endTime = null;
        }
    }

    private class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            return downloadBitmap(params[0]);
        }

        @Override
        protected void onPostExecute (Bitmap result){
            tempBitmap = result;
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

    public static String toAlphabetic(int i) {
        if( i<0 ) {
            return "-"+toAlphabetic(-i-1);
        }

        int quot = i/26;
        int rem = i%26;
        char letter = (char)((int)'A' + rem);
        if( quot == 0 ) {
            return ""+letter;
        } else {
            return toAlphabetic(quot-1) + letter;
        }
    }
}
