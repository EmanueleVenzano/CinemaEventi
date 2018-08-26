package nf.application.emanuele.tesi1;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ScrollView;

import java.util.logging.LogManager;

public class CustomScrollView extends ListView {
    public static int WITHOUT_MAX_HEIGHT_VALUE = -1;
    private int maxHeight = WITHOUT_MAX_HEIGHT_VALUE;

    public CustomScrollView(Context context){
        super(context);
    }

    public CustomScrollView(Context context, AttributeSet attrs, int defStyle){
        super(context,attrs,defStyle);
    }

    public CustomScrollView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMeasureSpec_custom = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE>>2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec_custom);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
    }
}
