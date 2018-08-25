package nf.application.emanuele.tesi1;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class MapWrapperLayout extends FrameLayout{
    private OnDragListener onDragListener;

    public MapWrapperLayout(Context context){
        super(context);
    }

    public interface OnDragListener{
        public void onDrag(MotionEvent motionEvent);
    }

    @Override
    public boolean dispatchTouchEvent (MotionEvent motionEvent){
        if (onDragListener != null){
            onDragListener.onDrag(motionEvent);
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    public void setOnDragListener (OnDragListener onDragListener){
        this.onDragListener = onDragListener;
    }

}
