package com.andronomy.moneymanagement.Activities;

import android.app.Activity;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.andronomy.moneymanagement.Adapters.DatabaseAdapter;
import com.andronomy.moneymanagement.Adapters.SpinnerAccountAdapter;
import com.andronomy.moneymanagement.Adapters.SpinnerAdapter;
import com.andronomy.moneymanagement.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.rey.material.app.DatePickerDialog;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.widget.EditText;
import com.rey.material.widget.Spinner;
import com.rey.material.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class AddTransferActivity extends ActionBarActivity implements View.OnClickListener {
    private Activity mActivity = this;
    private DatabaseAdapter dbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbAdapter = new DatabaseAdapter(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transfer);

        TextView datePicker = (TextView) findViewById(R.id.date_picker_field);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Spinner spinner_acc = (Spinner) findViewById(R.id.acc_from_field);
        SpinnerAccountAdapter adapter_acc = new SpinnerAccountAdapter(this, R.layout.spinner_layout, R.id.spinnerText, dbAdapter.getAccsData());
        adapter_acc.setDropDownViewResource(R.layout.spinner_layout);
        spinner_acc.setAdapter(adapter_acc);

        Spinner spinner_to = (Spinner) findViewById(R.id.acc_to_field);
        SpinnerAccountAdapter adapter_to = new SpinnerAccountAdapter(this, R.layout.spinner_layout, R.id.spinnerText, dbAdapter.getAccsData());
        adapter_to.setDropDownViewResource(R.layout.spinner_layout);
        spinner_to.setAdapter(adapter_to);

        long date = System.currentTimeMillis();
        DateFormat df = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.FULL, SimpleDateFormat.DEFAULT, new Locale(dbAdapter.getActiveLang()));
        datePicker.setText(df.format(date));
        datePicker.setOnClickListener(this);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_add_transfer, menu);
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

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }

    public void addTransfer(View view) throws ParseException {
        Spinner acc_from = (Spinner) findViewById(R.id.acc_from_field);
        Spinner acc_to = (Spinner) findViewById(R.id.acc_to_field);
        EditText amount = (EditText) findViewById(R.id.amount_field);
        TextView date = (TextView) findViewById(R.id.date_picker_field);
        String dateStr = date.getText().toString();

        DateFormat format = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.FULL, SimpleDateFormat.DEFAULT, new Locale(dbAdapter.getActiveLang()));
        Date newDate = format.parse(dateStr);
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateStr = format.format(newDate);
        ImageView accFromId = (ImageView) acc_from.getSelectedView().findViewById(R.id.spinnerImage);
        ImageView accToId = (ImageView) acc_to.getSelectedView().findViewById(R.id.spinnerImage);

        long id = dbAdapter.insertTransferData(accFromId.getContentDescription() + "", accToId.getContentDescription() + "", amount.getText().toString(), dateStr);
        if (id < 1) {
            Toast.makeText(this, getString(R.string.warn_failed_input), Toast.LENGTH_SHORT).show();
        } else {
            NavUtils.navigateUpFromSameTask(this);
            Toast.makeText(this, getString(R.string.warn_success_input), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        Dialog.Builder builder = null;
        switch (v.getId()) {
            case R.id.date_picker_field:
                builder = new DatePickerDialog.Builder(){
                    @Override
                    public void onPositiveActionClicked(DialogFragment fragment) {
                        TextView datePicker = (TextView) findViewById(R.id.date_picker_field);
                        DatePickerDialog dialog = (DatePickerDialog) fragment.getDialog();
                        String date = dialog.getFormattedDate(SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.FULL, SimpleDateFormat.DEFAULT, new Locale(dbAdapter.getActiveLang())));//new SimpleDateFormat("EEEE, dd MMMM yyyy. HH:mm:ss"));//DateFormat.getDateTimeInstance());
                        datePicker.setText(date);
                        Toast.makeText(mActivity, getString(R.string.selected_date_str) + date, Toast.LENGTH_SHORT).show();
                        super.onPositiveActionClicked(fragment);
                    }

                    @Override
                    public void onNegativeActionClicked(DialogFragment fragment) {
                        Toast.makeText(mActivity, getString(R.string.cancelled_str) , Toast.LENGTH_SHORT).show();
                        super.onNegativeActionClicked(fragment);
                    }
                };

                builder.positiveAction("OK")
                        .negativeAction(getString(R.string.cancel_str));
                break;
        }

        Locale.setDefault(new Locale(dbAdapter.getActiveLang()));
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getSupportFragmentManager(), null);
    }
}
