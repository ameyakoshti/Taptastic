package com.csci580.taptastic;

import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.csci580.taptastic.adapter.DatePickerFragment;
import com.csci580.taptastic.adapter.TimePickerFragment;

public class NewAppointment extends android.app.Fragment implements AsyncResponse {
	public NewAppointment() {
	}

	Button b1;
	Button b2;
	Button b3;
	View rootView;
	String dates = "";
	static int year = 0;
	static int monthOfYear = 0;
	static int dayOfMonth = 0;
	static int hourOfDay = 0;
	static int minute = 0;
	static String type;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.new_appointment, container, false);
		b1 = (Button) rootView.findViewById(R.id.button1);
		Calendar calendar = Calendar.getInstance();
		b1.setText((calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.YEAR));
		b2 = (Button) rootView.findViewById(R.id.button2);
		b3 = (Button) rootView.findViewById(R.id.button3);
		type = "g";

		if (calendar.get(Calendar.AM) == 1)
			b2.setText(calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE) + " PM");
		else
			b2.setText(calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE) + " AM");

		b1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showDatePicker();
			}
		});
		b2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showTimePicker();
			}
		});

		b3.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText query = (EditText) rootView.findViewById(R.id.editText1);
				if (year == 0) {
					Toast.makeText(getActivity(), "Please select Date", Toast.LENGTH_LONG).show();
				} else if (hourOfDay == 0) {
					Toast.makeText(getActivity(), "Please select Time", Toast.LENGTH_LONG).show();
				} else if (query.getText().toString().isEmpty()) {
					Toast.makeText(getActivity(), "Please enter a comment", Toast.LENGTH_LONG).show();
				} else {
					ServletCalls Appointments = new ServletCalls();
					Appointments.delegate = NewAppointment.this;
					String datetime = String.valueOf(year) + "/" + String.valueOf(monthOfYear) + "/" + String.valueOf(dayOfMonth) + "+" + String.valueOf(hourOfDay) + ":" + String.valueOf(minute)
							+ ":00";
					Appointments.execute("6", "9790886604", "csci588", datetime, query.getText().toString(), type);
					Toast.makeText(getActivity(), "Appointment Request Sent", Toast.LENGTH_LONG).show();
				}
			}
		});

		Spinner spinner = (Spinner) rootView.findViewById(R.id.Spinner01);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				Object item = parent.getItemAtPosition(pos);
				type = item.toString();
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		return rootView;
	}

	private void showDatePicker() {
		DatePickerFragment date = new DatePickerFragment();

		Calendar calender = Calendar.getInstance();
		Bundle args = new Bundle();
		args.putInt("year", calender.get(Calendar.YEAR));
		args.putInt("month", calender.get(Calendar.MONTH));
		args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
		date.setArguments(args);

		date.setCallBack(ondate);
		date.show(getFragmentManager(), "Date Picker");
	}

	OnDateSetListener ondate = new OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year1, int monthOfYear1, int dayOfMonth1) {
			year = year1;
			monthOfYear = monthOfYear1;
			dayOfMonth = dayOfMonth1;
			b1.setText(String.valueOf(monthOfYear + 1) + "/" + String.valueOf(dayOfMonth) + "/" + String.valueOf(year));
		}
	};

	private void showTimePicker() {
		TimePickerFragment date = new TimePickerFragment();
		/**
		 * Set Up Current Date Into dialog
		 */

		Calendar calender = Calendar.getInstance();
		Bundle args = new Bundle();
		args.putInt("hour", calender.get(Calendar.HOUR));
		args.putInt("minute", calender.get(Calendar.MONTH));
		args.putInt("ampm", calender.get(Calendar.AM));
		date.setArguments(args);
		/**
		 * Set Call back to capture selected date
		 */

		date.setCallBack(onTime);
		date.show(getFragmentManager(), "Time Picker");
	}

	OnTimeSetListener onTime = new OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay1, int minute1) {
			hourOfDay = hourOfDay1;
			minute = minute1;
			String aa = "AM";
			if (hourOfDay > 12) {
				hourOfDay -= 12;
				aa = "PM";
			}
			b2.setText(String.valueOf(hourOfDay) + ":" + String.valueOf(minute) + " " + aa);
		}
	};

	@Override
	public void processFinish(String output) {
		JSONObject jObject;
		String status = "";

		try {
			jObject = new JSONObject(output);
			try {
				status = jObject.getString("status").toString();
			} catch (Exception e) {

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
