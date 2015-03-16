package com.exaple.bgremoval;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * on 3/11/15.
 */
public class GraphicView extends View {

    public static enum TouchMode {
        ERASE,
        RESTORE
    }

    private static final float TOUCH_TOLERANCE = 4;

    private float mX, mY;

    private Bitmap mBmpOrigImage;
    private Bitmap mBmpResultImage;
    private Bitmap mBmpMask;
    private Paint mPaintImage;
    private Paint mPaintMask;
    private Path mPath;
    private Canvas mCanvasMask;
    private Canvas mCanvasResult;
    private PorterDuffXfermode mPdModeDstOut;
    private PorterDuffXfermode mPdClear;

    private TouchMode mTouchMode;

    public GraphicView(Context context) {
        super(context);
        init();
    }

    public GraphicView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GraphicView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        mTouchMode = TouchMode.ERASE;

        mPath = new Path();

        mPdModeDstOut = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT); // DST_OUT similar to CLEAR
        mPdClear = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);

        mPaintImage = new Paint(Paint.ANTI_ALIAS_FLAG);

        mPaintMask = new Paint();
        mPaintMask.setAntiAlias(true);
        mPaintMask.setDither(true);
        mPaintMask.setColor(0xFFFF0000);
        mPaintMask.setStyle(Paint.Style.STROKE);
        mPaintMask.setStrokeJoin(Paint.Join.ROUND);
        mPaintMask.setStrokeCap(Paint.Cap.ROUND);
        mPaintMask.setStrokeWidth(12);

        mCanvasMask = new Canvas();
        mCanvasResult = new Canvas();
    }

    public void setImage(Bitmap bmp) {

        mBmpOrigImage = null;
        mBmpMask = null;

        if (bmp != null) {
            mBmpOrigImage = bmp;
            mBmpMask = Bitmap.createBitmap(mBmpOrigImage.getWidth(), mBmpOrigImage.getHeight(), Bitmap.Config.ALPHA_8);
            mBmpResultImage = Bitmap.createBitmap(mBmpOrigImage.getWidth(), mBmpOrigImage.getHeight(), Bitmap.Config.ARGB_8888);
            mCanvasMask.setBitmap(mBmpMask);
            mCanvasResult.setBitmap(mBmpResultImage);
        }
    }

    public void setTouchMode(TouchMode touchMode) {

        mTouchMode = touchMode;
    }

    public TouchMode getTouchMode() {

        return mTouchMode;
    }

    private void touchStart(float x, float y) {

        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }
    private void touchMove(float x, float y) {

        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }
    private void touchUp() {
        mPath.lineTo(mX, mY);
        // commit the path to our offscreen
        /////////mCanvasMask.drawPath(mPath, mPaintMask);
        // kill this so we don't double draw
        mPath.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                invalidate();
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);

        canvas.drawColor(0xffffffff);

        if (mBmpOrigImage != null) {

            /*int sc = mCanvasResult.saveLayer(0, 0, mBmpOrigImage.getWidth(), mBmpOrigImage.getHeight(), null,
                    Canvas.MATRIX_SAVE_FLAG |
                            Canvas.CLIP_SAVE_FLAG |
                            Canvas.HAS_ALPHA_LAYER_SAVE_FLAG |
                            Canvas.FULL_COLOR_LAYER_SAVE_FLAG |
                            Canvas.CLIP_TO_LAYER_SAVE_FLAG);*/

            mCanvasResult.drawBitmap(mBmpOrigImage, 0, 0, mPaintImage); // draw background

            if (mTouchMode == TouchMode.RESTORE) {
                mPaintMask.setXfermode(mPdClear); // user touches clear the mask
            }
            mCanvasMask.drawPath(mPath, mPaintMask); // draw user touch to mask
            mPaintMask.setXfermode(mPdModeDstOut); // set Xfermode
            mCanvasResult.drawBitmap(mBmpMask, 0, 0, mPaintMask); // apply mask to background
            mPaintMask.setXfermode(null); // restore Xfermode

            //mCanvasResult.restoreToCount(sc);

            canvas.drawBitmap(mBmpResultImage, 0, 0, mPaintImage);
        }
    }
}
