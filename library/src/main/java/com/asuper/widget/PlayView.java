package com.asuper.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.asuper.library.R;

/**
 * Created by Joker on 2016/6/13.
 */
public class PlayView extends View {
    private static final String TAG = "PlayView";

    private final static double SQRT_3 = Math.sqrt(3f);
    private final static int SPEED = 1;

    private final Point mPoint;
    private Paint mPaint;
    private Path mLeftPath;
    private Path mRightPath;

    private ValueAnimator mCenterEdgeAnimator;
    private ValueAnimator mLeftEdgeAnimator;
    private ValueAnimator mRightEdgeAnimator;
    private OnControlStatusChangeListener mListener;
    private ValueAnimator.AnimatorUpdateListener mAnimatorUpdateListener =
            new ValueAnimator.AnimatorUpdateListener() {
                @Override public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    invalidate();
                }
            };

    private boolean mPlayed;
    private int mBackgroundColor = Color.BLACK;

    public PlayView(Context context) {
        this(context, null, 0);
    }

    public PlayView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setDef(context, attrs, defStyleAttr);
        mPoint = new Point();
        initView();
    }

    private void setDef(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PlayView, defStyleAttr, 0);
        int count = array.getIndexCount();
        for (int i = 0; i < count; i++) {
            int attr = array.getIndex(i);
            if (attr == R.styleable.PlayView_viewColor) {
                mBackgroundColor = array.getColor(attr, Color.BLACK);
            }
        }
    }


    private void initView() {
        setUpPaint();
        setUpPath();
        setUpAnimator();
    }
    
    

    private void setUpPaint() {
        mPaint = new Paint();
        mPaint.setColor(mBackgroundColor);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
    }

    private void setUpPath() {
        mLeftPath = new Path();
        mRightPath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPoint.setWidth(canvas.getWidth());
        mPoint.setHeight(canvas.getHeight());

        mLeftPath.reset();
        mRightPath.reset();

        Log.i(TAG, mPoint.getX(-0.5 * SQRT_3) + " " + mPoint.getY(1.f) + "");
        mLeftPath.moveTo(mPoint.getX(-0.5 * SQRT_3), mPoint.getY(1.f));
        mLeftPath.lineTo(mPoint.getY((Float) mLeftEdgeAnimator.getAnimatedValue()) + 0.7f,
                mPoint.getY((Float) mCenterEdgeAnimator.getAnimatedValue()));
        mLeftPath.lineTo(mPoint.getY((Float) mLeftEdgeAnimator.getAnimatedValue()) + 0.7f,
                mPoint.getY(-1 * (Float) mCenterEdgeAnimator.getAnimatedValue()));
        mLeftPath.lineTo(mPoint.getX(-0.5 * SQRT_3), mPoint.getY(-1.f));

        Log.i(TAG, mPoint.getY(-1 * (Float) mLeftEdgeAnimator.getAnimatedValue()) + " "
                + mPoint.getY((Float) mCenterEdgeAnimator.getAnimatedValue()) + "");
        mRightPath.moveTo(mPoint.getY(-1 * (Float) mLeftEdgeAnimator.getAnimatedValue()),
                mPoint.getY((Float) mCenterEdgeAnimator.getAnimatedValue()));
        mRightPath.lineTo(mPoint.getX(0.5 * SQRT_3),
                mPoint.getY((Float) mRightEdgeAnimator.getAnimatedValue()));
        mRightPath.lineTo(mPoint.getX(0.5 * SQRT_3),
                mPoint.getY(-1 * (Float) mRightEdgeAnimator.getAnimatedValue()));
        mRightPath.lineTo(mPoint.getY(-1 * (Float) mLeftEdgeAnimator.getAnimatedValue()),
                mPoint.getY(-1 * (Float) mCenterEdgeAnimator.getAnimatedValue()));

        canvas.drawPath(mLeftPath, mPaint);
        canvas.drawPath(mRightPath, mPaint);
    }

    private void setUpAnimator() {
        if (mPlayed) {
            mCenterEdgeAnimator = ValueAnimator.ofFloat(1.1f, 1.0f);
            mLeftEdgeAnimator = ValueAnimator.ofFloat((float) (-0.2f * SQRT_3), (float) (-0.2f * SQRT_3));
            mRightEdgeAnimator = ValueAnimator.ofFloat(1.0f, 1.0f);
        } else {
            mCenterEdgeAnimator = ValueAnimator.ofFloat(0.5f, 0.5f);
            mLeftEdgeAnimator = ValueAnimator.ofFloat(0f, 0f);
            mRightEdgeAnimator = ValueAnimator.ofFloat(0f, 0f);
        }

        mCenterEdgeAnimator.start();
        mLeftEdgeAnimator.start();
        mRightEdgeAnimator.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setPlayed(!mPlayed);
                startAnimation();
                if (mListener != null) {
                    mListener.onStatusChange(this, mPlayed);
                }
                break;
        }
        return false;
    }


    public void startAnimation() {
        mCenterEdgeAnimator = ValueAnimator.ofFloat(1.f, 0.5f);
        mCenterEdgeAnimator.setDuration(100 * SPEED);
        mCenterEdgeAnimator.addUpdateListener(mAnimatorUpdateListener);

        mLeftEdgeAnimator = ValueAnimator.ofFloat((float) (-0.2 * SQRT_3), 0f);
        mLeftEdgeAnimator.setDuration(100 * SPEED);
        mLeftEdgeAnimator.addUpdateListener(mAnimatorUpdateListener);

        mRightEdgeAnimator = ValueAnimator.ofFloat(1.f, 0.f);
        mRightEdgeAnimator.setDuration(150 * SPEED);
        mRightEdgeAnimator.addUpdateListener(mAnimatorUpdateListener);

        if (!mPlayed) {
            mCenterEdgeAnimator.start();
            mLeftEdgeAnimator.start();
            mRightEdgeAnimator.start();
        } else {
            mCenterEdgeAnimator.reverse();
            mLeftEdgeAnimator.reverse();
            mRightEdgeAnimator.reverse();
        }
    }


    public void setOnControlStatusChangeListener(OnControlStatusChangeListener listener) {
        mListener = listener;
    }


    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SaveState saveState = new SaveState(superState);
        saveState.played = isPlayed();
        return saveState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SaveState saveState = (SaveState) state;
        super.onRestoreInstanceState(saveState.getSuperState());
        setPlayed(saveState.played);
        setUpAnimator();
        invalidate();
    }

    public boolean isPlayed() {
        return mPlayed;
    }


    public void setPlayed(boolean played) {
        if (mPlayed != played) {
            mPlayed = played;
            invalidate();
        }
    }

    public void setColor(int color) {
        mBackgroundColor = color;
        mPaint.setColor(mBackgroundColor);
        invalidate();
    }

    private static class SaveState extends BaseSavedState {
        boolean played;

        public static final Creator<SaveState> CREATOR = new Creator<SaveState>() {
            @Override
            public SaveState createFromParcel(Parcel source) {
                return new SaveState(source);
            }

            @Override
            public SaveState[] newArray(int size) {
                return new SaveState[size];
            }
        };

        SaveState(Parcelable superState) {
            super(superState);
        }

        private SaveState(Parcel in) {
            super(in);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeValue(played);
        }
    }

    private static class Point {
        private int width;
        private int height;

        public void setWidth(int width) {
            this.width = width;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public float getX(float x) {
            return (width / 2) * (x + 1);
        }

        public float getY(float y) {
            return (height / 2) * (y + 1);
        }

        public float getX(double x) {
            return getX((float) x);
        }

        public float getY(double y) {
            return getY((float) y);
        }
    }


    public interface OnControlStatusChangeListener {
        void onStatusChange(View view, boolean state);
    }
}
