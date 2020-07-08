package com.gladys.cybuverse.Helpers;


import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.gladys.cybuverse.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class ViewOverKeyBoard {

    private View mToggleView;
    private View mContentView;
    private View mParentLayout;
    private Activity mActivity;
    private ScrollView mScrollView;
    private boolean mIsPopupVisible;
    private boolean mIsKeyBoardVisible;
    private Animation mAnimationIn;
    private Animation mAnimationOut;
    private PopupWindow mPopupWindow;
    private View mRootViewOverKeyboard;
    private int mKeyBoardHeight = 270;


    public ViewOverKeyBoard(Activity activity, View contentView) {
        init(activity, contentView);
    }

    public ViewOverKeyBoard(Activity activity, int contentLayoutID) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(contentLayoutID, null);
        init(activity, contentView);
    }


    private void init(Activity activity, View contentView) {
        this.mActivity = activity;
        this.mContentView = contentView;
        this.mParentLayout = activity.getWindow().getDecorView().getRootView();
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        mRootViewOverKeyboard = inflater.inflate(R.layout.view_over_keyboard_root, null);
        mScrollView = mRootViewOverKeyboard.findViewById(R.id.scrollView);
        mScrollView.addView(mContentView);

        //TODO: set default animators

        // Inflate the custom layout/view
        mParentLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();

                mParentLayout.getWindowVisibleDisplayFrame(r); //contentInParent

                int heightDiff = mParentLayout.getHeight() - (r.bottom - r.top);
                if (heightDiff > 100) {
                    //enter your code here
                    if (mIsPopupVisible) {
                        keepKeyboard();
                        mIsPopupVisible = false;
                        mPopupWindow.dismiss();
                    }
                } else {
                    //enter code for dismiss
                }
            }
        });

        updateKeyboardVisibleState();
    }

    public Activity getActivity() {
        return mActivity;
    }

    public Context getContext() {
        return getActivity().getApplicationContext();
    }

    public void setToggleView(View view) {
        this.mToggleView = view;
    }

    public boolean isKeyBoardOpen() {
        return mIsKeyBoardVisible;
    }

    public void setAnimateViewIn(Animation animation) {
        this.mAnimationIn = animation;
    }

    public void setAnimateViewOut(Animation animation) {
        this.mAnimationOut = animation;
    }

    private void keepKeyboard() {
        getActivity().getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }


    public void dismiss() {
        if (mIsPopupVisible) {
            if (mAnimationOut != null) {
                mAnimationOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mPopupWindow.dismiss();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                mRootViewOverKeyboard.startAnimation(mAnimationOut);
            }
            mPopupWindow.dismiss();
        }
    }

    public void show() {

        if (mIsKeyBoardVisible && !mIsPopupVisible) {
            mRootViewOverKeyboard.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    v.removeOnLayoutChangeListener(this);
                    if (mAnimationIn != null) {
                        mAnimationIn.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        mRootViewOverKeyboard.startAnimation(mAnimationIn);
                    } else {
                        startCircularReveal(mRootViewOverKeyboard);
                    }
                }
            });


            mPopupWindow = new PopupWindow(mRootViewOverKeyboard,
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
            );

            setSizeForViewOverKeyBoard();

            mPopupWindow.showAtLocation(mParentLayout, Gravity.CENTER, 0, 0);
        }

        mIsPopupVisible = true;
    }

    private void setSizeForViewOverKeyBoard() {
        mScrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                mParentLayout.getWindowVisibleDisplayFrame(r);

                int screenHeight = getUsableScreenHeight();
                int heightDifference = screenHeight
                        - (r.bottom - r.top);
                int resourceId = getActivity().getResources()
                        .getIdentifier("status_bar_height",
                                "dimen", "android");
                if (resourceId > 0) {
                    heightDifference -= getActivity().getResources()
                            .getDimensionPixelSize(resourceId);
                }
                if (heightDifference > 100) {
                    int keyBoardHeight = heightDifference;
                    mKeyBoardHeight = keyBoardHeight;

                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mScrollView.getLayoutParams();
                    params.height = keyBoardHeight;
                    mScrollView.setLayoutParams(params);
                }
            }
        });
    }

    private void updateKeyboardVisibleState() {

        mParentLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                mParentLayout.getWindowVisibleDisplayFrame(r);
                int screenHeight = mParentLayout.getHeight();

                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;

                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                    // keyboard is opened
                    mIsKeyBoardVisible = true;
                } else {
                    // keyboard is closed
                    mIsKeyBoardVisible = false;
                }
            }
        });
    }

    private int getUsableScreenHeight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();

            WindowManager windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(metrics);

            return metrics.heightPixels;

        } else {
            return mParentLayout.getHeight();
        }
    }

    private void startCircularReveal(View view) {

        if (mToggleView != null) {
            int cx = (mToggleView.getLeft() + mToggleView.getRight()) / 2;
            int cy = (mToggleView.getTop() + mToggleView.getBottom()) / 2;

            int finalRadius = Math.max(cy, view.getHeight() - cy);
            Animator anim = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
            }
            anim.addListener(new Animator.AnimatorListener() {
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
            });
            anim.setDuration(350);
            view.setVisibility(View.VISIBLE);
            anim.start();
        }
//        view.setBackgroundColor(Color.parseColor("#6FA6FF"));
    }

    public void showPopup() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_new, null);

        final PopupWindow popup = new PopupWindow(popupView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        int[] location = new int[2];
        mToggleView.getLocationInWindow(location);


        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.bottomMargin = metrics.heightPixels - location[1];  // left, top, right, bottom
        mScrollView.setLayoutParams(params);

        // Get a reference for the custom view close button
        Button belowCloseButton = (Button) popupView.findViewById(R.id.popup_new_below_close_button);
        // Get a reference for the custom view close button
        Button closeButton = (Button) popupView.findViewById(R.id.popup_new_close_button);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the popup window
                mIsPopupVisible = false;
                popup.dismiss();
            }
        });

        belowCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the popup window
                mIsPopupVisible = false;
                popup.dismiss();
            }
        });

        popup.showAtLocation(mParentLayout, Gravity.NO_GRAVITY, 0, 0);//location[1]-popupHeight);
    }

    public boolean isShown() {
        return mIsPopupVisible;
    }
}
