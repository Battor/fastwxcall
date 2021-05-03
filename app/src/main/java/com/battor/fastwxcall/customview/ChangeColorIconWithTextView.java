package com.battor.fastwxcall.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Looper;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.battor.fastwxcall.R;

public class ChangeColorIconWithTextView extends View {
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mPaint;

    private int mColor = 0xFF45C01A;    // 颜色
    private float mAlpha = 0f;  // 透明度 0.0 ~ 1.0
    private Bitmap mIconBitmap; // 图标
    private Rect mIconRect; // 限制绘制 icon 的范围

    private String mText;
    private int mTextSize = (int) TypedValue.applyDimension(
                                                TypedValue.COMPLEX_UNIT_SP,
                                            10,
                                                getResources().getDisplayMetrics());
    private Paint mTextPaint;
    private Rect mTextBound = new Rect();

    public ChangeColorIconWithTextView(Context context){
        super(context);
    }

    public ChangeColorIconWithTextView(Context context, AttributeSet attrs){
        super(context, attrs);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ChangeColorIconWithTextView);

        int count = ta.getIndexCount();
        for(int i = 0; i < count; i++) {
            int attr = ta.getIndex(i);
            switch (attr){
                case R.styleable.ChangeColorIconWithTextView_icon:
                    BitmapDrawable drawable = (BitmapDrawable) ta.getDrawable(attr);
                    mIconBitmap = drawable.getBitmap();
                    break;
                case R.styleable.ChangeColorIconWithTextView_color:
                    mColor = ta.getColor(attr, 0x45C01A);
                    break;
                case R.styleable.ChangeColorIconWithTextView_text:
                    mText = ta.getString(attr);
                    break;
                case R.styleable.ChangeColorIconWithTextView_text_size:
                    mTextSize = (int) ta.getDimension(attr, TypedValue.applyDimension(
                                                                TypedValue.COMPLEX_UNIT_SP,
                                                            10,
                                                                getResources().getDisplayMetrics()));
                    break;
            }
        }

        ta.recycle();

        mTextPaint = new TextPaint();
        mTextPaint.setFakeBoldText(true);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(0xff555555);
        mTextPaint.getTextBounds(mText, 0, mText.length(), mTextBound);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 得到绘制 icon 的宽（由最小的长度决定）
        int bitmapWidth = Math.min(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(),
                                    getMeasuredHeight() - getPaddingTop() - getPaddingBottom() - mTextBound.height());

        //设置 icon 的绘制范围
        int left = getMeasuredWidth() / 2 - bitmapWidth / 2;
        int top = (getMeasuredHeight() - mTextBound.height()) / 2 - bitmapWidth / 2;

        mIconRect = new Rect(left, top, left + bitmapWidth, top + bitmapWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int alpha = (int) Math.ceil(255 * mAlpha);  // 1. 计算 alpha 值

        canvas.drawBitmap(mIconBitmap, null, mIconRect, null);  // 2. 绘制原图
        setupTargetBitmap(alpha);
        drawSourceText(canvas, alpha);  // 5. 绘制原文本
        drawTargetText(canvas, alpha);  // 6. 绘制要设置的文本
        canvas.drawBitmap(mBitmap, 0, 0, null); // 7. 最终绘制
    }

    private void setupTargetBitmap(int alpha){
        mBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(),
                                        Bitmap.Config.ARGB_8888);   // Bitmap.Config.ARGB_8888 表示 alpha、red、green、blue 各占 8 位（共 32 位）
        mCanvas = new Canvas(mBitmap);
        mPaint = new Paint();
        mPaint.setColor(mColor);
        mPaint.setAntiAlias(true);  // 抗锯齿
        mPaint.setDither(true); // 防抖动
        mPaint.setAlpha(alpha);
        mCanvas.drawRect(mIconRect, mPaint);    // 3.绘制纯色块

        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN)); // UH
        mPaint.setAlpha(255);
        mCanvas.drawBitmap(mIconBitmap, null, mIconRect, mPaint);   // 4.再次绘制图标（第 1 个参数表示源 Bitmap 对象；第 2 个参数表示 Bitmap 对象中要绘制的区域，null 表示全部；
                                                                        // 第 3 个参数表示要绘制到屏幕上的区域；第 4 个参数为画笔，即绘制参数）
    }

    private void drawSourceText(Canvas canvas, int alpha){
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(0xff333333);
        mTextPaint.setAlpha(255 - alpha);
        canvas.drawText(mText, mIconRect.left + mIconRect.width() / 2 - mTextBound.width() / 2,
                            mIconRect.bottom + mTextBound.height(), mTextPaint);    // 根据图标位置确定文字位置（与图标居中对其）
    }

    private void drawTargetText(Canvas canvas, int alpha) {
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mColor);
        mTextPaint.setAlpha(alpha);
        canvas.drawText(mText, mIconRect.left + mIconRect.width() / 2 - mTextBound.width() / 2,
                mIconRect.bottom + mTextBound.height(), mTextPaint);    // 根据图标位置确定文字位置（与图标居中对其）
    }

    public void setTextAndIconAlpha(float alpha){
        this.mAlpha = alpha;
        invalidateView();
    }

    private void invalidateView(){
        if(Looper.getMainLooper() == Looper.myLooper()){
            invalidate();
        }else{
            postInvalidate();
        }
    }
}
