package com.fei.pathmeasureview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.media.ThumbnailUtils;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Administrator on 2017/11/23.
 */
public class PathMeasuerView extends View {

    private Paint mPaint;
    private PathMeasure pathMeasure;
    private Bitmap jtBitmap;
    private Path mPath;
    private int vWidth;
    private int vHeight;
    private float[] pos;
    private float[] tan;
    private float ratio;
    private float bitmapLength;
    private Matrix mMatrix;

    public PathMeasuerView(Context context) {
        this(context, null);
    }

    public PathMeasuerView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public PathMeasuerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5);

        mMatrix = new Matrix();
        mPath = new Path();
        pos = new float[2];
        tan = new float[2];
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;//在压缩之前将option的值
        jtBitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeResource(getResources(), R.drawable.jt, options), dip2px(getContext(), 30),
                dip2px(getContext(), 30), ThumbnailUtils.OPTIONS_RECYCLE_INPUT);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (vWidth == 0) {
            vWidth = getMeasuredWidth();
            vHeight = getMeasuredHeight();
            mPath.addCircle(vWidth / 2, vHeight / 2, 200, Path.Direction.CW);
            pathMeasure = new PathMeasure(mPath, false);
            bitmapLength = pathMeasure.getLength();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawPath(mPath, mPaint);

        ratio += 0.005f;
        if (ratio > 1) {
            ratio = 0;
        }
        pathMeasure.getPosTan(ratio * bitmapLength, pos, tan);
        mMatrix.reset();
        float degree = (float) (Math.atan2(tan[1], tan[0]) * 180 / Math.PI);
        mMatrix.postRotate(degree, jtBitmap.getWidth() / 2, jtBitmap.getHeight() / 2);
        mMatrix.postTranslate(pos[0] - jtBitmap.getWidth() / 2, pos[1] - jtBitmap.getHeight() / 2);
        canvas.drawBitmap(jtBitmap, mMatrix, mPaint);
        invalidate();
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
