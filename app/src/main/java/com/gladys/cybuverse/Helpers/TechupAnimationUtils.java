package com.gladys.cybuverse.Helpers;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.util.Property;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;

public class TechupAnimationUtils {

    private TechupAnimationUtils() {
    }

    private static void translateBounce(View view, Property<View, Float> direction, float distance, int duration) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, direction, distance);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setDuration(duration);
        animator.start();
    }

    public static void bounceX(View view, float distance, int duration) {
        translateBounce(view, View.TRANSLATION_X, distance, duration);
    }

    public static void bounceY(View view, float distance, int duration) {
        translateBounce(view, View.TRANSLATION_Y, distance, duration);
    }

    private static ObjectAnimator slideAnimation(View view, String direction, float fromDelta, float toDelta, int duration) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, direction, fromDelta, toDelta);
        animator.setDuration(duration);
        return animator;
    }

    public static void slideX(View view, float fromDelta, float toDelta, int duration, Animation.AnimationListener listener) {
//        ObjectAnimator animator = slideAnimation(view, "translationX", fromDelta, toDelta, duration);
//        if (listener != null)
//            animator.addListener(listener);
//        animator.start();
        Animation animation = new TranslateAnimation(fromDelta, toDelta, 0, 0);
        animation.setDuration(duration);
        if (listener != null)
            animation.setAnimationListener(listener);
        view.startAnimation(animation);
    }

    public static void slideY(View view, float fromDelta, float toDelta, int duration, Animation.AnimationListener listener) {
//        ObjectAnimator animator = slideAnimation(view, "translationY", fromDelta, toDelta, duration);
//        if (listener != null)
//            animator.addListener(listener);
//        animator.start();
        Animation animation = new TranslateAnimation(0, 0, fromDelta, toDelta);
        animation.setDuration(duration);
        if (listener != null)
            animation.setAnimationListener(listener);
        view.startAnimation(animation);
    }

    public static void slideX(View view, float fromDelta, float toDelta, int duration, Interpolator interpolator, Animation.AnimationListener listener) {
        Animation animation = new TranslateAnimation(fromDelta, toDelta, 0, 0);
        animation.setDuration(duration);
        animation.setInterpolator(interpolator);
        if (listener != null)
            animation.setAnimationListener(listener);
        view.startAnimation(animation);
    }

    public static void slideY(View view, float fromDelta, float toDelta, int duration, Interpolator interpolator, Animation.AnimationListener listener) {
        Animation animation = new TranslateAnimation(0, 0, fromDelta, toDelta);
        animation.setDuration(duration);
        animation.setInterpolator(interpolator);
        if (listener != null)
            animation.setAnimationListener(listener);
        view.startAnimation(animation);
    }

    public static void scaleInOut(View view, float scale, int duration) {

        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, scale);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, scale);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setDuration(duration);
        animator.start();
    }

    public void test() {
//        ObjectAnimator animatorX = ObjectAnimator.ofFloat(view, View.SCALE_X, distance);
//        animatorX.setRepeatMode(ValueAnimator.REVERSE);
//        animatorX.setRepeatCount(ValueAnimator.INFINITE);
//        animatorX.setDuration(duration);
//
//        ObjectAnimator animatorY = ObjectAnimator.ofFloat(view, View.SCALE_Y, distance);
//        animatorY.setRepeatMode(ValueAnimator.REVERSE);
//        animatorY.setRepeatCount(ValueAnimator.INFINITE);
//        animatorY.setDuration(duration);
//
//        AnimatorSet animatorSet = new AnimatorSet();
//        animatorSet.playTogether(animatorX, animatorY);
//        animatorSet.setInterpolator(new BounceInterpolator());
//        animatorSet.setDuration(duration);
//        animatorSet.start();


//        View v = findViewById(R.id.btn_login);
//        float y=v.getTranslationY();
//        float distance = 10f;
//
//        AnimatorSet set = new AnimatorSet();
//        set.playSequentially(
//                ObjectAnimator.ofFloat(v, View.TRANSLATION_Y, y-distance).setDuration(500),
//                ObjectAnimator.ofFloat(v, View.TRANSLATION_Y, y).setDuration(500),
//                ObjectAnimator.ofFloat(v, View.TRANSLATION_Y, y-(distance/2)).setDuration(500),
//                ObjectAnimator.ofFloat(v, View.TRANSLATION_Y, y).setDuration(500)
//        );
//        set.setDuration(600);
//        set.setInterpolator(new BounceInterpolator());
//        set.start();
    }

}
