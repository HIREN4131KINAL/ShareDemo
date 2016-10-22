package com.example.guest999.firebasenotification.ProgressDialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import com.example.guest999.firebasenotification.R;


/**
 * Created by Harshad on 13-10-2016 at 09:49 AM.
 */

public class CustomDialog extends DialogFragment {
    DotLoader textDotLoader;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.custom_dialog_box, container, false);

        textDotLoader = (DotLoader) v.findViewById(R.id.text_dot_loader);


        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //dotLoader.resetColors();
        textDotLoader.resetColors();
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        LinearLayout root = new LinearLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        dialog.getWindow().setBackgroundDrawable(null);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        return dialog;
    }

    private static class DotIncrementRunnable implements Runnable {
        private int mNumberOfDots;
        private DotLoader mDotLoader;

        private DotIncrementRunnable(int numberOfDots, DotLoader dotLoader) {
            mNumberOfDots = numberOfDots;
            mDotLoader = dotLoader;
        }

        @Override
        public void run() {
            mDotLoader.setNumberOfDots(mNumberOfDots);
        }
    }

}
