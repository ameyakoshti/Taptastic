package com.csci580.taptastic;

import java.util.ArrayList;

import com.csci580.taptastic.R;
import com.csci580.taptastic.AppointmentsFragment.AppointmentItemAdapter;
import com.tjerkw.slideexpandable.library.ActionSlideExpandableListView;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

public class AnnouncmentsFragments extends Fragment {
	
	public AnnouncmentsFragments(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.single_expandable_list, container, false);
        ActionSlideExpandableListView list = (ActionSlideExpandableListView)rootView.findViewById(R.id.list);
        list.setAdapter(buildDummyData());
        return rootView;
    }
	public class AnnoucementstItemAdapter extends ArrayAdapter {
		private ArrayList tweets;
		private ArrayList types;
		private int inflateId;
		public AnnoucementstItemAdapter(Context context, int textViewResourceId,
				int text,ArrayList tweets,ArrayList types) {
			super(context, textViewResourceId,text, tweets);
			this.tweets = tweets;
			this.types=types;
			this.inflateId=textViewResourceId;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(inflateId, null);
			}

			String tweet = tweets.get(position).toString();
			if (tweet != null) {
				
				ImageView image = (ImageView) v.findViewById(R.id.imageView1);
				TextView  tview2 = (TextView)  v.findViewById(R.id.text);
				tview2.setText(tweet);
				TextView  tview = (TextView)  v.findViewById(R.id.textView1);
				TextView  tview1 = (TextView)  v.findViewById(R.id.textView2);
				if (image != null){
					if(types.get(position).toString().equals("588"))
						image.setImageResource(R.drawable.ic_588);
					else if(types.get(position).toString().equals("580"))
						image.setImageResource(R.drawable.ic_580);

				}
				if(tweet.equals("\nProject Submission Due Date Changed")){
				tview.setText("\nProject Submission date has been changed to 12/31/2020");
				tview1.setText("Time Posted: 12/10/2013 6:00pm");
				}
				if(tweet.equals("\nFinal Exams Preponed")){
					tview.setText("\nFinal Exam preponed to 12/10/2013");
					tview1.setText("Time Posted: 12/10/2013 6:00am");
				}
				if(tweet.equals("\nHolidays Extended")){
					tview.setText("\nHolidays has been extended until further notice");
					tview1.setText("Time Posted: 12/8/2013 6:00pm");
				}
			}
			return v;
		}
	}

	public ListAdapter buildDummyData() {
		ArrayList<String> values = new ArrayList<String>();
		ArrayList<String> type = new ArrayList<String>();
		type.add("588");
		values.add("\nProject Submission Due Date Changed");
		type.add("580");
		values.add("\nFinal Exams Preponed");
		type.add("588");
		values.add("\nHolidays Extended");
		
		
		
		return new AnnoucementstItemAdapter(
				getActivity(),
				R.layout.expandable_list_annoucements,
				R.id.text,
				values,type
		);
	}
}
