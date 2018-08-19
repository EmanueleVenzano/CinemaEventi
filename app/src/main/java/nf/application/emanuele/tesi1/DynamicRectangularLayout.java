package nf.application.emanuele.tesi1;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class DynamicRectangularLayout extends ImageView {

    public DynamicRectangularLayout(Context context) {
        super(context);
    }

    public DynamicRectangularLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public DynamicRectangularLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, (widthMeasureSpec/2));
        int size = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(size, (size+150));
    }

}