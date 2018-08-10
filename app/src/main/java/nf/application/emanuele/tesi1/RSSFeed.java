package nf.application.emanuele.tesi1;

import android.annotation.SuppressLint;

import java.util.ArrayList;

@SuppressLint("SimpleDateFormat")
public class RSSFeed {
    private String title = null;
    private ArrayList<RSSItem> items;

    public RSSFeed() {
        items = new ArrayList<RSSItem>();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public int addItem(RSSItem item) {
        items.add(item);
        return items.size();
    }

    public RSSItem getItem(int index) {
        return items.get(index);
    }

    public ArrayList<RSSItem> getAllItems() {
        return items;
    }


}
