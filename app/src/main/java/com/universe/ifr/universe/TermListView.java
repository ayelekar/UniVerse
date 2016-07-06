package com.universe.ifr.universe;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ListView;

//Custom listview class for the course list page
public class TermListView extends ListView{
    public TermListView(Context context) {
        super(context);
    }
    public TermListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public TermListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    //Define the height to be the height of the content
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMeasureSpec_custom = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec_custom);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
    }

}
