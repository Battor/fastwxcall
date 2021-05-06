package com.battor.fastwxcall;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class Utils {
    public static Bitmap rotateBitmap(Bitmap origin, float angle){
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(angle);
        // 围绕原地进行旋转
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }else{
            origin.recycle();
            return newBM;
        }
    }
}
