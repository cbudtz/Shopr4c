package com.nexb.shopr4;

/**
 * Created by mac on 26/11/15.
 */
public class DpToPx {

    public static float density;

    public static int dpToPx(int dp) {
        return Math.round((float)dp * density);
    }
}
