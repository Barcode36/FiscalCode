package it.scripto.fiscalcode;

import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.nispok.snackbar.Snackbar;

import net.i2p.android.ext.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import it.scripto.models.FiscalCodeCalculator;
import it.scripto.util.BaseActivity;


public class MainActivity extends BaseActivity {

    private EditText nameEditText;
    private EditText surnameEditText;
    private TextView birthdayTextView;
    private TextView resultTextView;
    private AutoCompleteTextView birthplaceTextView;
    private FiscalCodeCalculator.Gender gender = FiscalCodeCalculator.Gender.FEMALE;

    private DatabaseAdapter database;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get Toolbar and set as ActionBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        // Set title empty
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(null);
        }

        // Get name edit text
        nameEditText = (EditText) findViewById(R.id.name_edit_text);

        // Get surname edit text
        surnameEditText = (EditText) findViewById(R.id.surname_edit_text);

        // Get birthday text view
        birthdayTextView = (TextView) findViewById(R.id.birthday_text_view);
        // Get current date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Set the current date to birthday text view
        birthdayTextView.setText(String.format("%02d/%02d/%d", day, month + 1, year));
        // Set the onClickListener in order to open the dataPicker
        birthdayTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });

        // Get result text view
        resultTextView = (TextView) findViewById(R.id.result_text_view);

        // Get query button
        FloatingActionButton queryButton = (FloatingActionButton) findViewById(R.id.query_button);
        // Set onClickListener
        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard();

                // Get surname, name and birthplace
                String surname = surnameEditText.getText().toString();
                String name = nameEditText.getText().toString();
                String birthplace = birthplaceTextView.getText().toString();

                // Get birthday
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date birthDay = new Date();
                try {
                    birthDay = simpleDateFormat.parse(birthdayTextView.getText().toString());
                } catch (ParseException e) {
                    Log.e(TAG, e.toString());
                }

                // If all data are inserted
                if (!name.equals("") & !surname.equals("") & !birthplace.equals("")) {
                    FiscalCodeCalculator fiscalCodeCalculator = new FiscalCodeCalculator(surname, name, birthDay, gender, database.getCode(birthplace));
                    // Calculate the fiscal code
                    String fiscalCode = fiscalCodeCalculator.calculate();
                    // Set the result
                    resultTextView.setText(fiscalCode);
                    // and show
                    resultTextView.setVisibility(View.VISIBLE);
                } else {
                    // Show message error
                    showSnackBarWithMessage(getString(R.string.input_error));
                }

            }
        });

        birthplaceTextView = (AutoCompleteTextView) findViewById(R.id.birthplaceTextView);
        birthplaceTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                hideKeyboard();
            }
        });

        database = new DatabaseAdapter(this);
        database.open();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, database.getAllCitiesArray());

        birthplaceTextView.setAdapter(adapter);
    }

    /**
     * Method that allow to show a snack bar with a custom message
     * @param message to show
     */
    private void showSnackBarWithMessage(String message) {
        Snackbar.with(getApplicationContext())
                .text(message)
                .show(this);
    }

    /**
     *
     * @param v
     */
    private void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.close();
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.male_choice:
                if (checked) {
                    gender = FiscalCodeCalculator.Gender.MALE;
                }
                break;
            case R.id.female_choice:
                if (checked) {
                    gender = FiscalCodeCalculator.Gender.FEMALE;
                }
                break;
            default:
                break;
        }
    }


    /**
     * Hide keyboard
     */
    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
