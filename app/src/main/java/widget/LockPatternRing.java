package widget;

import android.animation.*;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

/**
 * Created by dupengtao on 14-9-3.
 */
public class LockPatternRing implements
        ValueAnimator.AnimatorUpdateListener {

    private static final String OUTER_RADIUS_CIRCLE_SCALE = "outerRadiusCircleScale";
    private static final String OUTER_RADIUS_CIRCLE_ALPHA = "outerRadiusCircleAlpha";
    private static final String INNER_RADIUS_CIRCLE_SCALE = "innerRadiusCircleScale";
    private static final String INNER_RADIUS_CIRCLE_ALPHA = "innerRadiusCircleAlpha";
    private static int COLOR_NORMAL = 0x80FFFFFF;
    private static int COLOR_WRONG = 0x80FF0000;
    private static int COLOR_INNER_COLOR = Color.WHITE;
    private static int COLOR_RING_COLOR = 0x80FF0000;
    private static final String INNER_COLOR = "innerColor";
    private static final String RING_COLOR = "ringColor";
    private final View mView;
    private final int innerRadius, innerStrokeWidth, outerRadius;
    private float innerRadiusCircleScale = 0.0f, innerRadiusCircleAlpha = 0.0f, strokeRedAlpha = 1f, outerRadiusCircleScale = 0.0f, outerRadiusCircleAlpha = 0.0f;
    private int innerColor = Color.WHITE;
    private int ringColor = COLOR_NORMAL;
    private int outerColor = Color.WHITE;
    private AnimatorSet mRingAnim, mRingErrorAnim;


    public LockPatternRing(Context context, View view, int innerCircleColor, int ringColor, int outerCircleColor) {
        this(context, view);
        innerColor = innerCircleColor;
        this.ringColor = ringColor;
        outerColor = outerCircleColor;
        COLOR_RING_COLOR = ringColor;
        COLOR_INNER_COLOR = innerCircleColor;
    }

    public LockPatternRing(Context context, View view) {
        innerRadius = dip2px(context, 3f);
        innerStrokeWidth = dip2px(context, 1);
        outerRadius = dip2px(context, 22);
        mView = view;
    }

    public void drawRing(Canvas canvas, float cx, float cy) {

        //innerCircle
        Paint p1 = getPaint();
        p1.setColor(innerColor);
        p1.setAlpha((int) (255 * innerRadiusCircleAlpha));
        canvas.drawCircle(cx, cy, innerRadius * innerRadiusCircleScale, p1);

        //CircleStroke
        Paint p2 = getPaint();
        p2.setColor(ringColor);
        p2.setStyle(Paint.Style.STROKE);
        p2.setStrokeWidth(innerStrokeWidth);
        canvas.drawCircle(cx, cy, innerRadius + innerStrokeWidth / 2, p2);

        //outerRing
        Paint p3 = getPaint();
        p3.setColor(outerColor);
        p3.setAlpha((int) (255 * outerRadiusCircleAlpha));
        canvas.drawCircle(cx, cy, outerRadius * outerRadiusCircleScale, p3);
    }

    private Paint getPaint() {
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setDither(true);
        return p;
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private ObjectAnimator getInterDownSizeAnim() {
        PropertyValuesHolder pvSize = PropertyValuesHolder.ofFloat(INNER_RADIUS_CIRCLE_SCALE,
                0f, 1f);
        ObjectAnimator sizeAnim = ObjectAnimator.ofPropertyValuesHolder(
                this, pvSize).setDuration(350);
        sizeAnim.addUpdateListener(this);
        sizeAnim.setAutoCancel(true);
        return sizeAnim;
    }

    private ObjectAnimator getOuterDownSizeAnim() {
        PropertyValuesHolder pvSize = PropertyValuesHolder.ofFloat(OUTER_RADIUS_CIRCLE_SCALE,
                0.5f, 1f);
        ObjectAnimator sizeAnim = ObjectAnimator.ofPropertyValuesHolder(
                this, pvSize).setDuration(350);
        sizeAnim.setInterpolator(new DecelerateInterpolator());
        sizeAnim.addUpdateListener(this);
        sizeAnim.setAutoCancel(true);
        return sizeAnim;
    }

    private ObjectAnimator getInnerDownPaintAlphaAnim() {
        PropertyValuesHolder pvAlpha = PropertyValuesHolder.ofFloat(INNER_RADIUS_CIRCLE_ALPHA,
                0.2f, 0.4f);
        ObjectAnimator alphaAnim = ObjectAnimator.ofPropertyValuesHolder(
                this, pvAlpha).setDuration(350);
        alphaAnim.addUpdateListener(this);
        alphaAnim.setAutoCancel(true);
        return alphaAnim;
    }


    private ObjectAnimator getOuterDownPaintAlphaAnim() {
        PropertyValuesHolder pvAlpha = PropertyValuesHolder.ofFloat(OUTER_RADIUS_CIRCLE_ALPHA,
                0f, 0.2f);
        ObjectAnimator alphaAnim = ObjectAnimator.ofPropertyValuesHolder(
                this, pvAlpha).setDuration(350);
        //alphaAnim.setStartDelay(225);
        alphaAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        alphaAnim.addUpdateListener(this);
        alphaAnim.setAutoCancel(true);
        return alphaAnim;
    }

    private ObjectAnimator getOuterUpPaintAlphaAnim() {
        PropertyValuesHolder pvAlpha = PropertyValuesHolder.ofFloat(OUTER_RADIUS_CIRCLE_ALPHA,
                0.2f, 0f);
        ObjectAnimator alphaAnim = ObjectAnimator.ofPropertyValuesHolder(
                this, pvAlpha).setDuration(350);
        alphaAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        alphaAnim.addUpdateListener(this);
        alphaAnim.setAutoCancel(true);
        return alphaAnim;
    }

    public void downAnim() {
        cancelAnim();
        ObjectAnimator outerDownSizeAnim = getOuterDownSizeAnim();
        ObjectAnimator interDownSizeAnim = getInterDownSizeAnim();
        ObjectAnimator paintOuterAlphaAnim = getOuterDownPaintAlphaAnim();
        ObjectAnimator paintInnerAlphaAnim = getInnerDownPaintAlphaAnim();
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(outerDownSizeAnim).with(interDownSizeAnim).with(paintOuterAlphaAnim).with(paintInnerAlphaAnim);
        animatorSet.start();
        animatorSet.addListener(new EmptyAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                getOuterUpPaintAlphaAnim().start();
            }

        });
        mRingAnim = animatorSet;
    }

    private void cancelAnim() {
        if (mRingAnim != null && mRingAnim.isRunning()) {
            mRingAnim.end();
        }
        if (mRingErrorAnim != null && mRingErrorAnim.isRunning()) {
            mRingErrorAnim.end();
        }
    }


    public void doError() {
        cancelAnim();
        //ObjectAnimator innerColorAnim = getInnerErrorColorAnim();
        //ObjectAnimator ringColorAnim = getRingErrorColorAnim();
        //AnimatorSet animatorSet = new AnimatorSet();
        //animatorSet.play(innerColorAnim).with(ringColorAnim);
        //animatorSet.start();
        //mRingErrorAnim = animatorSet;
        setInnerColor(COLOR_WRONG);
        setRingColor(COLOR_WRONG);
    }

    private ObjectAnimator getRingErrorColorAnim() {
        PropertyValuesHolder pvColor = PropertyValuesHolder.ofInt(RING_COLOR,
                COLOR_NORMAL, COLOR_WRONG);
        ObjectAnimator colorAnim = ObjectAnimator.ofPropertyValuesHolder(
                this, pvColor).setDuration(175);
        colorAnim.setInterpolator(new LinearInterpolator());
        colorAnim.addUpdateListener(this);
        colorAnim.setAutoCancel(true);
        return colorAnim;
    }

    private ObjectAnimator getInnerErrorColorAnim() {
        PropertyValuesHolder pvColor = PropertyValuesHolder.ofInt(INNER_COLOR,
                COLOR_NORMAL, COLOR_WRONG);
        ObjectAnimator colorAnim = ObjectAnimator.ofPropertyValuesHolder(
                this, pvColor).setDuration(175);
        colorAnim.setInterpolator(new LinearInterpolator());
        colorAnim.addUpdateListener(this);
        colorAnim.setAutoCancel(true);
        return colorAnim;
    }


    public void resetRing() {
        cancelAnim();
        setInnerRadiusCircleScale(0);
        setInnerColor(COLOR_INNER_COLOR);
        setRingColor(COLOR_RING_COLOR);
    }


    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        mView.invalidate();
    }

    public float getStrokeRedAlpha() {
        return strokeRedAlpha;
    }

    public void setStrokeRedAlpha(float strokeRedAlpha) {
        this.strokeRedAlpha = strokeRedAlpha;
    }

    public float getInnerRadiusCircleScale() {
        return innerRadiusCircleScale;
    }

    public void setInnerRadiusCircleScale(float innerRadiusCircleScale) {
        this.innerRadiusCircleScale = innerRadiusCircleScale;
    }

    public float getInnerRadiusCircleAlpha() {
        return innerRadiusCircleAlpha;
    }

    public void setInnerRadiusCircleAlpha(float innerRadiusCircleAlpha) {
        this.innerRadiusCircleAlpha = innerRadiusCircleAlpha;
    }

    public float getOuterRadiusCircleScale() {
        return outerRadiusCircleScale;
    }

    public void setOuterRadiusCircleScale(float outerRadiusCircleScale) {
        this.outerRadiusCircleScale = outerRadiusCircleScale;
    }

    public float getOuterRadiusCircleAlpha() {
        return outerRadiusCircleAlpha;
    }

    public void setOuterRadiusCircleAlpha(float outerRadiusCircleAlpha) {
        this.outerRadiusCircleAlpha = outerRadiusCircleAlpha;
    }

    public int getInnerColor() {
        return innerColor;
    }

    public void setInnerColor(int innerColor) {
        this.innerColor = innerColor;
    }

    public int getRingColor() {
        return ringColor;
    }

    public void setRingColor(int ringColor) {
        this.ringColor = ringColor;
    }

    class EmptyAnimatorListener implements Animator.AnimatorListener {

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {

        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }

    }
}
