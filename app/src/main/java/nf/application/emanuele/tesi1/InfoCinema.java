package nf.application.emanuele.tesi1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class InfoCinema extends Fragment implements View.OnClickListener {
    private String name;
    private ImageView imgCinema;
    private TextView nameCinema;
    private TextView descriptionCinema;
    private ImageButton buttonDrive;
    private ImageButton buttonTransit;
    private ImageButton buttonWalk;
    private ImageButton buttonBike;
    private TextView streetCinema;
    private TextView cityCinema;
    private String nomeFilm;
    private String tornaInfoFilm;

    public static InfoCinema newInstance(){
        InfoCinema Fragment = new InfoCinema();
        return Fragment;
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        try{
            View view = inflater.inflate(R.layout.activity_infocinema, container, false);
            name = ((cercaFilm)getActivity()).getMyData();
            tornaInfoFilm = ((cercaFilm)getActivity()).getTornaInfoFilm();
            nomeFilm = ((cercaFilm)getActivity()).getNomeFilm();
            MyApplication myApplication = (MyApplication) getActivity().getApplication();
            ArrayList<DataCinema> dataCinema = myApplication.getDataInfo().cinemas;
            int position;
            for (position=0; position<dataCinema.size(); position++){
                if (dataCinema.get(position).getName().equals(name)) break;
            }

            imgCinema = (ImageView) view.findViewById(R.id.img_cinema);
            nameCinema = (TextView) view.findViewById(R.id.nameCinema);
            descriptionCinema = (TextView) view.findViewById(R.id.descriptionCinema);
            buttonDrive = (ImageButton) view.findViewById(R.id.buttonDriveCinema);
            buttonDrive.setOnClickListener(this);
            buttonTransit = (ImageButton) view.findViewById(R.id.buttonTransitCinema);
            buttonTransit.setOnClickListener(this);
            buttonWalk = (ImageButton) view.findViewById(R.id.buttonFootCinema);
            buttonWalk.setOnClickListener(this);
            buttonBike = (ImageButton) view.findViewById(R.id.buttonBikeCinema);
            buttonBike.setOnClickListener(this);
            streetCinema = (TextView) view.findViewById(R.id.streetCinema);
            cityCinema = (TextView) view.findViewById(R.id.cityCinema);

            nameCinema.setText(dataCinema.get(position).getName());
            descriptionCinema.setText(Html.fromHtml("<b>Cap: <br></b>"+dataCinema.get(position).getCap()));
            descriptionCinema.setTextColor(Color.parseColor("#000000"));
            streetCinema.setText(Html.fromHtml("<b>Indirizzo: <br></b>"+dataCinema.get(position).getAddress()));
            streetCinema.setTextColor(Color.parseColor("#000000"));
            cityCinema.setText(Html.fromHtml("<b>Città: <br></b>"+dataCinema.get(position).getCity()));
            cityCinema.setTextColor(Color.parseColor("#000000"));

            //---------------------------------------------------------------------
            if(dataCinema.get(position).getUrl_img()==null || dataCinema.get(position).getUrl_img().equals("")) {
                Drawable placeholder = imgCinema.getContext().getResources().getDrawable(R.drawable.noimg);
                imgCinema.setImageDrawable(placeholder);
            }else {
                ImageDownloaderTask idt = new ImageDownloaderTask((ImageView)view.findViewById(R.id.img_cinema));
                idt.execute(dataCinema.get(position).getUrl_img());
            }
            //---------------------------------------------------------------------

            return view;

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onClick(View v){
        Intent intent = new Intent(getContext(), DirectionsInfo.class);
        switch (v.getId()){
            case R.id.buttonDriveCinema: intent.putExtra("mode", "driving"); break;
            case R.id.buttonTransitCinema: intent.putExtra("mode", "transit"); break;
            case R.id.buttonFootCinema: intent.putExtra("mode", "walking"); break;
            case R.id.buttonBikeCinema: intent.putExtra("mode", "bicycling"); break;
        }
        intent.putExtra("name", name);
        intent.putExtra("nomeFilm", nomeFilm);
        intent.putExtra("tornaInfoFilm", tornaInfoFilm);
        startActivity(intent);
    }

    private class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
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

