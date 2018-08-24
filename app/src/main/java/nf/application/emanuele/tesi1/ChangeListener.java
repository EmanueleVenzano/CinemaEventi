package nf.application.emanuele.tesi1;

import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

public class ChangeListener {
    LatLng latLng;
    private Listener listener = null;

    public ChangeListener (){
        this.latLng = null;
    }

    public interface Listener{
        public void onChange(LatLng latLng);
    }

    public void setChangeListener (Listener listener){
        this.listener = listener;
    }

    public void somethingChanged(){
        if (listener != null){
            listener.onChange(latLng);
        }
    }
}
