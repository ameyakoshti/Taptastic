package com.csci580.taptastic;

import java.util.Calendar;

import com.csci580.taptastic.adapter.DatePickerFragment;
import com.csci580.taptastic.adapter.TimePickerFragment;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

public class NewAppointment extends android.app.Fragment {
	public NewAppointment() {
	}
	Button b1;
	Button b2;
	View rootView;
	String dates="";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.new_appointment, container, false);
		b1= (Button) rootView.findViewById(R.id.button1);
		Calendar calendar=Calendar.getInstance();
		b1.setText((calendar.get(Calendar.MONTH)+1)+"/"+calendar.get(Calendar.DAY_OF_MONTH)+"/"+calendar.get(Calendar.YEAR));
		b2= (Button) rootView.findViewById(R.id.button2);
		if(calendar.get(Calendar.AM)==1)
			b2.setText(calendar.get(Calendar.HOUR)+":"+calendar.get(Calendar.MINUTE)+" PM");
		else
			b2.setText(calendar.get(Calendar.HOUR)+":"+calendar.get(Calendar.MINUTE)+" AM");
		b1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDatePicker();
	
			}
		});
		b2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showTimePicker();
	
			}
		});
	
		return rootView;
	}

	private void showDatePicker() {
		DatePickerFragment date = new DatePickerFragment();
		/**
		 * Set Up Current Date Into dialog
		 */
		
		Calendar calender = Calendar.getInstance();
		Bundle args = new Bundle();
		args.putInt("year", calender.get(Calendar.YEAR));
		args.putInt("month", calender.get(Calendar.MONTH));
		args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
		date.setArguments(args);
		/**
		 * Set Call back to capture selected date
		 */
		
		date.setCallBack(ondate);
		date.show(getFragmentManager(), "Date Picker");
	}

	OnDateSetListener ondate = new OnDateSetListener() {
	
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			b1.setText(String.valueOf(monthOfYear+1)+"/"+ String.valueOf(dayOfMonth)+"/"+String.valueOf(year));
			Toast.makeText(
					getActivity(),
					String.valueOf(year) + "-" + String.valueOf(monthOfYear)
							+ "-" + String.valueOf(dayOfMonth),
					Toast.LENGTH_LONG).show();
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
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// TODO Auto-generated method stub
			String aa="AM";
			if(hourOfDay>12)
			{
				hourOfDay-=12;
				aa="PM";
			}
			b2.setText(String.valueOf(hourOfDay)+":"+ String.valueOf(minute)+" "+aa);
			
		}
	};

}
