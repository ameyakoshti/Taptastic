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

public class DisplayAttendanceFragment extends android.app.Fragment {
	public DisplayAttendanceFragment() {
	}
	Button b1;
	Button b2;
	View rootView;
	String dates="";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.attendance, container, false);
		
	
		return rootView;
	}

	
}
