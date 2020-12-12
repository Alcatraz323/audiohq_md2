package io.alcatraz.audiohq.utils;

import android.animation.Animator;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.TextView;

public class AnimateUtils {
    public static void playStart(View view, SimpleAnimateInterface animateInterface) {
        playStart(view, (int) view.getX(), (int) view.getY(), 660, animateInterface);
    }

    public static void playStart(final View view, int x, int y, int duration, SimpleAnimateInterface animateInterface) {
        Animator a = ViewAnimationUtils.createCircularReveal(view, x, y, 0, (float) Math.hypot((double) view.getWidth(), (double) view.getHeight()));
        a.setInterpolator(new AccelerateInterpolator());
        a.setDuration(duration);
        a.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator p1) {

            }

            @Override
            public void onAnimationEnd(Animator p1) {
                view.setVisibility(View.VISIBLE);
                animateInterface.onEnd();
            }

            @Override
            public void onAnimationCancel(Animator p1) {

            }

            @Override
            public void onAnimationRepeat(Animator p1) {

            }
        });
        view.setAlpha(1);
        a.start();
    }

    public static void playEnd(final View view) {
        playEnd(view, (int) view.getX(), (int) view.getY(), 450);
    }

    public static void playEnd(final View view, int x, int y, int duration) {
        Animator a1 = ViewAnimationUtils.createCircularReveal(view, x, y, (float) Math.hypot(view.getWidth(), view.getHeight()), 0);
        a1.setInterpolator(new AccelerateInterpolator());
        a1.setDuration(duration);
        a1.start();
        a1.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator p1) {

            }

            @Override
            public void onAnimationEnd(Animator p1) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator p1) {

            }

            @Override
            public void onAnimationRepeat(Animator p1) {

            }
        });
    }

    public static void textChange(final TextView txv, final CharSequence tx) {
        AnimationSet as1 = new AnimationSet(true);
        AlphaAnimation aa1 = new AlphaAnimation(1, 0);
        aa1.setDuration(200);
        as1.addAnimation(aa1);
        txv.startAnimation(as1);
        as1.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation p1) {

            }

            @Override
            public void onAnimationEnd(Animation p1) {
                txv.setVisibility(View.GONE);
                txv.setText(tx);
                AnimationSet as = new AnimationSet(true);
                AlphaAnimation aa = new AlphaAnimation(0, 1);
                aa.setDuration(200);
                as.addAnimation(aa);
                txv.startAnimation(as);
                as.setAnimationListener(new Animation.AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation p1) {

                    }

                    @Override
                    public void onAnimationEnd(Animation p1) {
                        txv.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onAnimationRepeat(Animation p1) {

                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation p1) {

            }
        });

    }

    public static void fadeIn(View v, SimpleAnimateInterface animateInterface) {
        AnimationSet as = new AnimationSet(true);
        AlphaAnimation aa = new AlphaAnimation(0, 1);
        aa.setDuration(1200);
        as.addAnimation(aa);
        v.startAnimation(as);
        as.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation p1) {
            }

            @Override
            public void onAnimationEnd(Animation p1) {
                v.setVisibility(View.VISIBLE);
                animateInterface.onEnd();
            }

            @Override
            public void onAnimationRepeat(Animation p1) {

            }
        });
    }

    public static void fadeOut(View v, SimpleAnimateInterface animateInterface) {
        AnimationSet as = new AnimationSet(true);
        AlphaAnimation aa = new AlphaAnimation(1, 0);
        aa.setDuration(300);
        as.addAnimation(aa);
        v.startAnimation(as);
        as.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation p1) {
            }

            @Override
            public void onAnimationEnd(Animation p1) {
                v.setVisibility(View.GONE);
                animateInterface.onEnd();
            }

            @Override
            public void onAnimationRepeat(Animation p1) {

            }
        });
    }

    public interface SimpleAnimateInterface {
        void onEnd();
    }
}