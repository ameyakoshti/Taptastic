package com.csci580.taptastic;

import java.util.ArrayList;
import java.util.List;

import com.csci580.taptastic.R;
import com.tjerkw.slideexpandable.library.ActionSlideExpandableListView;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class CoursesFragment extends Fragment {

	public CoursesFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.single_expandable_list,
				container, false);

		ActionSlideExpandableListView list = (ActionSlideExpandableListView) rootView
				.findViewById(R.id.list);
		list.setAdapter(buildDummyData());
		if(LoginActivity.appMode=="Professor")
		list.setItemActionListener(
				new ActionSlideExpandableListView.OnActionClickListener() {

					@Override
					public void onClick(View listView, View buttonview,
							int position) {

						/**
						 * Normally you would put a switch statement here, and
						 * depending on view.getId() you would perform a
						 * different action.
						 */
						Fragment attendance=new DisplayAttendanceFragment();
						ArrayList<String> studentname=new ArrayList<String>();
						ArrayList<String> studentattednace=new ArrayList<String>();
						int numberOfStudents = 11;
						String actionName = "";
						if (buttonview.getId() == R.id.button1) {
							actionName = "Fall 2013";
							//populate name and attendance
						} else if  (buttonview.getId() == R.id.button2){
							actionName = "Summer 2013";
							//populate name and attendance
						} else {
							actionName = "Spring 2013";
						//	populate name and attendance
						}
						//Dummy List.
						studentname.add("Sir Alex Fergusson"); studentattednace.add("10");
						studentname.add("Ryan Giggs"); studentattednace.add("10");
						studentname.add("Wayne Rooney"); studentattednace.add("10");
						studentname.add("Nemanja Vidic"); studentattednace.add("10");
						studentname.add("Micheal Carrick"); studentattednace.add("10");
						studentname.add("Robin Van Persie"); studentattednace.add("10");
						studentname.add("Rio Ferdinand"); studentattednace.add("10");
						studentname.add("shris Smalling"); studentattednace.add("10");
						studentname.add("Shinji Kagawa"); studentattednace.add("10");
						studentname.add("David De Gea"); studentattednace.add("10");
						studentname.add("Tom Cleverly"); studentattednace.add("10");
						((DisplayAttendanceFragment) attendance).setArguments1(numberOfStudents,studentname,studentattednace);
						((MainActivity)getActivity()).callfragment(actionName, attendance);
						
					}

					// note that we also add 1 or more ids to the
					// setItemActionListener
					// this is needed in order for the listview to discover the
					// buttons
				}, R.id.button1, R.id.button2,R.id.button3);

		return rootView;

	}

	public class CourseItemAdapter extends ArrayAdapter {
		private ArrayList tweets;
		private ArrayList types;
		private int inflateid;
		public CourseItemAdapter(Context context, int textViewResourceId,
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
			if (tweet != null) {
				
				ImageView image = (ImageView) v.findViewById(R.id.imageView1);
				TextView  tview = (TextView)  v.findViewById(R.id.text);
				if (image != null){
					if(types.get(position).toString().equals("588"))
						image.setImageResource(R.drawable.ic_588);
					else if(types.get(position).toString().equals("580"))
						image.setImageResource(R.drawable.ic_580);
				}
				tview.setText(tweet);
				if (LoginActivity.appMode == "Student") {
					tview = (TextView)  v.findViewById(R.id.textView1);
					TextView	tview1 = (TextView)  v.findViewById(R.id.professorName);
					TextView	tview2 = (TextView)  v.findViewById(R.id.taName);
					TextView	tview3 = (TextView)  v.findViewById(R.id.pofficehours);
					TextView	tview4 = (TextView)  v.findViewById(R.id.taofficehorus);
					TextView	tview5 = (TextView)  v.findViewById(R.id.website);
					if(types.get(position).toString().equals("588")){
						tview.setText("CSCI 588: Design and Specifications of User Interfaces");
						tview1.setText("Suya You");
						tview2.setText("Vaibhav Agarwal");
						tview3.setText("Tuesday, 4:00pm - 6:00pm");
						tview4.setText("Tuesday, 10:00am- 12:00pm");
						tview5.setText("www.uscden.net");
					}
					else if(types.get(position).toString().equals("580")){
						tview.setText("CSCI 580: 3D Graphics and Rendering");
						image.setImageResource(R.drawable.ic_580);
						tview1.setText("Ulrich Neumann");
						tview2.setText("Zhenzhen Gao");
						tview3.setText("Thursday, 4:00pm - 6:00pm");
						tview4.setText("Friday, 10:00am- 12:00pm");
						tview5.setText("www.blackboard.usc.edu");
					}
				}

			}
			return v;
		}
	}

	public ListAdapter buildDummyData() {
		ArrayList<String> values = new ArrayList<String>();
		ArrayList<String> type	 = new ArrayList<String>();
		values.add("\nCSCI 588: Design and Specification\nof User Interfaces\nTuesday : 6:40pm-9:20pm");
		type.add("588");
		values.add("\nCSCI 580: 3D Graphics and Rendering\nTuesday : 2:00pm-3:20pm\nThursday : 2:00pm-3:20pm");
		type.add("580");
		
		if (LoginActivity.appMode == "Student") {
			return new CourseItemAdapter(getActivity(),
					R.layout.expandable_list_item_courses, R.id.text, values,type);
		}
		return new CourseItemAdapter(getActivity(),
				R.layout.expandable_list_item_coursep, R.id.text, values,type);
	}
}
