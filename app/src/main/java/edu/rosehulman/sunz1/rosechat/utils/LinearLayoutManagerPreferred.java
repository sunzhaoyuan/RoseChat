package edu.rosehulman.sunz1.rosechat.utils;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

/**
 * Created by sun on 2/13/18.
 */

public class LinearLayoutManagerPreferred extends LinearLayoutManager {

    public LinearLayoutManagerPreferred(Context context) {
        super(context);
    }

    public LinearLayoutManagerPreferred(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public LinearLayoutManagerPreferred(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return false;
    }
}
