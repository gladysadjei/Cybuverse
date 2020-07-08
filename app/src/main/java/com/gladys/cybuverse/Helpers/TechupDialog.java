package com.gladys.cybuverse.Helpers;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TechupDialog {

    private Activity activity;

    private Integer
            btnPositiveID,
            btnNegativeID,
            btnNeutralID,
            backGroundID,
            titleTextViewID,
            messageTextViewID;

    private String
            btnPositiveText,
            btnNegativeText,
            btnNeutralText,
            titleText,
            messageText;

    private View.OnClickListener
            btnPositiveOnClickListener,
            contentViewOnClickListener,
            backGroundOnClickListener,
            btnNegativeOnClickListener,
            btnNeutralOnClickListener,
            titleOnClickListener,
            messageOnClickListener;

    private Dialog.OnCancelListener onCancelListener;
    private Dialog.OnDismissListener onDismissListener;
    private Dialog.OnShowListener onShowListener;
    private Dialog.OnKeyListener onKeyListener;
    private Dialog.OnMultiChoiceClickListener onMultiChoiceClickListener;

    private boolean cancelable, cancelOnTouchOutside, isHidden;

    private Dialog dialog;
    private View contentView;
    private OnSetupViewListener onSetupViewListener;


    public TechupDialog(Activity activity) {
        this.activity = activity;
        this.isHidden = false;
        this.cancelable = true;
        this.cancelOnTouchOutside = true;
        this.dialog = new Dialog(this.activity);
    }

    public TechupDialog(Activity activity, int dialogResource) {
        this.activity = activity;
        this.isHidden = false;
        this.cancelable = true;
        this.cancelOnTouchOutside = true;
        this.dialog = new Dialog(this.activity);
        this.setContentViewResource(dialogResource);
    }

    public TechupDialog(Activity activity, View dialogView) {
        this.activity = activity;
        this.isHidden = false;
        this.cancelable = true;
        this.cancelOnTouchOutside = true;
        this.dialog = new Dialog(this.activity);
        this.setContentView(dialogView);
    }

    public void setContentViewResource(int dialogResource) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        this.setContentView(inflater.inflate(dialogResource, null));
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Integer getBtnPositiveID() {
        return btnPositiveID;
    }

    public void setBtnPositiveID(Integer btnPositiveID) {
        this.btnPositiveID = btnPositiveID;
    }

    public Integer getBtnNegativeID() {
        return btnNegativeID;
    }

    public void setBtnNegativeID(Integer btnNegativeID) {
        this.btnNegativeID = btnNegativeID;
    }

    public Integer getBtnNeutralID() {
        return btnNeutralID;
    }

    public void setBtnNeutralID(Integer btnNeutralID) {
        this.btnNeutralID = btnNeutralID;
    }

    public Integer getBackGroundID() {
        return backGroundID;
    }

    public void setBackGroundID(Integer backGroundID) {
        this.backGroundID = backGroundID;
    }

    public Integer getTitleTextViewID() {
        return titleTextViewID;
    }

    public void setTitleTextViewID(Integer titleTextViewID) {
        this.titleTextViewID = titleTextViewID;
    }

    public Integer getMessageTextViewID() {
        return messageTextViewID;
    }

    public void setMessageTextViewID(Integer messageTextViewID) {
        this.messageTextViewID = messageTextViewID;
    }

    public String getBtnPositiveText() {
        return btnPositiveText;
    }

    public void setBtnPositiveText(String btnPositiveText) {
        this.btnPositiveText = btnPositiveText;
    }

    public String getBtnNegativeText() {
        return btnNegativeText;
    }

    public void setBtnNegativeText(String btnNegativeText) {
        this.btnNegativeText = btnNegativeText;
    }

    public String getBtnNeutralText() {
        return btnNeutralText;
    }

    public void setBtnNeutralText(String btnNeutralText) {
        this.btnNeutralText = btnNeutralText;
    }

    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public View.OnClickListener getBtnPositiveOnClickListener() {
        return btnPositiveOnClickListener;
    }

    public void setBtnPositiveOnClickListener(View.OnClickListener btnPositiveOnClickListener) {
        this.btnPositiveOnClickListener = btnPositiveOnClickListener;
    }

    public View.OnClickListener getContentViewOnClickListener() {
        return contentViewOnClickListener;
    }

    public void setContentViewOnClickListener(View.OnClickListener contentViewOnClickListener) {
        this.contentViewOnClickListener = contentViewOnClickListener;
    }

    public View.OnClickListener getBackGroundOnClickListener() {
        return backGroundOnClickListener;
    }

    public void setBackGroundOnClickListener(View.OnClickListener backGroundOnClickListener) {
        this.backGroundOnClickListener = backGroundOnClickListener;
    }

    public View.OnClickListener getBtnNegativeOnClickListener() {
        return btnNegativeOnClickListener;
    }

    public void setBtnNegativeOnClickListener(View.OnClickListener btnNegativeOnClickListener) {
        this.btnNegativeOnClickListener = btnNegativeOnClickListener;
    }

    public View.OnClickListener getBtnNeutralOnClickListener() {
        return btnNeutralOnClickListener;
    }

    public void setBtnNeutralOnClickListener(View.OnClickListener btnNeutralOnClickListener) {
        this.btnNeutralOnClickListener = btnNeutralOnClickListener;
    }

    public View.OnClickListener getTitleOnClickListener() {
        return titleOnClickListener;
    }

    public void setTitleOnClickListener(View.OnClickListener titleOnClickListener) {
        this.titleOnClickListener = titleOnClickListener;
    }

    public View.OnClickListener getMessageOnClickListener() {
        return messageOnClickListener;
    }

    public void setMessageOnClickListener(View.OnClickListener messageOnClickListener) {
        this.messageOnClickListener = messageOnClickListener;
    }

    public View getContentView() {
        return contentView;
    }

    public void setContentView(View contentView) {
        this.contentView = contentView;
    }

    public OnSetupViewListener getOnSetupViewListener() {
        return onSetupViewListener;
    }

    public void setOnSetupViewListener(OnSetupViewListener onSetupViewListener) {
        this.onSetupViewListener = onSetupViewListener;
    }

    public View getBackGround() {
        return getContentView().findViewById(backGroundID);
    }

    public View getBtnPositive() {
        return getContentView().findViewById(btnPositiveID);
    }

    public View getBtnNegative() {
        return getContentView().findViewById(btnNegativeID);
    }

    public View getBtnNeutral() {
        return getContentView().findViewById(btnNeutralID);
    }

    public View getTitleTextView() {
        return getContentView().findViewById(titleTextViewID);
    }

    public View getMessageTextView() {
        return getContentView().findViewById(messageTextViewID);
    }

    public Dialog.OnCancelListener getOnCancelListener() {
        return onCancelListener;
    }

    public void setOnCancelListener(Dialog.OnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
    }

    public Dialog.OnDismissListener getOnDismissListener() {
        return onDismissListener;
    }

    public void setOnDismissListener(Dialog.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    public Dialog.OnShowListener getOnShowListener() {
        return onShowListener;
    }

    public void setOnShowListener(Dialog.OnShowListener onShowListener) {
        this.onShowListener = onShowListener;
    }

    public Dialog.OnKeyListener getOnKeyListener() {
        return onKeyListener;
    }

    public void setOnKeyListener(Dialog.OnKeyListener onKeyListener) {
        this.onKeyListener = onKeyListener;
    }

    public Dialog.OnMultiChoiceClickListener getOnMultiChoiceClickListener() {
        return onMultiChoiceClickListener;
    }

    public void setOnMultiChoiceClickListener(Dialog.OnMultiChoiceClickListener onMultiChoiceClickListener) {
        this.onMultiChoiceClickListener = onMultiChoiceClickListener;
    }

    public boolean isCancelable() {
        return cancelable;
    }

    public void setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
    }

    public boolean isCancelOnTouchOutside() {
        return cancelOnTouchOutside;
    }

    public void setCancelOnTouchOutside(boolean cancelOnTouchOutside) {
        this.cancelOnTouchOutside = cancelOnTouchOutside;
    }

    private boolean isHidden() {
        return this.isHidden;
    }

    private void setHidden(boolean isHidden) {
        this.isHidden = isHidden;
    }

    private void prepareDialog() {

        if (dialog == null)
            dialog = new Dialog(getActivity());

        if (getContentView() != null && getContentViewOnClickListener() != null)
            getContentView().setOnClickListener(getContentViewOnClickListener());

        if (getMessageTextView() != null) {
            if (getMessageText() != null) {
                getMessageTextView().setOnClickListener(getMessageOnClickListener());
                ((TextView) getMessageTextView()).setText(getMessageText());
            } else {
                getMessageTextView().setVisibility(View.GONE);
            }
        }

        if (getTitleTextView() != null) {
            if (getTitleText() != null) {
                getTitleTextView().setOnClickListener(getTitleOnClickListener());
                ((TextView) getTitleTextView()).setText(getTitleText());
            } else {
                getTitleTextView().setVisibility(View.GONE);
            }
        }

        if (getBackGround() != null)
            getBackGround().setOnClickListener(getBackGroundOnClickListener());

        if (getBtnPositive() != null) {
            ((Button) getBtnPositive()).setText(getBtnPositiveText());
            getBtnPositive().setOnClickListener(getBtnPositiveOnClickListener());
        }

        if (getBtnNegative() != null) {
            ((Button) getBtnNegative()).setText(getBtnNegativeText());
            getBtnNegative().setOnClickListener(getBtnNegativeOnClickListener());
        }

        if (getBtnNeutral() != null) {
            ((Button) getBtnNeutral()).setText(getBtnNeutralText());
            getBtnNeutral().setOnClickListener(getBtnNeutralOnClickListener());
        }

        if (getOnSetupViewListener() != null) {
            getOnSetupViewListener().onSetup(getContentView());
        }

        dialog.setContentView(getContentView());
        dialog.setCancelable(isCancelable());
        dialog.setOnDismissListener(getOnDismissListener());
        dialog.setOnCancelListener(getOnCancelListener());
        dialog.setOnKeyListener(getOnKeyListener());
        dialog.setOnShowListener(getOnShowListener());
        if (isCancelable()) dialog.setCanceledOnTouchOutside(isCancelOnTouchOutside());
    }

    public boolean isShowing() {
        return dialog != null && dialog.isShowing();
    }

    private Dialog getDialog() {
        return dialog;
    }

    public void show() {
        prepareDialog();
        if (isHidden()) setHidden(false);
        getDialog().show();
    }

    public void hide() {
        setHidden(true);
        getDialog().hide();
    }

    public void dismiss() {
        getDialog().dismiss();
    }

    public interface OnSetupViewListener {
        void onSetup(View dialogView);
    }

}
