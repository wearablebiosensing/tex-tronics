package edu.uri.wbl.tex_tronics.smartglove.graph;

import android.os.Handler;

/**
 * Created by Andrew on 2/19/18.
 */

public class HandleData
{
    private static Handler handler;
    private static int mThumbFlex, mIndexFlex;

    public static void setHandler(Handler mainHandler){handler = mainHandler;}
    public static void setThumbFlex(int thumbFlex) {
        mThumbFlex = thumbFlex;
    }
    public static void setIndexFlex(int indexFlex) {
        mIndexFlex = indexFlex;
    }
    public static int getThumbFlex() {return mThumbFlex;}
    public static int getIndexFlex() {return mIndexFlex;}

    public static void sendMessage()
    {
        handler.sendEmptyMessageDelayed(1, 100);
    }
}
