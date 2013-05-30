package de.kohlbau.tucmensa;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class TUCMensa extends FragmentActivity {
    private static int mYear;
    private static int mMonth;
    private static int mDay;

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId()) {
            case R.id.reich:
                if (checked)
                    refreshMeal(mYear, mMonth, mDay, XMLLoader.MENSA_REICH);
                    break;
            case R.id.strana:
                if (checked)
                    refreshMeal(mYear, mMonth, mDay, XMLLoader.MENSA_STRANA);
                    break;
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            mYear = year;
            mMonth = month;
            mDay = day;
        }
    }

    public void refreshMeal(int year, int month, int day, int mensa) {
        XMLLoader xmlLoader = new XMLLoader(getBaseContext(), year, month, day, mensa);
        xmlLoader.execute();
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private BroadcastReceiver mMealReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LinearLayout ll = (LinearLayout) findViewById(R.id.ll);
            ll.removeAllViews();
            ArrayList<Meal> entries = intent.getParcelableArrayListExtra("entries");
            if (entries != null) {
                for (Meal entry : entries) {
                    LinearLayout linearLayout = new LinearLayout(context);
                    TextView textView = new TextView(context);
                    ImageView imageView = new ImageView(context);

                    linearLayout.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    textView.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    imageView.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));


                    linearLayout.setOrientation(LinearLayout.VERTICAL);

                    textView.setText("Category: " + entry.getCategory() + "\n" +
                            "Description: " + entry.getDescription() + "\n" +
                            "Student: " + (entry.getPrice()[2] > 0 ? String.format("%1$,.2f", entry.getPrice()[0]) + " \u20ac" : "Daily prices") + "\n" +
                            "Coworker: " + (entry.getPrice()[2] > 0 ? String.format("%1$,.2f", entry.getPrice()[1]) + " \u20ac" : "Daily prices") + "\n" +
                            "Guest: " + (entry.getPrice()[2] > 0 ? String.format("%1$,.2f", entry.getPrice()[2]) + " \u20ac" : "Daily prices") + "\n" +
                            "Rating: " + entry.getRating() + "\n"
                    );
                    imageView.setImageBitmap(entry.getImage());

                    linearLayout.addView(textView);
                    linearLayout.addView(imageView);
                    ll.addView(linearLayout);
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tucmensa);
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        findViewById(R.id.reich).performClick();

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMealReceiver,
                new IntentFilter("de.kohlbau.tucmensa.XMLParsed"));

    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMealReceiver);;
        super.onPause();
    }
}