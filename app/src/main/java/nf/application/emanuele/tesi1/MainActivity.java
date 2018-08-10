package nf.application.emanuele.tesi1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MainActivity extends Activity implements View.OnClickListener{
    private Button map;
    private Button saved;
    private Button search;
    private Button events;
    Cinemas c;
    private final String FILENAME = "news_feed.xml";
    RSSFeed feed;


    @Override
    public void onCreate (Bundle savedInstaceState){
        super.onCreate(savedInstaceState);
        setContentView(R.layout.activity_main);
        map = (Button) findViewById(R.id.MapButton);
        saved = (Button) findViewById(R.id.SavedButton);
        search = (Button) findViewById(R.id.SearchButton);
        events = (Button) findViewById(R.id.EventidButton);
        map.setOnClickListener(this);
        saved.setOnClickListener(this);
        search.setOnClickListener(this);
        events.setOnClickListener(this);
        new DownloadFeed().execute();
    }

    public void onClick (View v){
        Intent intent = null;
        switch (v.getId()){
            case R.id.MapButton:
                intent = new Intent (this, MapsActivity.class);
                intent.putExtra("name", "");
                break;
            case R.id.SavedButton:
                intent = new Intent (this, MapsActivity.class);
                break;
            case R.id.SearchButton:
                intent = new Intent (this, Locandine.class);
                break;
            case R.id.EventidButton:
                intent = new Intent (this,InfoCinema.class);
                intent.putExtra("name", "Fiumara");;
                break;
        }
        if (intent!=null){
            startActivity(intent);
        }
    }

    class DownloadFeed extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                Context context = MainActivity.this;
                FileOutputStream out = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
                URL url = new URL("https://www.mymovies.it/cinema/xml/rss/?id=genova");
                InputStream in = url.openStream();
                byte[] buffer = new byte[1024];
                int bytesRead = in.read(buffer);
                while (bytesRead != -1) {
                    out.write(buffer, 0, bytesRead);
                    bytesRead = in.read(buffer);
                }
                in.close();
                out.close();
            } catch (IOException e) {
                Log.e("News reader", e.toString());
            }
            Log.d("News reader", "Feed downloaded: " + new Date());
            try {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser parser = factory.newSAXParser();
                XMLReader xmlreader = parser.getXMLReader();

                RSSFeedHandler theRssHandler = new RSSFeedHandler();
                xmlreader.setContentHandler(theRssHandler);
                FileInputStream in = openFileInput(FILENAME);
                InputSource is = new InputSource(in);
                xmlreader.parse(is);
                MainActivity.this.feed = theRssHandler.getFeed();
            } catch (Exception e) {
                Log.e("News reader", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d("News reader", "Feed read: " + new Date());
        }
    }
}
