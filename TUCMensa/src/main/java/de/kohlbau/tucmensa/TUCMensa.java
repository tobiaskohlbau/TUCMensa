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
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class TUCMensa extends FragmentActivity {
    private static int mYear;
    private static int mMonth;
    private static int mDay;
    private static int mMensa;
    private static final String TAG = "TUCMensa";

    MealAdapter mMealAdapter;

    GestureDetector gDetector;

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId()) {
            case R.id.reich:
                if (checked)
                    mMensa = XMLLoader.MENSA_REICH;
                    refreshMeal(mYear, mMonth, mDay, XMLLoader.MENSA_REICH);
                    break;
            case R.id.strana:
                if (checked)
                    mMensa = XMLLoader.MENSA_STRANA;
                    refreshMeal(mYear, mMonth, mDay, XMLLoader.MENSA_STRANA);
                    break;
        }
    }

    public class DatePickerFragment extends DialogFragment
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
            refreshMeal(year, month, day, mMensa);
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
        public void onReceive(Context context, Intent intent) {;
            ArrayList<Meal> entries = intent.getParcelableArrayListExtra("entries");
            mMealAdapter.setData(entries);
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

        ListView listView = (ListView) findViewById(R.id.listMeals);

        mMealAdapter = new MealAdapter(getBaseContext());
        listView.setAdapter(mMealAdapter);


        findViewById(R.id.reich).performClick();

        GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapConfirmed(MotionEvent event) {
                Log.d(TAG, "+ onSingleTapConfirmed(event:" + event + ")");
                Log.d(TAG, "- onSingleTapConfirmed()");
                return true;
            }
        };

        gDetector = new GestureDetector(getApplicationContext(), gestureListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMealReceiver,
                new IntentFilter("de.kohlbau.tucmensa.XMLParsed"));

    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMealReceiver);
        super.onPause();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        super.onTouchEvent(motionEvent);
        gDetector.onTouchEvent(motionEvent);
        return true;
    }
}