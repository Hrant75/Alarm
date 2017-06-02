package com.example.hrant.alarmapp;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, View.OnClickListener{
    Button buttonSetAlarm, buttonCancelAlarm;
    protected Calendar calendar;
    private int year, month, day, hour, minute;
    int radio_group_selection;
    boolean isChecked_intervalCheckBox;
    int interval;

    RadioButton radioButtonVibrate, radioButtonNotify, radioButtonSound;
    CheckBox checkbox_interval;
    EditText editText_interval;

    Intent intent;
    PendingIntent pendingIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        interval = 10000;

        setDateAndTimeTexts();

        checkbox_interval = (CheckBox)findViewById(R.id.checkbox_interval);
        checkbox_interval.setOnClickListener(this);
        radioButtonVibrate = (RadioButton)findViewById(R.id.radioButton_vibrate);
        radioButtonNotify = (RadioButton)findViewById(R.id.radioButton_notify);
        radioButtonSound = (RadioButton)findViewById(R.id.radioButton_playSound);
        radioButtonVibrate.setOnClickListener(this);
        radioButtonNotify.setOnClickListener(this);
        if(radioButtonSound.isChecked()) radio_group_selection = 3;
        else if(radioButtonNotify.isChecked()) radio_group_selection = 2;
        else radio_group_selection = 1;
        radioButtonSound.setOnClickListener(this);
        editText_interval = (EditText)findViewById(R.id.editText_interval);
        editText_interval.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().trim().length()==0){interval = 10000;}
                else
                interval =  Integer.parseInt(s.toString()) * 1000;
            }
        });
        buttonSetAlarm = (Button)findViewById(R.id.set_alarm_button);
        buttonCancelAlarm = (Button) findViewById(R.id.button_cancel);
        buttonSetAlarm.setOnClickListener(this);
        buttonCancelAlarm.setOnClickListener(this);
    }

    public void datePicker(View view){
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.show(getSupportFragmentManager(), "date");
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        setDateAndTimeTexts();
    }

    public void timePicker(View view){
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.show(getSupportFragmentManager(), "time");
    }

    public void onTimeSet(TimePicker view, int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
        setDateAndTimeTexts();
    }

    public void setDateAndTimeTexts(){
        Calendar cal = new GregorianCalendar(year, month, day);
        final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
        ((TextView) findViewById(R.id.showDate)).setText(dateFormat.format(cal.getTime()));
        ((TextView) findViewById(R.id.showTime)).setText(hour + ":" + minute);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.radioButton_vibrate:
                radio_group_selection = 1;
                break;
            case R.id.radioButton_notify:
                radio_group_selection = 2;
                break;
            case R.id.radioButton_playSound:
                radio_group_selection = 3;
                break;
            case R.id.checkbox_interval:
                if(checkbox_interval.isChecked()){
                    isChecked_intervalCheckBox = true;
                    editText_interval.setVisibility(View.VISIBLE);
                }
                else {
                    isChecked_intervalCheckBox = false;
                    editText_interval.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.set_alarm_button:
                setAlarm();
                break;
            case R.id.button_cancel:
                cancelAlarm();
                break;
        }
    }

    void setAlarm(){
        intent = null;
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        switch (radio_group_selection){
            case 3:
                intent = new Intent(this, AlarmReceiverMediaPlayer.class);
                break;
            case 2:
                intent = new Intent(this, AlarmReceiverNotification.class);
                break;
            case 1:
                intent = new Intent(this, AlarmReceiverVibrate.class);
                break;
            default:
                return;
        }
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        if(!checkbox_interval.isChecked()) {
            manager.set(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent);
            Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
        } else {
            manager.setRepeating(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    interval,
                    pendingIntent);
            Toast.makeText(this, "Repeating Alarm Set", Toast.LENGTH_SHORT).show();
        }
    }

    void cancelAlarm(){
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        Toast.makeText(this, "Alarm Canceled", Toast.LENGTH_LONG).show();
    }


}
