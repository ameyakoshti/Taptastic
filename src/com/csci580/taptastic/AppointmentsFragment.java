package com.csci580.taptastic;

import java.util.ArrayList;

import com.csci580.taptastic.R;
import com.tjerkw.slideexpandable.library.ActionSlideExpandableListView;


import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

// Instances of this class are fragments representing a single
// object in our collection.
public class AppointmentsFragment extends ListFragment {
    public static final String ARG_OBJECT = "object";

    @Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
    	View rootView = inflater.inflate(
                R.layout.single_expandable_list, container, false);
    	
        ActionSlideExpandableListView list = (ActionSlideExpandableListView)rootView.findViewById(R.id.list);
        list.setAdapter(buildDummyData());
        
        list.setItemActionListener(new ActionSlideExpandableListView.OnActionClickListener() {
        
			@Override
			public void onClick(View listView, View buttonview, int position) {

				/**
				 * Normally you would put a switch
				 * statement here, and depending on
				 * view.getId() you would perform a
				 * different action.
				 */
				String actionName = "";
				if(buttonview.getId()==R.id.buttonA) {
					actionName = "buttonA";
				} else {
					actionName = "ButtonB";
				}
				/**
				 * For testing sake we just show a toast
				 */
				Toast.makeText(
					getActivity(),
					"Clicked Action: "+actionName+" in list item "+position,
					Toast.LENGTH_SHORT
				).show();
			}

		// note that we also add 1 or more ids to the setItemActionListener
		// this is needed in order for the listview to discover the buttons
		}, R.id.buttonA, R.id.buttonB);
    
        return rootView;
    }
    public class AppointmentItemAdapter extends ArrayAdapter {
		private ArrayList tweets;
		private ArrayList types;
		private int inflateId;
		public AppointmentItemAdapter(Context context, int textViewResourceId,
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
				TextView  tview = (TextView)  v.findViewById(R.id.text);
				if (image != null){
					if(types.get(position).toString().equals("g"))
						image.setImageResource(R.drawable.ic_g);
					else if(types.get(position).toString().equals("p"))
						image.setImageResource(R.drawable.ic_p);
					else if(types.get(position).toString().equals("u"))
						image.setImageResource(R.drawable.ic_u);
					else if(types.get(position).toString().equals("o"))
						image.setImageResource(R.drawable.ic_o);
				}
				tview.setText(tweet);
			}
			return v;
		}
	}

    public ListAdapter buildDummyData() {
		ArrayList<String> values = new ArrayList<String>();
		ArrayList<String> type = new ArrayList<String>();
		type.add("g");
		values.add("\nK Andy\nGeneral Appointment\n12/10/2013 5:00pm");
		type.add("p");
		values.add("\nC Brad\nProject Appointment\n12/10/2013 5:00pm");
		type.add("o");
		values.add("\nA Cook\nOther Appointment\n12/10/2013 5:00pm");
		type.add("u");
		values.add("\nM Daniel\nUrgent Appointment\n12/10/2013 5:00pm");
		type.add("g");
		values.add("\nN Fred\nGeneral Appointment\n12/10/2013 5:00pm");
		
		if(LoginActivity.appMode=="Student"){
			return new AppointmentItemAdapter(
					getActivity(),
					R.layout.expandable_list_item,
					R.id.text,
					values,type
			);
		}
		return new AppointmentItemAdapter(
				getActivity(),
				R.layout.expandable_list_item,
				R.id.text,
				values,type
		);
	}
}

