/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.palistudios.multigame.tools;

import android.content.Context;

/**
 * @author virdzek
 */
public class GraphicUnitsConvertor {

    public static int convertDptoPx(Context context, int dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
