package nf.application.emanuele.tesi1;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.ViewUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;

public class MaxHeightLinearLayout extends LinearLayout {

    private int maxHeightDp;

    public MaxHeightLinearLayout(Context context) {
        super(context);
    }

    public MaxHeightLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        final TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MaxHeightLinearLayout, 0, 0);
        try {
            maxHeightDp = a.getInteger(R.styleable.MaxHeightLinearLayout_maxHeightDp, 0);
        } finally {
            a.recycle();
        }
    }

    public MaxHeightLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int dpToPx(int dp){
        return Math.round(dp*(getResources().getDisplayMetrics().xdpi)/ DisplayMetrics.DENSITY_DEFAULT);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxHeightPx = dpToPx(maxHeightDp);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeightPx, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setMaxHeightDp(int maxHeightDp) {
        this.maxHeightDp = maxHeightDp;
        invalidate();
    }

}
