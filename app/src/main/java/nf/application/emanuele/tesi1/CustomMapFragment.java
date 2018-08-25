package nf.application.emanuele.tesi1;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.SupportMapFragment;

public class CustomMapFragment extends SupportMapFragment {
    private View view;
    private MapWrapperLayout mapWrapperLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle ssavedInstanceState){
        view = super.onCreateView(inflater, container, ssavedInstanceState);
        mapWrapperLayout = new MapWrapperLayout(getActivity());
        mapWrapperLayout.addView(view);
        return mapWrapperLayout;
    }

    @Override
    public View getView(){
        return view;
    }

    public void setOnDragListener(MapWrapperLayout.OnDragListener onDragListener){
        mapWrapperLayout.setOnDragListener(onDragListener);
    }
}
