package com.csci580.taptastic;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class DisplayAttendanceFragment extends android.app.Fragment {
	int numberOfStudents;
	ArrayList<String> studentName;
	ArrayList<String> attendance;
	public DisplayAttendanceFragment() {
	
	}
	public void setArguments1(int a,ArrayList<String> s1,ArrayList<String> s2){
		numberOfStudents=a;
		studentName=s1;
		attendance=s2;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.attendance, container, false);
		TextView text=(TextView)rootView.findViewById(R.id.textView1);
		text.setText("Number of Students: "+numberOfStudents);
		ListView list2 = (ListView) rootView.findViewById(R.id.list);
		list2.setAdapter(new AttendanceItemAdapter(getActivity(),
				R.layout.attedancelist, R.id.text, studentName,attendance));
		return rootView;
	}
	public class AttendanceItemAdapter extends ArrayAdapter {
		private ArrayList tweets;
		private ArrayList types;
		private int inflateid;
		public AttendanceItemAdapter(Context context, int textViewResourceId,
				int text,ArrayList tweets,ArrayList types) {
			super(context, textViewResourceId,text, tweets);
			this.tweets = tweets;
			this.types=types;
			this.inflateid=textViewResourceId;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(inflateid, null);
			}

			String tweet = tweets.get(position).toString();
			((TextView) v.findViewById(R.id.textView1)).setText(tweet);
			tweet=types.get(position).toString();
			((TextView) v.findViewById(R.id.textView2)).setText(tweet);
			return v;
		}
	}

	}
