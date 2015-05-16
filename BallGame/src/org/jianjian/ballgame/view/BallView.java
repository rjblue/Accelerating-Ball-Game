package org.jianjian.ballgame.view;

import com.cmcm.ballgame.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;

public class BallView extends View {
    Activity mCtx = null;
    Paint mPaint = new Paint();
    float mCurrentX = 0;
    float mCurrentY = 0;
    int mScreenHeight = 0;
    int mScreenWeight = 0;
    int mRadius = 32; // 图片大小

    @SuppressWarnings("deprecation")
    public BallView(Context context) {
        super(context);
        mCtx = (Activity) context;
        // 初始位置在屏幕中间
        mScreenWeight = mCtx.getWindowManager().getDefaultDisplay().getWidth();
        mScreenHeight = mCtx.getWindowManager().getDefaultDisplay().getHeight();
        mCurrentX = mScreenWeight / 2;
        mCurrentY = mScreenHeight / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        // 画边界
        mPaint.setColor(Color.RED);
        canvas.drawText("边界", mScreenWeight, mScreenHeight, mPaint);
        // 画加速区
        mPaint.setAlpha(10);
        mPaint.setColor(Color.GRAY);
        canvas.drawCircle(mScreenWeight * 3 / 4 + 50, mScreenHeight / 4 + 50,
                50, mPaint);
        // 画球
        Bitmap ballIco = ((BitmapDrawable) getResources().getDrawable(
                R.drawable.ball2)).getBitmap();
        canvas.drawBitmap(ballIco, mCurrentX, mCurrentY, mPaint);
    }

    // 移动小球
    public void move(float x, float y) {
        mCurrentX = x;
        mCurrentY = y;
        invalidate();
    }

    // 返回到初始位置
    public void moveToStartPos() {
        mCurrentX = 2 * mRadius;
        mCurrentY = 2 * mRadius;
        invalidate();
    }

    public int getRadius() {
        return mRadius;
    }

    public float getCurrentX() {
        return mCurrentX;
    }

    public void setCurrentX(float mCurrentX) {
        this.mCurrentX = mCurrentX;
    }

    public float getCurrentY() {
        return mCurrentY;
    }

    public void setCurrentY(float mCurrentY) {
        this.mCurrentY = mCurrentY;
    }

    public int getmScreenHeight() {
        return mScreenHeight;
    }

    public void setmScreenHeight(int mScreenHeight) {
        this.mScreenHeight = mScreenHeight;
    }

    public int getmScreenWeight() {
        return mScreenWeight;
    }

    public void setmScreenWeight(int mScreenWeight) {
        this.mScreenWeight = mScreenWeight;
    }

}