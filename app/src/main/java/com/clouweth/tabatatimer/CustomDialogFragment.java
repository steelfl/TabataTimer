package com.clouweth.tabatatimer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class CustomDialogFragment extends DialogFragment {
    private Removable removable;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        removable = (Removable) context;
    }
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //final TextView textView = getArguments().get();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setTitle("dialog window")
                .setMessage("alert")
                .setNegativeButton("отмена", null)
                .setPositiveButton("ok", null)
                .create();
    }
}
