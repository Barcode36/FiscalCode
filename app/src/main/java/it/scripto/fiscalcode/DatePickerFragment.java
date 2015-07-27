package it.scripto.fiscalcode;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;


public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get the old date from the text view
        CharSequence oldDate = ((TextView) getActivity().findViewById(R.id.birthday_text_view)).getText();
        int year = Integer.parseInt(oldDate.subSequence(6, 10).toString());
        int month = Integer.parseInt(oldDate.subSequence(3, 5).toString()) - 1;  //
        int day = Integer.parseInt(oldDate.subSequence(0, 2).toString());

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Set the date chosen to text view
        ((TextView) getActivity().findViewById(R.id.birthday_text_view)).setText(String.format("%02d/%02d/%d", day, month + 1, year));
    }
}