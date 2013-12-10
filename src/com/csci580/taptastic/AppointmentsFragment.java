package com.csci580.taptastic;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.tjerkw.slideexpandable.library.ActionSlideExpandableListView;

// Instances of this class are fragments representing a single
// object in our collection.
public class AppointmentsFragment extends ListFragment implements AsyncResponse {
	public static final String ARG_OBJECT = "object";
	View rootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.single_expandable_list, container, false);

		ServletCalls Appointments = new ServletCalls();
		Appointments.delegate = AppointmentsFragment.this;
		Appointments.execute("7");

		return rootView;
	}

	public class AppointmentItemAdapter extends ArrayAdapter {
		private ArrayList tweets;
		private ArrayList types;
		private int inflateId;

		public AppointmentItemAdapter(Context context, int textViewResourceId, int text, ArrayList tweets, ArrayList types) {
			super(context, textViewResourceId, text, tweets);
			this.tweets = tweets;
			this.types = types;
			this.inflateId = textViewResourceId;
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
				TextView tview = (TextView) v.findViewById(R.id.text);
				if (image != null) {
					if (types.get(position).toString().equals("g"))
						image.setImageResource(R.drawable.ic_g);
					else if (types.get(position).toString().equals("p"))
						image.setImageResource(R.drawable.ic_p);
					else if (types.get(position).toString().equals("o"))
						image.setImageResource(R.drawable.ic_o);
					else if (types.get(position).toString().equals("u"))
						image.setImageResource(R.drawable.ic_u);
				}
				tview.setText(tweet);
			}
			return v;
		}
	}

	public ListAdapter buildDummyData(String servletResponse) {

		ArrayList<String> values = new ArrayList<String>();
		ArrayList<String> type = new ArrayList<String>();
		JSONObject jObject;
		JSONArray posts;

		try {
			jObject = new JSONObject(servletResponse);
			try {
				posts = jObject.getJSONArray("appointments");
				for (int i = 0; i < posts.length(); i++) {
					if (posts.getJSONObject(i).get("type").equals("General"))
						type.add("g");
					if (posts.getJSONObject(i).get("type").equals("Project"))
						type.add("p");
					if (posts.getJSONObject(i).get("type").equals("Other"))
						type.add("o");
					if (posts.getJSONObject(i).get("type").equals("Urgent"))
						type.add("u");

					// String id = posts.getJSONObject(i).get("id").toString();
					String info = posts.getJSONObject(i).get("info").toString();
					String ts = posts.getJSONObject(i).get("ts").toString();

					values.add("\nRyan Giggs\n" + info + "\n" + ts);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (LoginActivity.appMode == "Student") {
			return new AppointmentItemAdapter(getActivity(), R.layout.expandable_list_item, R.id.text, values, type);
		}
		return new AppointmentItemAdapter(getActivity(), R.layout.expandable_list_item, R.id.text, values, type);
	}

	@Override
	public void processFinish(String output) {
		ActionSlideExpandableListView list = (ActionSlideExpandableListView) rootView.findViewById(R.id.list);
		list.setAdapter(buildDummyData(output));
		list.setItemActionListener(new ActionSlideExpandableListView.OnActionClickListener() {

			@Override
			public void onClick(View listView, View buttonview, int position) {

				/**
				 * Normally you would put a switch statement here, and depending
				 * on view.getId() you would perform a different action.
				 */
				String actionName = "";
				if (buttonview.getId() == R.id.buttonA) {
					actionName = "buttonA";
				} else {
					actionName = "ButtonB";
				}
				/**
				 * For testing sake we just show a toast
				 */
				Toast.makeText(getActivity(), "Clicked Action: " + actionName + " in list item " + position, Toast.LENGTH_SHORT).show();
			}

			// note that we also add 1 or more ids to the setItemActionListener
			// this is needed in order for the listview to discover the buttons
		}, R.id.buttonA, R.id.buttonB);
	}
}
