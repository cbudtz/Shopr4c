package com.nexb.shopr4.dataModel;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

/**
 * @author Christian
 */
public class InstantAutoCompleteTextView extends AutoCompleteTextView {
    public InstantAutoCompleteTextView(Context context) {
        super(context);
    }

    public InstantAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InstantAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
//
//    @Override
//    protected void onFocusChanged(boolean focused, int direction,
//                                  Rect previouslyFocusedRect) {
//        super.onFocusChanged(focused, direction, previouslyFocusedRect);
//        System.out.println("Focus changed - InstantAutoComplete");
//        if (focused) {
//            //performFiltering(getText(), 0);
//            //this.showDropDown();
//
//        }
//    }


}
