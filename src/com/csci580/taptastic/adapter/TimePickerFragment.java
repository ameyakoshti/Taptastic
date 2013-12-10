package com.csci580.taptastic.adapter;


import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;

public class TimePickerFragment extends DialogFragment {
	OnTimeSetListener ondateSet;

	
	public TimePickerFragment() {
	}

	public void setCallBack(OnTimeSetListener ondate) {
		ondateSet = ondate;
	}

	private int hour, minute,am;

	@Override
	public void setArguments(Bundle args) {
		super.setArguments(args);
		hour = args.getInt("hour");
		minute = args.getInt("minute");
		am = args.getInt("ampm");
		
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		if(am==1)
			hour+=12;
		return new TimePickerDialog(getActivity(), ondateSet, hour, minute,false);
	}
}
