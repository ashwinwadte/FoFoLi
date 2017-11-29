package com.waftinc.fofoli.posts;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

import com.waftinc.fofoli.R;

public class DistributionConfirmationDialogFragment extends DialogFragment {
    EditText etCount;

    /**
     * Public static constructor that creates fragment and
     * passes a bundle with data into it when adapter is created
     */
    public static DistributionConfirmationDialogFragment newInstance() {
        return new DistributionConfirmationDialogFragment();
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.string_confirm)
                .setMessage(getActivity().getString(R.string.string_ready_to_distribute))
                .setPositiveButton(getActivity().getString(R.string.string_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //setDistributedTrue(viewHolder, position);
                    }
                })
                .setNegativeButton(getActivity().getString(R.string.string_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }
}
