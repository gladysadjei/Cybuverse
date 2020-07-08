package com.gladys.cybuverse.Helpers;


import android.app.Activity;
import android.view.View;

import com.gladys.cybuverse.R;

public class ProgressTechupDialog extends TechupDialog {

    public ProgressTechupDialog(Activity activity) {
        super(activity);
        setContentViewResource(R.layout.dialog_progress);
        setBackGroundID(R.id.background);
        setTitleTextViewID(R.id.title);
        setMessageTextViewID(R.id.message);
        setBtnPositiveID(R.id.btn_positive);
        setBtnNegativeID(R.id.btn_negative);
        setBtnNeutralID(R.id.btn_neutral);
    }

    public void setPositiveButton(String text, View.OnClickListener onClickListener) {
        setBtnPositiveText(text);
        setBtnPositiveOnClickListener(onClickListener);
        getBtnPositive().setVisibility(View.VISIBLE);
        getContentView().findViewById(R.id.buttons).setVisibility(View.VISIBLE);
    }

    public void setNegativeButton(String text, View.OnClickListener onClickListener) {
        setBtnNegativeText(text);
        setBtnNegativeOnClickListener(onClickListener);
        getBtnNegative().setVisibility(View.VISIBLE);
        getContentView().findViewById(R.id.buttons).setVisibility(View.VISIBLE);
    }

    public void setNeutralButton(String text, View.OnClickListener onClickListener) {
        setBtnNeutralText(text);
        setBtnNeutralOnClickListener(onClickListener);
        getBtnNeutral().setVisibility(View.VISIBLE);
        getContentView().findViewById(R.id.buttons).setVisibility(View.VISIBLE);
    }

}
