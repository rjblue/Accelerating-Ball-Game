package org.jianjian.ballgame;

import java.util.Timer;
import java.util.TimerTask;

import org.jianjian.ballgame.view.BallView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.Toast;

import com.cmcm.ballgame.R;

public class GameActivity extends Activity implements OnGestureListener {
    final int FLUSHINTERVAL = 10; // 小球刷新时间 ms
    final int mSpeedUpRate = 10; // 进入加速区后的加速值
    BallView mBall = null;
    Handler mHandler = null; // 主线程handler,处理定时器请求
    float mMoveX = 0; // 每次移动的水平距离
    float mMoveY = 0; // 每次移动的垂直距离
    float mInitSpeed = 0;
    float mSlowRate = 0;
    int mScreenHeight = 0;
    int mScreenWeight = 0;
    int mScreenChange = 200; // 屏幕缩放程度
    int mScreenRecord = 0;
    boolean mEnableMovint = false;
    GestureDetector mDetector = null; // 手势监听器
    Timer mTimer = null; // 定时器
    int mCount = 0; // 记录碰撞次数

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置全屏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        // WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game);

        mBall = new BallView(this);
        setContentView(mBall);

        mDetector = new GestureDetector(this, this);
        mScreenHeight = getWindowManager().getDefaultDisplay().getHeight();
        mScreenWeight = getWindowManager().getDefaultDisplay().getWidth();
        Intent it = getIntent();
        mInitSpeed = it.getFloatExtra("initSpeed", 0);
        mSlowRate = it.getFloatExtra("slowRate", 0);
        // 该handler处理定时移动任务
        mHandler = new Handler() {
            @Override
            public synchronized void handleMessage(Message msg) {
                if (mBall.getCurrentX() <= 0 + mBall.getRadius()) {
                    mMoveX = Math.abs(mMoveX);
                    mCount++;
                }
                if (mBall.getCurrentX() >= mScreenWeight - mBall.getRadius()) {
                    mMoveX = -Math.abs(mMoveX);
                    mCount++;
                }
                if (mBall.getCurrentY() <= 0 + mBall.getRadius()) {
                    mMoveY = Math.abs(mMoveY);
                    mCount++;
                }
                if (mBall.getCurrentY() >= mScreenHeight - mBall.getRadius()) {
                    mMoveY = -Math.abs(mMoveY);
                    mCount++;
                }
                // 判断是否进入加速区
                if (mBall.getCurrentX() >= mScreenWeight * 3 / 4 - 50
                        && mBall.getCurrentX() <= mScreenWeight * 3 / 4 + 50
                        && mBall.getCurrentY() >= mScreenHeight / 4 - 50
                        && mBall.getCurrentY() <= mScreenHeight / 4 + 50) {

                    mMoveX += mSpeedUpRate;
                    mMoveY += mSpeedUpRate;
                    Toast.makeText(GameActivity.this, "加速！！", 20).show();
                }
                mBall.move(mBall.getCurrentX() + mMoveX, mBall.getCurrentY()
                        + mMoveY); // 移动小球
                // 减速
                if (Math.abs(mMoveX) <= mSlowRate) {
                    mMoveX = 0;
                } else {
                    if (mMoveX > 0) {
                        mMoveX -= mSlowRate;
                    } else {
                        mMoveX += mSlowRate;
                    }
                }
                if (Math.abs(mMoveY) <= mSlowRate) {
                    mMoveY = 0;
                } else {
                    if (mMoveY > 0) {
                        mMoveY -= mSlowRate;
                    } else {
                        mMoveY += mSlowRate;
                    }
                }

                // 当速度为0时，停止
                if (Math.abs(mMoveX) <= 0 && Math.abs(mMoveY) <= 0) {
                    mTimer.cancel();
                    getResult();
                }
            }
        };
        Toast.makeText(this, "拨动小球！", 20).show();
    }

    // 一小轮结束后的处理
    void getResult() {
        mTimer.cancel(); // 停止定时器
        SharedPreferences perference = getSharedPreferences("record",
                MODE_PRIVATE); // 获取记录文件
        Editor editor = perference.edit();
        int oldCount = perference.getInt("count", -1);
        if (mCount > oldCount) {
            Toast.makeText(GameActivity.this, "新纪录！！碰撞了" + mCount + "次！",
                    Toast.LENGTH_SHORT).show();
            editor.putInt("count", mCount); // 写入新纪录
            editor.commit();
        } else {
            Toast.makeText(GameActivity.this, "碰撞了" + mCount + "次！",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDetector.onTouchEvent(event);
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // TODO Auto-generated method stub
        getResult();
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
            float velocityY) {
        if (mTimer != null)
            mTimer.cancel();
        // 通过比例计算X Y方向的分速度
        double lenX = e2.getX() - e1.getX();
        double lenY = e2.getY() - e1.getY();
        double lenZ = Math.sqrt(Math.pow(lenX, 2) + Math.pow(lenY, 2));
        mMoveX = (float) (mInitSpeed * lenX / lenZ);
        mMoveY = (float) (mInitSpeed * lenY / lenZ);
        mCount = 0;
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(0);
            }
        }, 0, FLUSHINTERVAL);
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
            float distanceY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.standard_activity_one, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings1) {
            if (mScreenRecord < 0) {
                mScreenHeight += mScreenChange;
                mScreenWeight += mScreenChange;
                mScreenRecord += mScreenChange;
            }
        }
        if (id == R.id.action_settings2) {
            if (mScreenRecord == -3 * mScreenChange) {
                Toast.makeText(this, "不能继续缩放！", Toast.LENGTH_SHORT).show();
                return true;
            } else {
                mScreenHeight -= mScreenChange;
                mScreenWeight -= mScreenChange;
                mScreenRecord -= mScreenChange;
            }
        }
        mBall.setmScreenHeight(mScreenHeight);
        mBall.setmScreenWeight(mScreenWeight);
        mBall.moveToStartPos();
        return true;
    }
}
