package steppschuh.androdiweartest;

import android.content.res.Resources;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;

public class UiHelper {

	public static float DEFAULT_FADE_STEP = 0.04f;
	public static int DEFAULT_DURATION = 300;

	public static int dpToPx(int dp) {
		return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
	}

	public static int pxToDp(int px) {
		return (int) (px / Resources.getSystem().getDisplayMetrics().density);
	}

	public static void pressAnimation(final View v) {
		AnimationSet s = new AnimationSet(false);

		float newSize = 0.95f;

		ScaleAnimation scaleUpAnimation = new ScaleAnimation(1f, newSize, 1f, newSize, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		scaleUpAnimation.setDuration(70);
		scaleUpAnimation.setFillAfter(true);

		ScaleAnimation scaleBackAnimation = new ScaleAnimation(newSize, 1f, newSize, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		scaleBackAnimation.setDuration(30);
		scaleBackAnimation.setFillAfter(true);
		scaleBackAnimation.setStartOffset(70);

		s.addAnimation(scaleUpAnimation);
		s.addAnimation(scaleBackAnimation);
		v.startAnimation(s);
	}

	public static void fadeOutRotating(final View v, int duration) {
		AnimationSet s = new AnimationSet(false);
		s.addAnimation(getRotationAnimation(duration));
		s.addAnimation(getFadeOutAnimation(v, duration));
		v.startAnimation(s);
	}

	public static void fadeInRotating(final View v, int duration) {
		AnimationSet s = new AnimationSet(false);
		s.addAnimation(getRotationRevertAnimation(duration));
		s.addAnimation(getFadeInAnimation(v, duration));
		v.startAnimation(s);
	}

	public static Animation getRotationRevertAnimation(int duration) {
		Animation animation = new RotateAnimation(360.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setDuration(duration);
		animation.setFillAfter(true);
		return animation;
	}

	public static Animation getRotationAnimation(int duration) {
		Animation animation = new RotateAnimation(0f, 270.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setDuration(duration);
		animation.setFillAfter(true);
		return animation;
	}

	public static void fadeIn(final View v, int duration) {
		v.startAnimation(getFadeInAnimation(v, duration));
	}

	public static void fadeOut(final View v, int duration) {
		if (v.getVisibility() == View.GONE) {
			// view is invisible anyway
			return;
		}
		v.startAnimation(getFadeOutAnimation(v, duration));
	}

	private static Animation getFadeOutAnimation(final View v, int duration) {
		AlphaAnimation alphaAnimation = new AlphaAnimation(v.getAlpha(), 0f);
		alphaAnimation.setDuration(duration);
		alphaAnimation.setFillAfter(true);
		alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				v.setVisibility(View.GONE);
				v.setAlpha(0f);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
		return alphaAnimation;
	}

	private static Animation getFadeInAnimation(final View v, int duration) {
		AlphaAnimation alphaAnimation = new AlphaAnimation(v.getAlpha(), 1.0f);
		alphaAnimation.setDuration(duration);
		alphaAnimation.setFillAfter(true);
		alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				v.setVisibility(View.VISIBLE);
				v.setAlpha(1f);
			}

			@Override
			public void onAnimationEnd(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
		return alphaAnimation;
	}

}
